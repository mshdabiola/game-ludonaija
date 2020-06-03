package com.mshdabiola.naijaludo.entity.connection

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.Pool
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.config.GameManager
import com.mshdabiola.naijaludo.config.GameState
import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.entity.player.HumanPlayer
import com.mshdabiola.naijaludo.entity.player.OnlinePlayer
import com.mshdabiola.naijaludo.screen.game.GameController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


class Factory(val gameController: GameController) {
    companion object {
        val currentState = "currentState"
        val currentPlayerIndex = "currentPlayerIndex"

        //    val currentPlayer = players[currentPlayerIndex]
        val currentSeed = "currentSeed"
        val noSeedActivated = "noSeedActivated"
        val currentDiceIndex = "currentDiceIndex"
        val currentDiceNo = "currentDiceNo"
        val diceValue = "diceValue"
        val dice1Value = "dice1Value"
        val dice2Value = "dice2Value"
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

        fun currentStateToJson(gameController: GameController): String {
            val json = jsonPool.obtain()
            val str = json.toJson(GameController().apply {
                currentState = gameController.currentState
            })
            jsonPool.free(json)
            return str

        }

        fun currentPlayerIndexToJson(gameController: GameController): String {
            val json = jsonPool.obtain()
            val str = json.toJson(GameController().apply {
                currentPlayerIndex = gameController.currentPlayerIndex
            })
            jsonPool.free(json)
            return str


        }

        fun currentSeedToJson(gameController: GameController): String {
            val json = jsonPool.obtain()
            val str = json.toJson(GameController().apply {
                currentSeed = gameController.currentSeed
            })
            jsonPool.free(json)
            return str

        }

        fun currentSeedToSendJson(gameController: GameController): String {
            val json = jsonPool.obtain()
            val seedToSend = SeedToSend(gameController.currentSeed)
            val str = json.toJson(seedToSend)
            jsonPool.free(json)
            return str

        }

        fun noSeedActivatedToJson(gameController: GameController): String {
            val json = jsonPool.obtain()
            val str = json.toJson(GameController().apply {
                noSeedActivated = gameController.noSeedActivated
            })
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

        fun currentDiceNoToJson(gameController: GameController): String {
            val json = jsonPool.obtain()
            val str = json.toJson(GameController().apply {
                currentDiceNo = gameController.currentDiceNo
            })
            jsonPool.free(json)
            return str


        }

        fun diceValueToJson(gameController: GameController): String {
            val json = jsonPool.obtain()
            val str = json.toJson(DiceValue(gameController.diceController.value[0], gameController.diceController.value[1]))
            jsonPool.free(json)
            return str

        }

    }


    var playerArray = ArrayList<BasePlayer>()
    var updateActor: SendChannel<Message>? = null
    var myId = 0

    lateinit var myPlayer: BasePlayer
    val computerPlayerArray = ArrayList<BasePlayer>()


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

    fun update(strJson: String) {
        when (getKeyFromJson(strJson)) {

            currentState -> {
                gameController.currentState = getTempGameController(strJson).currentState

            }
            currentPlayerIndex -> {
                with(gameController) {
                    currentPlayerIndex = getTempGameController(strJson).currentPlayerIndex

                }

            }
            "ids" -> {
                val json = jsonPool.obtain()
                val seedToSend = json.fromJson(SeedToSend::class.java, strJson)
                val seed = gameController.currentPlayer.homeSeed.find { it.playerId == seedToSend.ids[0] && it.colorId == seedToSend.ids[1] && it.id == seedToSend.ids[2] }!!
                gameController.currentSeed = seed
                gameController.currentState = GameState.HASCHOOSESEED
            }
            currentSeed -> {
                var seed = getTempGameController(strJson).currentSeed
                println("seed send is $seed")
                seed = gameController.currentPlayer.homeSeed.find { it.playerId == seed.playerId && it.colorId == seed.colorId && it.id == seed.id }!!
                println("seed received is $seed")
                gameController.currentSeed = seed
                gameController.currentState = GameState.HASCHOOSESEED
            }
            currentDiceNo -> {
                gameController.currentDiceNo = getTempGameController(strJson).currentDiceNo
            }
            noSeedActivated -> {
                gameController.noSeedActivated = getTempGameController(strJson).noSeedActivated
            }
            currentDiceIndex -> {
                with(gameController) {
                    currentDiceIndex = getTempGameController(strJson).currentDiceIndex
                    currentDiceNo = diceController.getDiceValue(currentDiceIndex)
                    currentState = GameState.HASCHOOSEDICE
                }
            }
            dice1Value -> {
                val json = jsonPool.obtain()
                val dice1 = json.fromJson(DiceValue::class.java, strJson)
                jsonPool.free(json)
                gameController.diceController.tossWithValue(dice1.dice1Value, dice1.dice2Value)
                gameController.currentState = GameState.HASTOSS
            }

            createPlayers -> {
//                println(strJson)
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


        }

    }

    fun addComputerPlayer(basePlayer: BasePlayer): String {
        basePlayer.id = MathUtils.random(100, 599)
        computerPlayerArray.add(basePlayer)
        val json = jsonPool.obtain()
        val addPlayer = AddPlayer(basePlayer.id, 0)

        val str = json.toJson(addPlayer)
        jsonPool.free(json)
        return str
    }

    fun removerPlayer(index: Int): String {
        val removePlayer = RemovePlayer(index)
        val json = jsonPool.obtain()
        val str = json.toJson(removePlayer)
        jsonPool.free(json)
        return str
    }

    fun play(): String {
        val play = Play(true)
        val json = jsonPool.obtain()
        val str = json.toJson(play)
        jsonPool.free(json)
        println("play string to send $str")
        return str
    }

    fun changeComputerId(addPlayer: AddPlayer) {
        println("change compute id from ${computerPlayerArray[0].id} to ${addPlayer.newId}")
        val basePlayer = computerPlayerArray.find { it.id == addPlayer.oldId }
        basePlayer?.let {
            println("change compute id from ${it.id} to ${addPlayer.newId}")
            it.id = addPlayer.newId
        }
    }


    fun getMyPlayer(player: BasePlayer): HumanPlayer {
        return HumanPlayer(player.id, player.gamecolorsId).apply {
            if (!Config.isTest) {
                iconId = GameManager.avatar
            } else {
                iconId = 4
            }
        }
    }

    fun editPlayerColorNPosition() {
        println("find computer player")
        val player = playerArray.find { it.id == myPlayer.id }

        player?.let {
            myPlayer.gamecolorsId = it.gamecolorsId
            val index = playerArray.indexOf(player)
            playerArray.remove(player)
            playerArray.add(index, myPlayer)

        }
        println(" computer player ${computerPlayerArray.isNotEmpty()}")
        if (computerPlayerArray.isNotEmpty()) {
            for (computer in computerPlayerArray) {
                var findPlayer: BasePlayer? = null
                for (player in playerArray) {
                    println(" computer player enter playerArray for loop computer id=${computer.id} and player id=${player.id}")
                    if (computer.id == player.id) {
                        println(" computer player enter id")
                        findPlayer = player
                    }
                }
                findPlayer?.let {
                    println(" computer player enter find player")
                    computer.gamecolorsId = it.gamecolorsId
                    val index = playerArray.indexOf(it)
                    playerArray.remove(it)
                    playerArray.add(index, computer)
                }
            }
        }
    }


    class DiceValue(var dice1Value: Int, var dice2Value: Int) {
        constructor() : this(0, 0)
    }

    class SendPlayers(var createPlayers: List<OnlinePlayer>) {
        constructor() : this(arrayListOf())
    }

    class AddPlayer(var oldId: Int, var newId: Int) {


        constructor() : this(-8, -9)

        var iconId = 1
    }

    class RemovePlayer(var removeIndex: Int) {
        constructor() : this(0)
    }

    class Play(var play: Boolean) {
        constructor() : this(false)
    }

    class SeedToSend(var ids: IntArray) {
        constructor() : this(intArrayOf())
        constructor(seed: Seed) : this(intArrayOf()) {
            ids = intArrayOf(seed.playerId, seed.colorId, seed.id)
        }


    }

    sealed class Message {
        object Play : Message()
        class SendPlayer(val players: ArrayList<BasePlayer>) : Message()
    }


}