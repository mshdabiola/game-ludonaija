package com.mshdabiola.naijaludo.entity.connection

import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.entity.player.HumanPlayer
import com.mshdabiola.naijaludo.screen.game.DiceController
import com.mshdabiola.naijaludo.screen.game.logic.ClientGameController
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor


class GameClient() : Client(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    constructor(player: BasePlayer) : this() {
        this.player = player
    }

    //    private val logger = Logger(GameClient::class.java.name, Logger.DEBUG)
    var player: BasePlayer? = null


    var isPause = false
    var playerName = player?.name ?: "Player ${(Math.random() * 10).toInt()}"
    val clientGameController = ClientGameController(this)
    val factory = Factory(clientGameController)


    lateinit var players: Array<BasePlayer>
    lateinit var diceController: DiceController


    init {
        Packets.registerPacketFor(this)




        addListener(object : Listener() {
            override fun connected(p0: Connection?) {
                factory.myPlayer = player ?: HumanPlayer(0, intArrayOf())
                println("CONNECTED ClIENT: from ${playerName}")
                println("CONNECTED ClIENT: send name ${playerName}")

            }

            override fun disconnected(connection: Connection?) {
                println("DISCONNECTED ClIENT: from ${playerName}")


            }

            override fun idle(p0: Connection?) {
//                println("IDLE ClIENT: from ${playerName}")
//                logger.debug("client idle")

            }

            override fun received(connection: Connection?, any: Any?) {
//                println("RECEIVED ClIENT: from ${playerName} receice itme $any")
//                logger.debug("client receive")

                when (any) {

                    is Connect -> {
                        println("CONNECT ClIENT: from ${playerName} CHANGED NAME TO ${any.name}")
                        GlobalScope.launch {
//                            playerName = any.name
                            player?.let {
                                it.id = any.ids
                                clientGameController.playerId = any.ids
                                playerName = it.name
                                println("name is ${it.name}")
//                                playerName="abiola"
                            }

                            delay(20)
                            sendTCP(SendName(playerName))
                        }

                    }
                    is Pause -> {
                        println("PAUSE ClIENT:  from ${playerName} ")
                        GlobalScope.launch { isPause = true }
                    }
                    is String -> {
                        println("String ClIENT $playerName:  from $any ")
                        launch { processor().send(any) }
                    }

                    is Resume -> {
                        println("RESUME ClIENT: from ${playerName}")
                        GlobalScope.launch { isPause = false }
                    }

                }

            }
        })

    }

    fun processor() = actor<String> {
        for (msg in channel) {
            log("processor $msg")
//            delay(1000)
            factory.update(msg)
        }
    }

//    fun CoroutineScope.actorP() = actor<GameControllerPacket> {
//        channel.receive().gameController?.let { gameController.copyDetails(it) }
//    }

//    fun connect() {
//
//        start()
//        try {
//            connect(900, "localhost", Packets.port)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }

    fun log(str: String) {
        println("Client $str")
    }

    fun connectIP(ip: String = "localhost") {
        launch {
            log("start client")
            start()
            delay(1000)
            try {
                log("connect to ip $ip")
                connect(900, ip, Packets.port)
                log(" connected")
            } catch (e: Exception) {
                log(" client fail to connected")
                e.printStackTrace()


            }
        }


    }


    fun sendString(str: String) {
        launch {
            log("client send $str")

            sendTCP(str)
        }
    }

    override fun dispose() {
        println("dispose client")
        super.dispose()
        cancel()
    }

    override fun stop() {
        println("stop client")
        super.stop()
        cancel()
    }

    fun pause() {
        isPause = true
        launch { sendTCP(Pause) }
    }

    fun resume() {
        isPause = false
        launch { sendTCP(Resume) }
    }
}