package com.mshdabiola.naijaludo.entity.connection

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.screen.game.logic.ServerGameController
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor

class GameServer : Server(), CoroutineScope by CoroutineScope(Dispatchers.Default) {


    var message = ""
    var isRunning = false

    var isPause = false
    var updateActor: SendChannel<Factory.Message>? = null


    val mapConnection = HashMap<Int, NameConnection>()
    val serverGameController = ServerGameController(this)
    val serverFactory = ServerFactory(serverGameController)
    var channel: SendChannel<ArrayList<BasePlayer>>? = null


    init {

        Packets.registerPacketFor(this)

        addListener(object : Listener() {
            override fun connected(connection: Connection?) {
                (connection as NameConnection)

                launch {


                    connection.let {
                        if (!mapConnection.containsKey(it.playerId)) {
                            val counter = connection.id
                            it.playerName = "player $counter"


                            mapConnection[counter] = it

                            serverFactory.addOnlinePlayer(counter)
                            it.playerId = serverFactory.getPlayerIndex()
                            delay(2000)
                            sendToTCP(it.id, Connect(it.playerId, it.playerName!!))
                            println("CONNECTED SERVER: server register name = ${it.playerName} and id is ${it.playerId}  and send id and name to client ")

                        }
                    }
                }


            }

            override fun received(connection: Connection?, any: Any?) {

                (connection as NameConnection).playerName?.let {
                    println("RECEIVED SERVER: server from ${connection.playerName} id ${connection.playerId} and any $any")
                }

                when (any) {
                    is SendName -> {
                        launch {

                            println("SEND NAME SERVER: from ${connection.playerName} id ${connection.playerId}")

                            connection.playerName = any.name
                            serverFactory.setNameOnPlayer(connection.playerId, any.name)
                            //send players to all
                            delay((connection.id * 10).toLong())
                            launch { sendToAllTCP(serverFactory.sendAllPlayerNew()) }
                            launch { updateActor?.send(Factory.Message.SendPlayer(serverFactory.playerArray)) }

                        }
                    }
//
                    is String -> {
                        println("STRING SERVER: from name ${connection.playerName} id ${connection.playerId}")
                        println("STRING SERVER MESSAGE $any")

                        launch { sendToAllExceptTCP(connection.id, any) }
                        launch { processor().send(any) }


                    }


                    is Resume -> {
                        println("RESUME SERVER: from ${connection.playerName} id ${connection.playerId}")
                        launch { isPause = false }
                    }

                    is Pause -> {
                        println("PAUSE SERVER: from ${connection.playerName} id ${connection.playerId}")
                        launch { isPause = true }
                    }
                }
            }

            override fun disconnected(connection: Connection?) {
                super.disconnected(connection)

                connection?.let {
                    println("DISCONNECTED SERVER: from ${(connection as NameConnection).playerName}")
                    removeLocalPlayer(connection.id)
                }

            }
        })

//
    }

    fun processor() = actor<String> {


        for (msg in channel) {
            log(" processor: msg")
//            delay(1000)
            serverFactory.update(msg)
        }

    }

    fun bind() {
        bind(Packets.port)
    }

    fun connect() {
        log(" connect()")



        start()
        isRunning = true
    }

    override fun dispose() {
        log("dispose server")
        super.dispose()
        isRunning = false
        cancel()
    }

    override fun stop() {
        log("server stop")
        super.stop()
        isRunning = false
        cancel()
    }

    fun log(str: String) {
        println("Server $str")
    }

    fun addLocalPlayer(basePlayer: BasePlayer): BasePlayer {

        log(" addLocalplayer $basePlayer")
        val player = serverFactory.addLocalPlayer(basePlayer)
        launch { sendToAllTCP(serverFactory.sendAllPlayerNew()) }
        return player
    }

    fun removeLocalPlayer(removeIndex: Int) {
        log(" removelocaplayer $removeIndex")
        serverFactory.removePlayer(removeIndex)

        launch { sendToAllTCP(serverFactory.sendAllPlayerNew()) }
    }

    fun sendString(str: String) {
        log(" sendString $str")
        launch { sendToAllTCP(str) }
    }

    override fun newConnection(): Connection {
        println("get new connection")
        return NameConnection()
    }


    inner class NameConnection : Connection() {
        var playerName: String? = null
        var playerId = -1
        var idTest = id - 1
        var playerStatus: String? = null
    }

}