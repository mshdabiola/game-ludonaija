package com.mshdabiola.naijaludo.entity.player

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.mshdabiola.naijaludo.asset.MassetDescriptor
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.config.GameManager
import com.mshdabiola.naijaludo.config.GameState
import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.board.Floor
import com.mshdabiola.naijaludo.entity.dice.DiceOutcome
import com.mshdabiola.naijaludo.screen.game.GameController


open class HumanPlayer(id: Int, gameColor: IntArray, name: String = "") : BasePlayer(name, id, gameColor) {


    constructor() : this(0, intArrayOf(), "")

    @Transient
    lateinit var manySeedTable: Table


    init {
        println("enter human player")
        if (!Config.isTest) {
            super.name = GameManager.name
        } else {
            super.name = "Human players"
        }

    }

    var locX = 0f
    var locY = 0f

//    var lastPoint = 0

    @Transient
    var diceListener = object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            diceController.toss {
                diceController.diceTouchable(false)
                gameController.currentState = GameState.HASTOSS
            }

        }
    }

    @Transient
    var diceOutcomeListener = object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
            val index = (actor as DiceOutcome).id
            gameController.currentDiceNo = diceController.getDiceValue(index)
            gameController.currentDiceIndex = index
            diceController.diceDisplayTouchable(false)

            gameController.currentState = GameState.HASCHOOSEDICE
        }
    }


    override fun chooseDice() {

        diceController.diceDisplayTouchable(true)
    }


    override fun chooseSeed() {
        gameController.activateSeedNew(gameController.currentDiceNo)
//        homeSeed.forEach { it.actor.touchable = Touchable.enabled }

    }

    override fun toss() {
        diceController.diceTouchable(true)
    }

    override fun createUi() {
        super.createUi()
        iconId = GameManager.avatar
        manySeedTable = Table().apply {
            setSize(220f, 120f)
            setPosition(750f, 750f)
            name = ""
            background = MassetDescriptor.gameSkin2.getDrawable("manySeedBg")


        }
    }

    override fun updateUi() {
        super.updateUi()
        manySeedTable = Table().apply {
            setSize(220f, 120f)
            setPosition(750f, 750f)
            name = ""
            background = MassetDescriptor.gameSkin2.getDrawable("manySeedBg")


        }
    }

    override fun setController(controller: GameController) {
        super.setController(controller)
//        logger.debug("setPlayerController")

        diceController.addListenerToDice(diceListener)
        diceController.addListenerToOutCome(diceOutcomeListener)
        homeSeed.forEach {
//            logger.debug("add listener to seed")
            (it.actor as Button).addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    super.clicked(event, x, y)
//                    logger.debug("seed is Click")

                    if (gameController.isManySeedInThisArea(it.currentFloor)) {
                        showManyTable(it.currentFloor)
                    } else {
                        it.actor.toFront()
                        gameController.currentSeed = it

                        gameController.deactivateSeed()
                        gameController.currentState = GameState.HASCHOOSESEED
                    }


                }
            })
        }


    }

    private fun showManyTable(currentFloor: Floor) {
        with(manySeedTable) {
            toFront()
            addAction(Actions.fadeIn(0.0f))
            touchable = Touchable.enabled
            val vec = currentFloor.coord
            locX = vec.x
            locY = vec.y
//            location = Pair(vec.x, vec.y)
            if (vec.x >= 1400f) {
                setPosition(vec.x - 100, vec.y)

            } else {
                setPosition(vec.x, vec.y)
            }

            addAction(Actions.sequence(
                    Actions.delay(2f, Actions.fadeOut(0.01f)),
                    Actions.run {
                        touchable = Touchable.disabled
                        toBack()
                    }
            ))
        }


    }

    fun addManySeedToTable(table: Table) {
//        manySeedTable.isVisible=false

        gamecolorsId.forEach { n ->

            val seed = Seed(n, 0, 1)
            seed.createUi()
            seed.actor.touchable = Touchable.enabled

            seed.actor.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    println(" seed click ${seed.colorId} and x are $locX ${manySeedTable.x} and y are $locY ${manySeedTable.y}")
                    val seed = homeSeed.find {
                        it.colorId == seed.colorId
                                && it.currentFloor.coord.x == locX
                                && it.currentFloor.coord.y == locY
                    }
                    seed!!.let {
                        manySeedTable.addAction(Actions.fadeOut(0.0f))
                        it.actor.toFront()
                        gameController.currentSeed = it

                        gameController.deactivateSeed()
                        gameController.currentState = GameState.HASCHOOSESEED
                    }
                }
            })
            manySeedTable.add(seed.actor)
        }
        manySeedTable.touchable = Touchable.disabled

        table.addActor(manySeedTable)
        manySeedTable.addAction(Actions.fadeOut(0f))
    }


}