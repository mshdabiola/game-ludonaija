package com.mshdabiola.naijaludo.screen.game

import com.mshdabiola.naijaludo.config.GameState
import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.entity.player.HumanPlayer
import com.mshdabiola.naijaludo.entity.player.computer.ComputerPlayerBase


open class GameLogic(
        var players: Array<BasePlayer>,
        var gameController: GameController = GameController(),
        var diceController: DiceController = DiceController()
) {
    constructor() : this(emptyArray())

    val name = this::class.java.name
    val playerScore = HashMap<Int, Int>(players.size)

    var update = false
    var finished = false
//    var tableDegree=-90f

    init {
        gameController.diceController = diceController
        gameController.players = players
        if (players.isNotEmpty())
            gameController.currentPlayer = players[gameController.currentPlayerIndex]
        players.forEach { playerScore[it.id] = 0 }
    }

    fun update() {
        gameController.diceController = diceController
        gameController.players = players
        gameController.currentPlayer = players[gameController.currentPlayerIndex]
        if (gameController.currentPlayer is ComputerPlayerBase && gameController.currentState == GameState.TOSS) {
            (gameController.currentPlayer as ComputerPlayerBase).isToss = false
        }

    }

    fun reset(oldlayers: Array<BasePlayer>) {
        gameController.apply {
            currentState = GameState.PLAY
            currentPlayerIndex = 0
        }
        diceController = DiceController()
        players = oldlayers



        when (players.size) {
            2 -> {
                val color0 = players[0].gamecolorsId
                players[0].gamecolorsId = players[1].gamecolorsId
                players[1].gamecolorsId = color0
            }
            3 -> {
                val color0 = players[0].gamecolorsId
                val color1 = players[1].gamecolorsId
                val color2 = players[2].gamecolorsId
                players[0].gamecolorsId = color1
                players[1].gamecolorsId = color2
                players[2].gamecolorsId = color0
            }
            4 -> {
                val color0 = players[0].gamecolorsId
                val color1 = players[1].gamecolorsId
                val color2 = players[2].gamecolorsId
                val color3 = players[3].gamecolorsId
                players[0].gamecolorsId = color1
                players[1].gamecolorsId = color2
                players[2].gamecolorsId = color3
                players[3].gamecolorsId = color0
            }
        }
        players.forEach {
            it.lastPoint = 0
            it.seedOut.clear()
            if (it is HumanPlayer) {
                it.manySeedTable.clear()
            }
//            it.playerPanel = PlayerPanel(it)

        }
        gameController.diceController = diceController
//        (players[gameController.playerId] as HumanPlayer).lastPoint = 0
        gameController.players = players
        finished = false
    }

}