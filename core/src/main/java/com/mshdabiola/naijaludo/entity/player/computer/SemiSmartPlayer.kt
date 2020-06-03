package com.mshdabiola.naijaludo.entity.player.computer

import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.player.BasePlayer

class SemiSmartPlayer(id: Int, gameColor: IntArray, name: String = "SemiSmart") : ComputerPlayerBase(name, id, gameColor) {

    constructor() : this(0, intArrayOf(), "")

    override fun checkBestMove(diceIndex: Int) {
        val diceNo: Int = diceController.getDiceValue(diceIndex)
        if (diceNo == 0) {
            return
        }
        list = gameController.getNewPosition(diceNo)
        bestValueSeed = -1f
        for (seed in list) {
            val value = seedAnalyse(seed)

            if (value > bestValueSeed) {

                bestValueSeed = value
                bestSeed = changeSeed(seed)!!
            }
        }
    }

    override fun seedAnalyse(seed: Seed): Float {
        val players: Array<BasePlayer> = gameController.players
        for (player in players) {
            if (player !== gameController.currentPlayer) {
                for (otherPlayerSeed in player.homeSeed) {
                    if (seed.isAtSameFloor(otherPlayerSeed)) {
                        return 5 + random.nextFloat()
                    }
                }
                return if (gameController.isAboutToFinish(seed)) {
                    1 + random.nextFloat()
                } else if (gameController.isStatingPoint(seed)) {
                    3 + random.nextFloat()
                } else {
                    2 + random.nextFloat()
                }
            }
        }
        return 0f
    }

    override fun diceIndexAnalyse(index: Int): Float {
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
                            return 5 + random.nextFloat()
                        }
                    }
                }
                //check at home
                for (seed in list) {
                    if (gameController.isStatingPoint(seed)) {
                        return 3 + random.nextFloat()
                    }
                }
                return 2f
            }
        }
        return 0f
    }

}