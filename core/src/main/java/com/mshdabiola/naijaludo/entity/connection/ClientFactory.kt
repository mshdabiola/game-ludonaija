package com.mshdabiola.naijaludo.entity.connection

import com.mshdabiola.naijaludo.screen.game.GameController

class ClientFactory(gameController: GameController) : Factory(gameController) {

    fun sendPlayerName(playerName: String): String {


        val playerName = PlayerName(playerName)
        val jsonP = jsonPool.obtain()
        val str = jsonP.toJson(playerName)
        jsonPool.free(jsonP)

        return str
    }
}