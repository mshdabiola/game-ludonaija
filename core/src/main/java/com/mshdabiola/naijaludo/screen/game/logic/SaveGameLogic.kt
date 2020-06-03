package com.mshdabiola.naijaludo.screen.game.logic

import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.screen.game.DiceController
import com.mshdabiola.naijaludo.screen.game.GameController
import com.mshdabiola.naijaludo.screen.game.GameLogic

class SaveGameLogic(
        players: Array<BasePlayer>,
        gameController: GameController = GameController(),
        diceController: DiceController = DiceController()
) : GameLogic(players, gameController, diceController) {
//    init {
//
//        players.flatMap { it.homeSeed }.forEach { it.update() }
//        players.forEach { it.update() }
//        players.flatMap { it.seedOut }.forEach { players[it.playerId].playerPlayer.addSeedOut(it) }
//    }

    constructor(gameLogic: GameLogic) : this(gameLogic.players, gameLogic.gameController, gameLogic.diceController)
}