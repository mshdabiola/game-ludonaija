package com.mshdabiola.naijaludo.entity

import com.mshdabiola.naijaludo.config.GameManager
import com.mshdabiola.naijaludo.config.PlayType
import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.entity.player.HumanPlayer
import com.mshdabiola.naijaludo.entity.player.computer.*
import com.mshdabiola.naijaludo.screen.game.GameLogic
import com.mshdabiola.naijaludo.screen.game.logic.SaveGameLogic
import kotlin.random.Random

class SavedGameGenerator {
    val pathArray = arrayOf(
            "a", "fa", "prs", "rf", "sr",
            "ap", "frp", "ps", "r", "sap",
            "apf", "fp", "psf", "rp", "sfa",
            "as", "far", "pa", "rps", "s",
            "asr", "f", "p", "rpa", "sf"

    )


    fun getPlayer(startName: String): BasePlayer {
        return when (startName) {

            "a" -> {
                AggressivePlayer(0, intArrayOf())
            }
            "s" -> {
                SemiSmartPlayer(0, intArrayOf())
            }
            "f" -> {
                FifoPlayer(0, intArrayOf())
            }
            "p" -> {
                PacifisticPlayer(0, intArrayOf())
            }
            "r" -> {
                RandomPlayer(0, intArrayOf())
            }
            else -> HumanPlayer(0, intArrayOf())
        }
    }


    fun getGameLogic(level: Int): GameLogic {
        val levelModule = level % 25
        val stringNames = pathArray[levelModule]
        val playerNames = "h$stringNames"
        val numberOfPlayer = playerNames.length
        val colors = getColors(numberOfPlayer)
        val pawnFloors = getRandomArray(numberOfPlayer, level)

        val arrayOfPlayer = Array(numberOfPlayer) {
            val player = getPlayer("${playerNames[it]}")
            player.id = it
            player.gamecolorsId = colors[it]
            if (it != 0) {
                player.iconId = changePlayerToIcon(player)
            } else {
                player.iconId = GameManager.avatar
            }

            player
        }

        return SaveGameLogic(arrayOfPlayer).apply { seedPositions = pawnFloors }

    }


    fun getRandomArray(noOfPlayer: Int, level: Int): IntArray {
        val range = 55
        val random = Random(level)
        val random2 = Random(level * 10000)
        val humanRange = range - ((range + 1) % range)
        val noOfSeed = if (noOfPlayer == 2) 16 else noOfPlayer * 4


        return IntArray(noOfSeed) {


            if (noOfPlayer == 2) {
                if (it in 0..7) {
                    random2.nextInt(0, humanRange)
                } else {
                    random.nextInt(0, range)
                }
            } else {
                if (it in 0..3) {
                    random2.nextInt(0, humanRange)
                } else {
                    random.nextInt(0, range)
                }
            }
        }
    }


    fun changePlayerToIcon(player: BasePlayer): Int {
        return when (player) {
            is RandomPlayer -> {
                PlayType.RANDOM.ordinal + 1
            }
            is FifoPlayer -> {
                PlayType.FIFO.ordinal + 1
            }
            is PacifisticPlayer -> {
                PlayType.PACIFISTIC.ordinal + 1
            }
            is AggressivePlayer -> {
                PlayType.AGGRESSIVE.ordinal + 1
            }
            is SemiSmartPlayer -> {
                PlayType.SEMISMART.ordinal + 1
            }
            else -> {
                7
            }
        }
    }

    fun getColors(playerSize: Int): Array<IntArray> {
        return when (playerSize) {
            1 -> {
                arrayOf(intArrayOf(0, 1, 2, 3))
            }
            2 -> {

                arrayOf(intArrayOf(0, 2), intArrayOf(1, 3))

            }
            3 -> {
                arrayOf(intArrayOf(0), intArrayOf(1), intArrayOf(2))
            }
            else -> {
                arrayOf(intArrayOf(0), intArrayOf(1), intArrayOf(2), intArrayOf(3))
            }
        }
    }
}