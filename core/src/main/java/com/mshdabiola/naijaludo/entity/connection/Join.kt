package com.mshdabiola.naijaludo.entity.connection

import com.mshdabiola.naijaludo.entity.player.HumanPlayer
import kotlinx.coroutines.channels.SendChannel

class Join(val humanPlayer: HumanPlayer) {
    val client = GameClient(humanPlayer)

    fun connect(ipAddress: String = "localhost") {
        client.connectIP(ipAddress)
    }

    fun disconnected() {
        client.stop()
    }

    fun setPlayerListUpdate(actor: SendChannel<Factory.Message>) {
        client.factory.updateActor = actor
    }
}