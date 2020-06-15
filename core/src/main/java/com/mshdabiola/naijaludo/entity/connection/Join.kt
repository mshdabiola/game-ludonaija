package com.mshdabiola.naijaludo.entity.connection

import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.mshdabiola.naijaludo.asset.MassetDescriptor
import com.mshdabiola.naijaludo.entity.player.HumanPlayer
import kotlinx.coroutines.channels.SendChannel
import ktx.actors.onClick

class Join(val humanPlayer: HumanPlayer) {
    val client = lazy { GameClient(humanPlayer) }
    var lastIpAddress: String? = null


    val reConnect = lazy {
        TextButton("Reconnect", MassetDescriptor.purpleSkinn, "red").apply {
            isVisible = false
            onClick {
                lastIpAddress?.let {
                    connect(it)
                }
            }
        }
    }


    fun connect(ipAddress: String = "localhost") {
        lastIpAddress = ipAddress
        reConnect.value.isVisible = false
        try {
            client.value.connectIP(ipAddress)
        } catch (e: Exception) {
            reConnect.value.isVisible = true

        }

    }

    fun disconnected() {
        client.value.stop()
    }

    fun setPlayerListUpdate(actor: SendChannel<Factory.Message>) {
        client.value.clientFactory.updateActor = actor
    }
}