package com.mshdabiola.naijaludo.entity.connection

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.screen.game.logic.ServerGameController
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import java.io.IOException
import java.net.DatagramSocket
import java.net.ServerSocket

class GameServer : Server(), CoroutineScope by CoroutineScope(Dispatchers.Default) {


    var message = ""
    var isRunning = false
    var port = 0

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

            }

            override fun received(connection: Connection?, any: Any?) {

                (connection as NameConnection).playerName?.let {
                    //println("RECEIVED SERVER: server from ${connection.playerName} id ${connection.playerId} and any $any")
                }

                when (any) {

                    is String -> {
                        //println("STRING SERVER: from name ${connection.playerName} id ${connection.playerId}")
                        //println("STRING SERVER MESSAGE $any")


                        if (serverFactory.getKeyFromJson(any) == "playerName") {
                            val playerName = serverFactory.getPlayerName(any)
                            playerConnected(connection, playerName)

                        } else {
                            launch { sendToAllExceptTCP(connection.id, any) }
                            launch { processor().send(any) }
                        }


                    }


                }
            }

            override fun disconnected(connection: Connection?) {
                super.disconnected(connection)

                connection?.let {
                    //println("DISCONNECTED SERVER: from ${(connection as NameConnection).playerName}")
                    removeLocalPlayer(connection.id)
                }

            }
        })

//
    }

    @ObsoleteCoroutinesApi
    fun processor() = actor<String> {


        for (msg in channel) {
            log(" processor: msg")
            delay(500)
            serverFactory.update(msg)
        }

    }

    fun bind() {
        port = getServerPort()
        log(" bind server to port $port")
        bind(port)
    }

    fun getServerPort(): Int {
        log("checking server availability")
        return if (available(Packets.port)) {
            Packets.port
        } else if (available(Packets.port2)) {
            Packets.port2
        } else {
            Packets.port2
        }
    }

    fun playerConnected(connection: NameConnection, playerName: Factory.PlayerName) {
        launch {
            connection.let {
                if (!mapConnection.containsKey(it.playerId)) {
                    val counter = connection.id
                    it.playerName = playerName.playerName
                    mapConnection[counter] = it

                    serverFactory.addOnlinePlayer(counter, playerName.playerName)
                    it.playerId = serverFactory.getPlayerIndex()
                    delay(1000)
                    sendToTCP(it.id, serverFactory.sendPlayerId(it.playerId))
                    delay(1000)
                    sendString(serverFactory.sendAllPlayerNew())
                    //send player to server
                    updateActor?.send(Factory.Message.SendPlayer(serverFactory.playerArray))
                    //println("CONNECTED SERVER: server register name = ${it.playerName} and id is ${it.playerId}  and send id and name to client ")

                }
            }

        }
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

    }

    override fun stop() {
        log("server stop")
        super.stop()
        isRunning = false
        serverFactory.playerArray.clear()

    }

    fun log(str: String) {
        //println("Server $str")
    }

    fun addLocalPlayer(basePlayer: BasePlayer): BasePlayer {

        log(" addLocalplayer $basePlayer")
        val player = serverFactory.addLocalPlayer(basePlayer)
        sendString(serverFactory.sendAllPlayerNew())
//        launch { sendToAllTCP(serverFactory.sendAllPlayerNew()) }
        return player
    }

    fun removeLocalPlayer(removeIndex: Int) {
        log(" removelocaplayer $removeIndex")
        serverFactory.removePlayer(removeIndex)

        sendString(serverFactory.sendAllPlayerNew())
//        launch { sendToAllTCP(serverFactory.sendAllPlayerNew()) }
    }

    fun sendPlay() {
        sendString(serverFactory.playStr())
    }

    fun sendString(str: String) {
        log(" sendString $str")
        launch {
//             delay(1000)
            sendToAllTCP(str)
        }
    }

    fun cleerPlayerArray() {
        serverFactory.playerArray.clear()
    }

    override fun newConnection(): Connection {
        //println("get new connection")
        return NameConnection()
    }


    inner class NameConnection : Connection() {
        var playerName: String? = null
        var playerId = -1
        var idTest = id - 1
        var playerStatus: String? = null
    }

    fun available(port: Int): Boolean {
        var serverSocket: ServerSocket? = null
        var dataGramSocket: DatagramSocket? = null
        try {
            serverSocket = ServerSocket(port)
            serverSocket.reuseAddress = true
            dataGramSocket = DatagramSocket(port)
            dataGramSocket.reuseAddress = true

            return true
        } catch (e: IOException) {

        } finally {
            dataGramSocket?.let {
                it.close()
            }
            serverSocket?.let {
                try {
                    it.close()
                } catch (e: IOException) {

                }
            }
        }
        return false

    }

}