package com.mshdabiola.naijaludo.screen.game.logic

import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.screen.game.DiceController
import com.mshdabiola.naijaludo.screen.game.GameController
import com.mshdabiola.naijaludo.screen.game.GameLogic

open class NewGameLogic(
        players: Array<BasePlayer>,
        gameController: GameController = GameController(),
        diceController: DiceController = DiceController()
) : GameLogic(players, gameController, diceController) {


    constructor() : this(emptyArray())
    constructor(gameLogic: GameLogic) : this(gameLogic.players, gameLogic.gameController, gameLogic.diceController)


}