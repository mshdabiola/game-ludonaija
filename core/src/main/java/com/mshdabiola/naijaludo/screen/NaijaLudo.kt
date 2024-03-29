package com.mshdabiola.naijaludo.screen

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.Pool
import com.mshdabiola.naijaludo.entity.SavedGameGenerator
import com.mshdabiola.naijaludo.entity.connection.*
import com.mshdabiola.naijaludo.screen.game.logic.NewGameLogic
import com.mshdabiola.naijaludo.screen.loading.LoadingScreen
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class NaijaLudo : Game(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    lateinit var assetManager: AssetManager
    lateinit var batch: SpriteBatch
    lateinit var shapeRenderer: ShapeRenderer
    var server by Delegates.notNull<GameServer>()
    private val logger = Logger(NaijaLudo::class.java.name, Logger.DEBUG)

    val savedGameGenerator = SavedGameGenerator()
    var newGameLogic: NewGameLogic? = null


    val jsonPool = object : Pool<Json>(3) {
        override fun newObject(): Json {
            return Json(JsonWriter.OutputType.minimal)
        }
    }
    val fileCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    val path = "Ludo\\"

    val fileName = "NewGameLogic.json"

    var record = mutableMapOf(
            "port" to (5555).toString(),
            "name" to "player",
            "available" to "visible",
            "isServer" to "false"
    )
    var isConnected = false
    var peerDevicesChanged = false
    var peerDevices: List<Pair<String, String>>? = null
    var connectInterfaceAnd: ConnectInterfaceAnd? = null
    val connectInterface = object : ConnectInterface {
        override fun onP2pEnabled(enable: Boolean) {
            if (enable) {
                log("p2p enable")
            } else {
                log("p2p disable")
            }
        }

        override fun onP2pError() {
            log("error occur")
        }

        override fun onDeviceBusy() {
            log("device busy")
        }

        override fun onConnectResult(success: Boolean) {
            //println("onconnectedResult $success")
            if (success) {
                log("connected")

                launch {
                    delay(500)
                    isConnected = true
                }

            } else {
                log("not connected")
                isConnected = false
            }
        }

        override fun onServiceFound(name: String, registrationType: String, device: WifiDevice) {

            log("on Service Found name: $name")
            log("on Service Found registrationType: $registrationType")
            log("on Service Found device address: ${device.address}")
            log("on Service Found device address: ${device.isOwner}")
            log("on Service Found device address: ${device.name}")

            log("fond service device CONNECTING")
//            connectInterfaceAnd?.connect(device.address)

        }

        override fun onServiceInfo(fullName: String, records: Map<String, String>, device: WifiDevice) {

            log("on service info name: $fullName")
            log("on service info registrationType: ${records["name"]}")
            log("on service info device address: ${device.address}")
            log("on service info device address: ${device.isOwner}")
            log("on service info device address: ${device.name}")
            log("on service info device address: $records")
            records["name"]?.let {
                val buddies = HashMap<String, String>()
                buddies[device.address] = it
                log("buddyName is $it")
            }

        }

        override fun onGroupFormed(form: P2pInfo?) {
            log("on group form hostAddress:  ${form?.groupOwnerAddress?.hostAddress}")
            log("on group form group form: ${form?.isGroupForm}")
            log("on group form isGroupOwner: ${form?.isGroupOwner}")
        }

        override fun onGroupInfo(info: P2pGroup?) {
            log("on group Info network name:  ${info?.networkName}")
            log("on group Info password: ${info?.password}")
            log("on group Info isGroupOwner: ${info?.isGroupOwner}")
            connectInterfaceAnd?.getGroupInfo()?.let {
                log("Password: ${it.password}")
            }
        }

        override fun onDiscovery(start: Boolean) {
            //println("NaijaLudo: onDiscovery $start")

            if (start) {
                log("discovery start")
                discoveryButton?.isVisible = false
            } else {
                log("discovery stop")
                discoveryButton?.isVisible = true
            }

        }

        override fun onPeerDevicesChanged(peerDevicess: List<Pair<String, String>>) {
            log("onpeerDeviceChanged")
            peerDevices = peerDevicess

            peerDevicesChanged = true
        }


    }

    var discoveryButton: TextButton? = null


    override fun create() {
//        Gdx.app.logLevel = Application.LOG_ERROR
        assetManager = AssetManager()
//        assetManager.logger.level = Logger.ERROR
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()


        setScreen(LoadingScreen(this))

//        launch {
//            server = GameServer()
//            delay(1000)
//
//            log("bind port")
//            server.bind()
//            record = mutableMapOf(
//                    "port" to server.port.toString(),
//                    "name" to GameManager.name,
//                    "available" to "visible",
//                    "isServer" to "false"
//            )
//            connectInterfaceAnd?.startDiscoveryRegistration(record)
//        }


    }


    override fun dispose() {

        saveNewGameLogic()
//        server.dispose()
        assetManager.dispose()
        shapeRenderer.dispose()
        batch.dispose()
    }

    fun readNewGameLogicSaved() {
        this.launch(fileCoroutineExceptionHandler) {
            if (newGameLogic == null) {
                newGameLogic = readJsonObject(fileName)
            }

        }
    }


    fun saveNewGameLogic() {
        launch(fileCoroutineExceptionHandler) {

            writeJsonObject(newGameLogic, fileName)

        }
    }


    fun writeJsonObject(gameLogic: NewGameLogic?, fileName: String) {


        gameLogic?.let {
            val json = jsonPool.obtain()
            json.setUsePrototypes(false)
            val str = json.prettyPrint(it)
            jsonPool.free(json)
            val file = Gdx.files.local("$path$fileName")
            file.writeString(str, false)
            //println("write file finished")
        }

    }

    fun readJsonObject(fileName: String): NewGameLogic {

        val file = Gdx.files.local("$path$fileName")
        val str = file.readString()
        //println("read file finished")
        val json = jsonPool.obtain()
        val logic = json.fromJson(NewGameLogic::class.java, str)
        jsonPool.free(json)
        return logic

    }

    fun deleteCompleteFile() {
        launch(fileCoroutineExceptionHandler) {
            val file = Gdx.files.local("$path$fileName")
            file.delete()
        }
    }

    fun log(string: String) {
//        //println("logging msg: $string")
//        connectInterfaceAnd?.log(string)
//        logger.error(string)
    }

}