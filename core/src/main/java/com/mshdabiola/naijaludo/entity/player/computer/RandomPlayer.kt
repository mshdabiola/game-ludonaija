package com.mshdabiola.naijaludo.entity.player.computer

import com.badlogic.gdx.math.MathUtils
import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.player.BasePlayer

class RandomPlayer(id: Int, gameColor: IntArray, name: String = "Random") : ComputerPlayerBase(name, id, gameColor) {

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
            if (bestValueSeed < 3) {
                val randomNo = MathUtils.random(list.size - 1)
                val seed = changeSeed(list[randomNo])!!

                bestSeed = seed
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
                return if (gameController.isStatingPoint(seed)) {
                    4 + random.nextFloat()
                } else {
                    2 + random.nextFloat()
                }
            }
        }
        return 0f
    }

}