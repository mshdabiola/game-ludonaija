package com.mshdabiola.naijaludo.screen.game.logic

import com.mshdabiola.naijaludo.entity.connection.Factory
import com.mshdabiola.naijaludo.entity.connection.GameClient
import com.mshdabiola.naijaludo.screen.game.GameController

class ClientGameController(val client: GameClient) : GameController() {

    private var prevState = currentState

    var send = false

    override fun toss() {
        super.toss()
        send = true
    }

    override fun hasToss() {
        sendString(Factory.diceValueToJson(this))

        super.hasToss()

    }

    override fun chooseDice() {
        super.chooseDice()
        send = true
    }

    override fun hasChooseDice() {

        sendString(Factory.currentDiceIndexToJson(this))

        super.hasChooseDice()

    }

    override fun chooseSeed() {
        super.chooseSeed()
        send = true
    }

    override fun hasChooseSeed() {
        sendString(Factory.currentSeedToSendJson(this))

        super.hasChooseSeed()

    }

    fun sendString(string: String) {
        if (currentPlayerIndex == playerId && send) {
            client.sendString(string)
            send = false

        }
    }

}