package com.mshdabiola.naijaludo.entity.connection

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.Pool
import com.mshdabiola.naijaludo.config.GameState
import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.entity.player.OnlinePlayer
import com.mshdabiola.naijaludo.screen.game.GameController

class ServerFactory(val gameController: GameController) {

    val jsonReaderPool = object : Pool<JsonReader>(6) {
        override fun newObject(): JsonReader {
            return JsonReader()
        }
    }
    val playerArray = ArrayList<BasePlayer>()

    val jsonPool = object : Pool<Json>(6) {
        override fun newObject(): Json {
            return Json()
        }
    }

    fun getTempGameController(strJson: String): GameController {
        val json = jsonPool.obtain()
        val controller = json.fromJson(GameController::class.java, strJson)
        jsonPool.free(json)
        return controller

    }

    fun update(strJson: String) {
        when (getKeyFromJson(strJson)) {
            Factory.currentState -> {
                gameController.currentState = getTempGameController(strJson).currentState

            }
            Factory.currentPlayerIndex -> {
                with(gameController) {
                    currentPlayerIndex = getTempGameController(strJson).currentPlayerIndex

                }

            }
            "ids" -> {
                val json = Factory.jsonPool.obtain()
                val seedToSend = json.fromJson(Factory.SeedToSend::class.java, strJson)
                val seed = gameController.currentPlayer.homeSeed.find { it.playerId == seedToSend.ids[0] && it.colorId == seedToSend.ids[1] && it.id == seedToSend.ids[2] }!!
                gameController.currentSeed = seed
                gameController.currentState = GameState.HASCHOOSESEED
            }
            Factory.currentSeed -> {
                var seed = getTempGameController(strJson).currentSeed
                seed = gameController.currentPlayer.homeSeed.find { it.playerId == seed.playerId && it.colorId == seed.colorId && it.id == seed.id }!!
                gameController.currentSeed = seed
                gameController.currentState = GameState.HASCHOOSESEED
            }
            Factory.currentDiceNo -> {
                gameController.currentDiceNo = getTempGameController(strJson).currentDiceNo
            }
            Factory.noSeedActivated -> {
                gameController.noSeedActivated = getTempGameController(strJson).noSeedActivated
            }
            Factory.currentDiceIndex -> {
                with(gameController) {
                    currentDiceIndex = getTempGameController(strJson).currentDiceIndex
                    currentDiceNo = diceController.getDiceValue(currentDiceIndex)
                    currentState = GameState.HASCHOOSEDICE
                }
            }
            Factory.dice1Value -> {
                val json = Factory.jsonPool.obtain()
                val dice1 = json.fromJson(Factory.DiceValue::class.java, strJson)
                Factory.jsonPool.free(json)
                gameController.diceController.tossWithValue(dice1.dice1Value, dice1.dice2Value)
                gameController.currentState = GameState.HASTOSS
            }


        }
    }


    fun changePlayerToOnlinePlayer(player: BasePlayer): OnlinePlayer {
        return OnlinePlayer(player.id, player.gamecolorsId, player.name).apply {

            iconId = player.iconId
        }
    }

    fun addOnlinePlayer(id: Int) {
        addPlayer(OnlinePlayer(id, intArrayOf(), ""))
    }

    fun getPlayerIndex() = playerArray.size - 1
    suspend fun setNameOnPlayer(id: Int, name: String) {
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
//        val playerSize = playerArray.size
//        val colors = getColors(playerSize)
//        playerArray.forEachIndexed { index, basePlayer ->
//            basePlayer.id = index
//
//            basePlayer.gamecolorsId = colors[index]
//        }


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

    suspend fun sendAllPlayerNew(): String {
        val players = playerArray.map { changePlayerToOnlinePlayer(it) }

        val sendPlayer = Factory.SendPlayers(players)
        val jsonP = jsonPool.obtain()
        val str = jsonP.toJson(sendPlayer)
        jsonPool.free(jsonP)
//        println("Send all player $str")
        return str
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

    fun getKeyFromJson(strJson: String): String {
        val json = jsonReaderPool.obtain()
        val str = json.parse(strJson)?.get(0)?.name ?: "noValue"
        jsonReaderPool.free(json)
        return str

    }

}