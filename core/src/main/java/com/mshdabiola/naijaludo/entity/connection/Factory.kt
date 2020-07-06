package com.mshdabiola.naijaludo.entity.connection

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.Pool
import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.entity.player.OnlinePlayer
import com.mshdabiola.naijaludo.screen.game.GameController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


open class Factory(val gameController: GameController) {
    companion object {

        //        val currentPlayerIndex = "currentPlayerIndex"
        val currentDiceIndex = "currentDiceIndex"
        val diceValue = "diceValue"
        val dice1Value = "dice1Value"
        val createPlayers = "createPlayers"

        val jsonPool = object : Pool<Json>(6) {
            override fun newObject(): Json {
                return Json()
            }
        }
        val jsonReaderPool = object : Pool<JsonReader>(6) {
            override fun newObject(): JsonReader {
                return JsonReader()
            }
        }


        fun currentSeedToSendJson(gameController: GameController): String {
            val json = jsonPool.obtain()
            val seedToSend = SeedToSend(gameController.currentSeed, gameController.currentDiceIndex)
            val str = json.toJson(seedToSend)
            jsonPool.free(json)
            return str

        }

        fun currentDiceIndexToJson(gameController: GameController): String {
            val json = jsonPool.obtain()
            val str = json.toJson(GameController().apply {
                currentDiceIndex = gameController.currentDiceIndex
            })
            jsonPool.free(json)
            return str


        }

//        fun currentPlayerIndexToJson(gameController: GameController): String {
//            val json = jsonPool.obtain()
//            val str = json.toJson(GameController().apply {
//                currentDiceIndex = gameController.currentPlayerIndex
//            })
//            jsonPool.free(json)
//            return str
//
//
//        }

        fun diceValueToJson(gameController: GameController): String {
            val json = jsonPool.obtain()
            val str = json.toJson(DiceValue(gameController.diceController.value[0], gameController.diceController.value[1]))
            jsonPool.free(json)
            return str

        }

    }


    var playerArray = ArrayList<BasePlayer>()
    var updateActor: SendChannel<Message>? = null
    lateinit var myPlayer: BasePlayer

    var isServer = false


    fun getKeyFromJson(strJson: String): String {
        val json = jsonReaderPool.obtain()
        val str = json.parse(strJson)?.get(0)?.name ?: "noValue"
        jsonReaderPool.free(json)
        return str

    }

    fun getTempGameController(strJson: String): GameController {
        val json = jsonPool.obtain()
        val controller = json.fromJson(GameController::class.java, strJson)
        jsonPool.free(json)
        return controller

    }

    open fun update(strJson: String) {
        when (getKeyFromJson(strJson)) {


//            currentPlayerIndex -> {
//                with(gameController) {
//                    currentPlayerIndex = getTempGameController(strJson).currentPlayerIndex
//
//                }
//
//            }
            "play" -> {
                log("update receive play")

                GlobalScope.launch { updateActor?.send(Message.Play) }

            }
            "ids" -> {
                val json = jsonPool.obtain()
                log("receive seeds is $strJson")
                val seedToSend = json.fromJson(SeedToSend::class.java, strJson)
//                val seed = gameController.currentPlayer.homeSeed.find { it.playerId == seedToSend.ids[0] && it.colorId == seedToSend.ids[1] && it.id == seedToSend.ids[2] }!!
//                gameController.sendSeed = seed
                gameController.seedToSend = seedToSend
//                gameController.currentState = GameState.HASCHOOSESEED
            }

            currentDiceIndex -> {
                with(gameController) {
//                    currentDiceIndex =
                    log("set currentDiceIndex is $strJson")
                    sendChooseDiceIndex = getTempGameController(strJson).currentDiceIndex
//                    currentDiceNo = diceController.getDiceValue(currentDiceIndex)
//                    currentState = GameState.HASCHOOSEDICE
                }
            }
            dice1Value -> {
                val json = jsonPool.obtain()
                val dice1 = json.fromJson(DiceValue::class.java, strJson)
                jsonPool.free(json)
                log("set gamecontroller for dicevalue $strJson")
                gameController.sendDiceValue = dice1
//                gameController.diceController.tossWithValue(dice1.dice1Value, dice1.dice2Value)
//                gameController.currentState = GameState.HASTOSS
            }

            createPlayers -> {
                log("createPlayers")
                if (!isServer) {
                    //println(strJson)
                    val json = jsonPool.obtain()
                    val sendPlayers = json.fromJson(SendPlayers::class.java, strJson)
                    jsonPool.free(json)
                    playerArray.clear()
                    playerArray.addAll(sendPlayers.createPlayers)
                    editPlayerColorNPosition()

                    GlobalScope.launch {
                        updateActor?.send(Message.SendPlayer(playerArray))
                    }
                }
//
            }
            "playerId" -> {
                if (!isServer) {
                    //println(strJson)
                    val json = jsonPool.obtain()
                    val playerId = json.fromJson(PlayerId::class.java, strJson)
                    jsonPool.free(json)


                    myPlayer.let {
                        it.id = playerId.playerId
                        gameController.playerId = playerId.playerId

                    }


                }
            }


        }

    }

    open fun log(str: String) {
        //println("Factory: $str")
    }

    fun playStr(): String {
        val play = Play(true)
        val json = jsonPool.obtain()
        val str = json.toJson(play)
        jsonPool.free(json)
        //println("play string to send $str")
        return str
    }


    fun editPlayerColorNPosition() {
        //println("find computer player")
        val player = playerArray.find { it.id == myPlayer.id }

        player?.let {
            myPlayer.gamecolorsId = it.gamecolorsId
            val index = playerArray.indexOf(player)
            playerArray.remove(player)
            playerArray.add(index, myPlayer)

        }

    }


    class DiceValue(var dice1Value: Int, var dice2Value: Int) {
        constructor() : this(0, 0)
    }

    class PlayerName(var playerName: String) {
        constructor() : this("player")
    }

    class PlayerId(var playerId: Int) {
        constructor() : this(-1)
    }

    class SendPlayers(var createPlayers: List<OnlinePlayer>) {
        constructor() : this(arrayListOf())
    }

    class Play(var play: Boolean) {
        constructor() : this(false)
    }

    class SeedToSend(var ids: IntArray) {
        constructor() : this(intArrayOf(0, 0, 0, 0))
        constructor(seed: Seed, currentDiceIndex: Int) : this(intArrayOf(0, 0, 0, 0)) {
            ids = intArrayOf(seed.playerId, seed.colorId, seed.id, currentDiceIndex)
        }


    }

    class CurrentPlayerToSend(val currentPlayerIndex: Int) {
        constructor() : this(0)
    }

    sealed class Message {
        object Play : Message()
        class SendPlayer(val players: ArrayList<BasePlayer>) : Message()
    }


}