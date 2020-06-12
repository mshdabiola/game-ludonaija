package com.mshdabiola.naijaludo.entity.connection

import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.entity.player.OnlinePlayer
import com.mshdabiola.naijaludo.screen.game.GameController

class ServerFactory(gameController: GameController) : Factory(gameController) {


    init {
        isServer = true
    }


    fun changePlayerToOnlinePlayer(player: BasePlayer): OnlinePlayer {
        return OnlinePlayer(player.id, player.gamecolorsId, player.name).apply {

            iconId = player.iconId
        }
    }


    fun addOnlinePlayer(id: Int) {
        addPlayer(OnlinePlayer(id, intArrayOf(), ""))
    }

    fun addOnlinePlayer(id: Int, playerName: String) {
        addPlayer(OnlinePlayer(id, intArrayOf(), playerName))
    }

    fun getPlayerIndex() = playerArray.size - 1
    fun setNameOnPlayer(id: Int, name: String) {
        println("setNameOnPlayer id $id and Name $name")

        playerArray[id].name = name
    }

    fun addLocalPlayer(player: BasePlayer): BasePlayer {

        addPlayer(player)
        return playerArray[playerArray.size - 1]
    }

    fun addPlayer(player: BasePlayer) {

        println("add player ${player.id}")
        if (playerArray.size == 4) {
            return
        }
        playerArray.add(player)
        reFreshList()
    }

    fun reFreshList() {
        val playerSize = playerArray.size
        val colors = getColors(playerSize)
        playerArray.forEachIndexed { index, basePlayer ->
            basePlayer.id = index

            basePlayer.gamecolorsId = colors[index]
        }

    }

    fun removePlayer(removeIndex: Int) {

        playerArray.removeAt(removeIndex)
        reFreshList()
    }

    fun sendAllPlayerNew(): String {
        val players = playerArray.map { changePlayerToOnlinePlayer(it) }

        val sendPlayer = SendPlayers(players)
        val jsonP = jsonPool.obtain()
        val str = jsonP.toJson(sendPlayer)
        jsonPool.free(jsonP)
//        println("Send all player $str")
        return str
    }

    fun sendPlayerId(playerId: Int): String {


        val playerId = PlayerId(playerId)
        val jsonP = jsonPool.obtain()
        val str = jsonP.toJson(playerId)
        jsonPool.free(jsonP)

        return str
    }

    fun getPlayerName(strJson: String): PlayerName {


        val jsonP = jsonPool.obtain()
        val playerName = jsonP.fromJson(PlayerName::class.java, strJson)

        jsonPool.free(jsonP)
        return playerName
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

    override fun log(str: String) {
        super.log("Server: $str")
    }


}