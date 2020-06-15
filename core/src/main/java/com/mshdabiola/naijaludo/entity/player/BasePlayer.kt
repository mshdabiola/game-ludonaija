package com.mshdabiola.naijaludo.entity.player

import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.config.GameColor
import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.Ui
import com.mshdabiola.naijaludo.entity.connection.PlayerData
import com.mshdabiola.naijaludo.entity.connection.SeedData
import com.mshdabiola.naijaludo.entity.display.PlayerPanel
import com.mshdabiola.naijaludo.screen.game.DiceController
import com.mshdabiola.naijaludo.screen.game.GameController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

abstract class BasePlayer(var name: String, var id: Int, var gamecolorsId: IntArray, val isOnlineGame: Boolean = false) : Ui {

    constructor() : this("", 0, intArrayOf())

    var homeSeed = ArrayList<Seed>()
    var seedOut = ArrayList<Seed>()

    var iconId = 1

    var point = 0
    var lastPoint = 0
//
//    @Transient
//    var client: GameClient? = null

    @Transient
    var list = ArrayList<Seed>()

    @Transient
    protected lateinit var gameController: GameController

    @Transient
    protected lateinit var diceController: DiceController

    @Transient
    lateinit var playerPanel: PlayerPanel


    override fun createUi() {
//        iconId = changePlayerToIcon(this)
        playerPanel = PlayerPanel(this)

    }

    override fun updateUi() {
        homeSeed.forEach { it.updateUi() }
        seedOut.forEach {
            playerPanel.addSeedOut(it)
        }
    }

    override fun resetUi() {
        homeSeed.forEach { it.actor.remove() }
        homeSeed.clear()
        seedOut.forEach { it.actor.remove() }
        seedOut.clear()
    }

    init {
        println("enter base player")

////       createSeed()
//        try {
//            if (!Config.isTest) {
//
////                playerPanel = PlayerPanel(this)
//            }
//        } catch (e: Exception) {
//            println("skin not init")
//            e.printStackTrace()
//        }
//        if (isOnlineGame) {
//            client = GameClient(this)
//        }

    }

//    fun update() {
//        playerPanel = PlayerPanel(this)
//
//    }

//    fun reset() {
//
//    }

    open fun setController(controller: GameController) {
        println("set player controller base")
        gameController = controller
        diceController = controller.diceController
    }

//    fun changePlayerToIcon(player: BasePlayer): Int {
//        return when (player) {
//            is RandomPlayer -> {
//                PlayType.RANDOM.ordinal + 1
//            }
//            is FifoPlayer -> {
//                PlayType.FIFO.ordinal + 1
//            }
//            is PacifisticPlayer -> {
//                PlayType.PACIFISTIC.ordinal + 1
//            }
//            is AggressivePlayer -> {
//                PlayType.AGGRESSIVE.ordinal + 1
//            }
//            is SemiSmartPlayer -> {
//                PlayType.SEMISMART.ordinal + 1
//            }
//            else -> {
//                7
//            }
//        }
//    }

    fun createSeed(seedNumber: Int) {
        val arraySeeds = ArrayList<Seed>()
        for (c in gamecolorsId) {

            if (Config.isTest) {
                for (i in 1..4) {
                    arraySeeds.add(Seed(c, i, id))
                }
            } else {
                for (i in 1..seedNumber) {
                    val seed = Seed(c, i, id)
                    seed.createUi()
                    arraySeeds.add(seed)
                }
            }


        }
        homeSeed = arraySeeds
    }

    open fun addListenerToSeed(event: EventListener?) {
        for (s in homeSeed) {
            s.actor.addListener(event)
        }
        // TODO: Implement this method
    }


    open fun addSeedToTable(table: Table) {
        for (seed in homeSeed) {
            table.addActor(seed.bgActor)
            table.addActor(seed.actor)
            table.addActor(seed.animActor)

        }
    }


    fun getGameColors() = Array<GameColor>(gamecolorsId.size) {
        GameColor.values()[gamecolorsId[it]]
    }

    suspend fun getSeeds() = withContext(Dispatchers.Default) {


        Array(homeSeed.size) {
            val seed = homeSeed[it]
            SeedData(seed.id, seed.color.ordinal, seed.currentFloor.coord.x, seed.currentFloor.coord.y, seed.moveRemain)
        }
    }

    suspend fun getSeedOuts() = withContext(Dispatchers.Default) {


        Array(seedOut.size) {
            val seed = seedOut[it]
            SeedData(seed.id, seed.color.ordinal, seed.currentFloor.coord.x, seed.currentFloor.coord.y, seed.moveRemain)
        }
    }

    fun getData() = GlobalScope.async {
        val intArray = IntArray(gamecolorsId.size) {
            gamecolorsId[it]
        }
        PlayerData(id, intArray, name)
    }

    abstract fun chooseDice()
    abstract fun chooseSeed()
    abstract fun toss()

    override fun toString(): String {
        return "id is $id name is $name color is $gamecolorsId"
    }

}