package com.mshdabiola.naijaludo.screen

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.*
import com.mshdabiola.naijaludo.config.GameManager
import com.mshdabiola.naijaludo.entity.SavedGameGenerator
import com.mshdabiola.naijaludo.entity.connection.*
import com.mshdabiola.naijaludo.screen.game.GameLogic
import com.mshdabiola.naijaludo.screen.game.logic.NewGameLogic
import com.mshdabiola.naijaludo.screen.loading.LoadingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class NaijaLudo : Game(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    lateinit var assetManager: AssetManager
    lateinit var batch: SpriteBatch
    lateinit var shapeRenderer: ShapeRenderer
    var server by Delegates.notNull<GameServer>()
    private val logger = Logger(NaijaLudo::class.java.name, Logger.DEBUG)
    var runClient = { _: String, _: Boolean -> }
    var saveGame = true

    val pathArray = arrayOf(
            "a", "fa", "prs", "rf", "sr",
            "ap", "frp", "ps", "r", "sap",
            "apf", "fp", "psf", "rp", "sfa",
            "as", "far", "pa", "rps", "s",
            "asr", "f", "p", "rpa", "sf"

    )
    val savedGameGenerator = SavedGameGenerator()

    var newGameLogic: NewGameLogic? = null

    //    val json = Json().apply {
////        setUsePrototypes(false)
//    }
    val jsonPool = object : Pool<Json>(3) {
        override fun newObject(): Json {
            return Json(JsonWriter.OutputType.minimal)
        }
    }

    val path = "Ludo\\"
    val line = "\\"
    val fileName = "NewGameLogic.json"
    var counter = 0
    val part = 0
    var savegameName = "SaveGame_part_$part"
    var saveGamePath = "$path$savegameName${"_number_"}"

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
            println("onconnectedResult $success")
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
            println("NaijaLudo: onDiscovery $start")

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
        Gdx.app.logLevel = Application.LOG_ERROR
        assetManager = AssetManager()
        assetManager.logger.level = Logger.ERROR
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()


        setScreen(LoadingScreen(this))

        launch {
            server = GameServer()
            delay(1000)

            log("bind port")
            server.bind()
            record = mutableMapOf(
                    "port" to server.port.toString(),
                    "name" to GameManager.name,
                    "available" to "visible",
                    "isServer" to "false"
            )
            connectInterfaceAnd?.startDiscoveryRegistration(record)
        }


    }


    override fun dispose() {

        saveNewGameLogic()

//        client.close()
        server.dispose()
//        MassetDescriptor.loadingSkin.dispose()
        assetManager.dispose()
        shapeRenderer.dispose()
        batch.dispose()
    }

    fun readNewGameLogicSaved() {
        this.launch {
            try {
                if (newGameLogic == null) {
                    newGameLogic = readJsonObject(fileName)
                }
            } catch (e: GdxRuntimeException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun readNewGameLogicSaved(number: Int): GameLogic {
        val nu = number / 30
        val folderNumber = number % 25
        val foldername = pathArray[folderNumber]
        val name = "SaveGame_part_0_number_${nu}.json"
        val pathF = "$foldername$line$name"
        println("reading gamelogic $pathF")
        return readJsonObjectSaved(pathF)

    }

    fun readJsonObjectSaved(fileName: String): NewGameLogic {

        val file = Gdx.files.internal("$path$fileName")
        val str = file.readString()
        println("read file finished")
        val json = jsonPool.obtain()
        val logic = json.fromJson(NewGameLogic::class.java, str)
        jsonPool.free(json)
        return logic


    }

    fun saveNewGameLogic() {
        launch {
            try {
                writeJsonObject(newGameLogic, fileName)
            } catch (e: GdxRuntimeException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun writeJsonObject(gameLogic: NewGameLogic?, fileName: String) {


        gameLogic?.let {
//            val str = json.prettyPrint(it)
            val json = jsonPool.obtain()
            val str = json.toJson(it)
            jsonPool.free(json)

            val file = Gdx.files.local("$saveGamePath$counter.json")
            file.writeString(str, false)
            println("write file finished")
            saveGame = true
            counter++
        }

    }
//    fun writeJsonObject(gameLogic: NewGameLogic?, fileName: String) {
//
//
//        gameLogic?.let {
//            val str = json.prettyPrint(it)
//            val file = Gdx.files.local("$path$fileName")
//            file.writeString(str, false)
//            println("write file finished")
//        }
//
//    }

    fun readJsonObject(fileName: String): NewGameLogic {

        val file = Gdx.files.local("$path$fileName")
        val str = file.readString()
        println("read file finished")
        val json = jsonPool.obtain()
        val logic = json.fromJson(NewGameLogic::class.java, str)
        jsonPool.free(json)
        return logic

    }

    fun deleteCompleteFile() {
        launch {
            val file = Gdx.files.local("$path$fileName")
            file.delete()
        }
    }

    fun log(string: String) {
        println("logging msg: $string")
        connectInterfaceAnd?.log(string)
        logger.error(string)
    }

}