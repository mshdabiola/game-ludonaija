package com.mshdabiola.naijaludo.entity.player.computer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Logger
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.config.GameState
import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.player.BasePlayer
import java.security.SecureRandom


abstract class ComputerPlayerBase(name: String = "Robot", id: Int, gamecolors: IntArray, isOnlineGame: Boolean = false) : BasePlayer(name, id, gamecolors, isOnlineGame) {

    @Transient
    open lateinit var logger: Logger

    init {

        try {
            if (!Config.isTest) {
                logger = Logger(ComputerPlayerBase::class.java.name, Logger.DEBUG)
            }
        } catch (e: Exception) {

        }
    }

    protected lateinit var bestSeed: Seed
    protected var bestDiceNo = 0
    protected var bestDiceIndex = -1
    var timer = 0f
    var timeToStop = 0.4f
    var bestValueDiceIndex = -1.0f
    var bestValueSeed = -1.0f


    var lastDiceIndex = 0
    var lastDiceNo = 0

    @Transient
    var random = SecureRandom()

    var isToss = false


    override fun chooseDice() {
        timer += Gdx.graphics.deltaTime
        if (timer >= timeToStop) {
            timer = 0f
            getBestDiceIndexTest()
            gameController.currentDiceIndex = bestDiceIndex
            gameController.currentDiceNo = bestDiceNo
            //  lastDice[bestDiceIndex.toFloat()] = bestDiceNo.toFloat()
//            (lastDiceIndex) = Pair(bestDiceIndex, bestDiceNo)
            lastDiceIndex = bestDiceIndex
            lastDiceNo = bestDiceNo

            if (bestDiceIndex == 2)
                logger.debug("choose 2 value is $bestDiceNo")




            gameController.currentState = GameState.HASCHOOSEDICE

            clearList()
        }

    }

    override fun chooseSeed() {
        timer += Gdx.graphics.deltaTime
        if (timer >= timeToStop) {
            timer = 0f
            checkBestMove(bestDiceIndex)
            if (this::bestSeed.isInitialized) {
                bestSeed.actor.toFront()
                gameController.currentSeed = bestSeed

//                gameController.currentState = GameState.HASCHOOSESEED

                gameController.currentState = GameState.HASCHOOSESEED


            } else {
//                gameController.currentState = GameState.CHOOSEDICE

                gameController.currentState = GameState.CHOOSEDICE

            }

            clearList()
        }

    }

    override fun toss() {
        timer += Gdx.graphics.deltaTime
        if (timer >= timeToStop) {
            timer = 0f
            if (!isToss) {
                isToss = true
                diceController.toss {

                    gameController.currentState = GameState.HASTOSS

                    clearList()
                    isToss = false
                }

            }
        }

    }

    fun clearList() {
        list.forEach { Seed.seedPool.free(it) }
        list.clear()
    }

    abstract fun checkBestMove(diceIndex: Int)

    abstract fun seedAnalyse(seed: Seed): Float

    fun changeSeed(s: Seed): Seed? {
        for (seed in homeSeed) {
            if (seed.id == s.id && seed.color == s.color) {
                return seed
            }
        }
        return null
    }

    fun getBestDiceIndexTest() {
        bestValueDiceIndex = -1f
        for (i in 0..2) {
            val diceNo: Int = diceController.getDiceValue(i)
            if (diceNo == 0) {
                continue
            }
            val value = processor(i)

            if (value > bestValueDiceIndex) {

                bestValueDiceIndex = value
                bestDiceIndex = i
                bestDiceNo = diceNo
            }
        }
    }

    open fun diceIndexAnalyse(index: Int): Float {
        val diceNo: Int = diceController.getDiceValue(index)
        if (diceNo == 0) {
            return 0f
        }
        list = gameController.getNewPosition(diceNo)
        for (player in gameController.players) {
            if (player !== gameController.currentPlayer) { //check last choose dice

                if (Pair(lastDiceIndex, lastDiceNo) == Pair(index, diceNo)) {
                    return 0.5f
                }
                //check where the dice kill
                for (otherPlayerSeed in player.homeSeed) {
                    for (seed in list) {
                        if (seed.isAtSameFloor(otherPlayerSeed)) {
                            return 1 + random.nextFloat()
                        }
                    }
                }
                //check at home
                for (seed in list) {
                    if (gameController.isStatingPoint(seed)) {
                        return 4 + random.nextFloat()
                    }
                }
                return 1.toFloat()
            }
        }
        list.forEach { Seed.seedPool.free(it) }
        return 0.toFloat()
    }

    fun processor(diceIndex: Int): Float {
        val diceNo: Int = diceController.getDiceValue(diceIndex)

        //dice number is equal to zero
        if (diceNo == 0) {
            return 0f
        }

        //check if is last dice
        if (Pair(lastDiceIndex, lastDiceNo) == Pair(diceIndex, diceNo)) {
            return 0f
        }

        val otherplayerSeed = gameController.players
                .filter { it != gameController.currentPlayer }
                .flatMap { it.homeSeed }
                .filter { !gameController.isHome(it) }

        list = gameController.getNewPosition(diceNo)
        //check if it kill opponent
        list
                .forEach foreach@{ firstSeed ->
                    otherplayerSeed
                            .firstOrNull { it.isAtSameFloor(firstSeed) }
                            ?.let {


                                return 4 + random.nextFloat()
                            }

                }




        list
                .firstOrNull { gameController.isStatingPoint(it) }
                ?.let {

                    return 5 + random.nextFloat()
                }

        //is only one seed
        if (diceIndex == 2 && list.count() == 1) {
            return 3 + random.nextFloat()
        }

        return 0.3f + random.nextFloat()

    }


}