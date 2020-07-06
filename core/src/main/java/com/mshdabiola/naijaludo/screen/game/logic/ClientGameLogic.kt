package com.mshdabiola.naijaludo.screen.game.logic

import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.screen.game.DiceController
import com.mshdabiola.naijaludo.screen.game.GameController
import com.mshdabiola.naijaludo.screen.game.GameLogic

class ClientGameLogic(
        players: Array<BasePlayer>,
        gameController: GameController = GameController(),
        diceController: DiceController = DiceController()
) : GameLogic(players, gameController, diceController)