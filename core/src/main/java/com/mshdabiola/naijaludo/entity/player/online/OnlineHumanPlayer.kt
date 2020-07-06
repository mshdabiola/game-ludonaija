package com.mshdabiola.naijaludo.entity.player.online

import com.mshdabiola.naijaludo.entity.connection.GameClient
import com.mshdabiola.naijaludo.entity.player.HumanPlayer

class OnlineHumanPlayer(id: Int, gameColor: IntArray, name: String) : HumanPlayer(id, gameColor, name) {

    val client = GameClient(this)
//    var updateActor: SendChannel<Factory.Message>? = null

    fun connectClient(ip: String = "localhost") {
        client.connectIP(ip)
    }


}