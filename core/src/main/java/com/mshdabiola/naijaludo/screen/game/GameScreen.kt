package com.mshdabiola.naijaludo.screen.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mshdabiola.naijaludo.asset.MassetDescriptor.gameSkin2
import com.mshdabiola.naijaludo.asset.MassetDescriptor.purpleSkinn
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.config.GameManager
import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.board.Board
import com.mshdabiola.naijaludo.entity.display.OptionWindowNew
import com.mshdabiola.naijaludo.entity.display.OutComeDisplayPanel
import com.mshdabiola.naijaludo.entity.player.HumanPlayer
import com.mshdabiola.naijaludo.screen.NaijaLudo
import com.mshdabiola.naijaludo.screen.game.logic.FriendNewGameLogic
import com.mshdabiola.naijaludo.screen.game.logic.NewGameLogic
import com.mshdabiola.naijaludo.screen.menu.MenuScreen
import com.mshdabiola.naijaludo.util.GdxUtils
import com.mshdabiola.naijaludo.util.ViewportUtils
import com.mshdabiola.naijaludo.util.debug.DebugCameraController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class GameScreen(val naijaLudo: NaijaLudo, var gameLogic: GameLogic) : Screen, CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val logger = Logger(GameScreen::class.java.name, Logger.DEBUG)


    val assetManager = naijaLudo.assetManager

    val viewport = FitViewport(Config.WORDLD_WIDTH, Config.WORLD_HEIGHT)
    private val stage = Stage(viewport, naijaLudo.batch)

    private val debugCameraController = DebugCameraController().apply {
        setStartPosition(Config.WORDLD_WIDTH_HALF, Config.WORLD_HEIGHT_HALF)
    }

    private val nameRow = Table()
    private val nameRow2 = Table()
    val outComeTable = Table()
    val outComeTable2 = Table()

    val finishedWindow = OptionWindowNew("Finished ", skin = purpleSkinn)

    val optionWindow = OptionWindowNew("Options", skin = purpleSkinn)
    val windowTable = Table()


    var diceController = gameLogic.diceController
    var players = gameLogic.players
    var gameController = gameLogic.gameController

    private val gameTable = Table()
    private lateinit var boardTable: Table

    private lateinit var outComeDisplayPanel: OutComeDisplayPanel
    private lateinit var outComeDisplayPanelCopy: OutComeDisplayPanel

    companion object {
        var isFriend = false
    }

    val rotate = GameManager.rotate
    val animate = GameManager.animator
    val assistant = GameManager.assistant

    init {

        initUi()
        boardTable.toFront()
        initFinishedWindow()
        initOptionWindow()

        Seed.speed = GameManager.seedSpeed
        stage.addActor(gameTable)
        stage.addActor(windowTable.apply { setFillParent(true) })

    }

    fun changeWindow(win: Window) {
        windowTable.clear()
        windowTable.add(win).width(1200f)
        pause()
        win.isVisible = true
    }

    var changeScreen = Pair<Boolean, Screen>(false, this)

    var isPause = false
    var swap = true

    override fun show() {

        // stage.isDebugAll = true
        Gdx.input.inputProcessor = stage
        // add dice and outcome to stage

        naijaLudo.newGameLogic = gameLogic as? NewGameLogic
        initGame()
        //rotate board
        boardTable.setOrigin(750f, 750f)
        boardTable.rotation = 0f
        boardTable.addAction(Actions.rotateBy(getRotateDegree(players[0].gamecolorsId[0]), 0f))
    }

    fun initGame() {

        diceController.createUi()


        gameLogic.players.forEach {
            it.createUi()
        }

        isFriend = gameLogic is FriendNewGameLogic

        //create seed for new game
        if ((gameLogic is NewGameLogic || gameLogic is FriendNewGameLogic) && !gameLogic.update) {
            players.forEach {
                it.createSeed()
            }
        }

        //if his save game update on the table
        if (gameLogic.update) {
            players.forEach { it.updateUi() }
            diceController.updateUi()
            gameLogic.update = false
        }

        players.forEach { it.setController(controller = gameController) }

        gameController.exitFunction = {
            scorePlayer()
            gameLogic.finished = true
            changeWindow(finishedWindow)
        }
        gameController.swapOutcome = {

            if (players.size >= 3) {
                if (gameController.currentPlayerIndex == 2) {
                    outComeTable.reset()
                    outComeTable2.reset()
                    swapOutCome()
                } else if (gameController.currentPlayerIndex == 0) {
                    outComeTable.reset()
                    outComeTable2.reset()
                    swapOutCome()
                }
            } else {
                outComeTable.reset()
                outComeTable2.reset()
                swapOutCome()
            }
        }
        gameController.pause = {
            pause()
        }
        gameController.resume = {
            resume()
        }
        gameController.assistant = assistant
        gameController.animate = animate


        diceController.addDicetoTable(boardTable)

        //outCome to table
        outComeDisplayPanel = diceController.outcomeDiceOutcome
        outComeTable.add(outComeDisplayPanel).width(1000f)

        if (isFriend) {
            outComeTable.reset()
            outComeDisplayPanelCopy = diceController.outcomeDiceOutcomeCopy
            outComeTable.add(outComeDisplayPanelCopy).width(1000f)
            outComeTable2.add(outComeDisplayPanel).width(1000f)
        }


        //set player panels

        when (players.size) {
            1 -> {
                nameRow.add(players[0].playerPanel)
            }
            2 -> {
                nameRow.add(players[0].playerPanel).padRight(100f)
                nameRow.add(players[1].playerPanel)
            }
            3 -> {
                nameRow.add(players[0].playerPanel).padRight(100f)
                nameRow.add(players[1].playerPanel)
                nameRow2.add(players[2].playerPanel)
            }
            else -> {
                nameRow.add(players[0].playerPanel).padRight(100f)
                nameRow.add(players[1].playerPanel)
                nameRow2.add(players[3].playerPanel).padRight(100f)
                nameRow2.add(players[2].playerPanel)
            }
        }

        //add seed to the table
        players.forEach {
            it.addSeedToTable(boardTable)

            //add manySeedTable

            if (it is HumanPlayer) {
                it.addManySeedToTable(boardTable)
            }
        }

    }


    fun swapOutCome() {

        if (!swap) {
            outComeTable2.add(outComeDisplayPanel).width(1000f)
            outComeTable.add(outComeDisplayPanelCopy).width(1000f)
        }
        if (swap) {
            outComeTable.add(outComeDisplayPanel).width(1000f)
            outComeTable2.add(outComeDisplayPanelCopy).width(1000f)
        }
        swap = !swap
    }

    fun reset() {


        gameLogic.reset(players)

        nameRow.reset()
        nameRow2.reset()
        outComeTable.reset()
        outComeTable2.reset()
        boardTable.reset()

        diceController = gameLogic.diceController
        gameController = gameLogic.gameController
        gameController.winnerMap.clear()
        players = gameLogic.players

        initGame()

        pause()

        boardTable.rotation = 0f
        if (rotate) {
            boardTable.addAction(SequenceAction(
                    Actions.rotateBy(getRotateDegree(players[0].gamecolorsId[0]) + 360f, 2f),
                    Actions.run {
                        println("enter run")
                        resume()

                    }
            ))
        } else {
            boardTable.addAction(SequenceAction(
                    Actions.rotateBy(getRotateDegree(players[0].gamecolorsId[0]), 0f),
                    Actions.run {
                        println("enter run")
                        resume()

                    }
            ))
        }

//        gameLogic.tableDegree=boardTable.rotation

    }

    fun getRotateDegree(colorInde: Int) = when (colorInde) {
        0 -> 270f
        3 -> 180f
        1 -> 0f
        else -> 90f
    }

    override fun render(delta: Float) {
//        debugCameraController.applyTo(viewport.camera as OrthographicCamera)
//        debugCameraController.handleDebugInput(delta)

        GdxUtils.clearScreen()

        stage.act()
        stage.draw()
//        isPause=finishedWindow.isVisible
//        isPause = optionWindow.isVisible

        if (!isPause) {
            gameController.update(delta)
        }
//        ViewportUtils.drawGrid(viewport, naijaLudo.shapeRenderer, 100)

        if (changeScreen.first) {
            naijaLudo.screen = changeScreen.second

        }

    }


    override fun pause() {
        isPause = true
        if (gameLogic is NewGameLogic && !gameLogic.finished) {
            naijaLudo.newGameLogic = gameLogic as NewGameLogic
            naijaLudo.saveNewGameLogic()
        }
    }

    override fun resume() {
        isPause = false

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        ViewportUtils.debugPixelsPerUnit(viewport)
    }

    override fun dispose() {
        if (gameLogic is NewGameLogic) {
            naijaLudo.newGameLogic = gameLogic as NewGameLogic
            naijaLudo.saveNewGameLogic()
        }

        stage.dispose()


    }

    override fun hide() {
        dispose()
    }

    fun scorePlayer() {
        val id = gameController.playerId
        if (gameLogic is NewGameLogic) {
            with(players[id] as HumanPlayer) {
                if (lastPoint == 0 && players.size == 2) {
                    GameManager.lossOne = 1
                }
                if (lastPoint == 1 && players.size == 2) {
                    GameManager.winOne = 1
                }
                if (lastPoint == 0 && players.size > 2) {
                    GameManager.lossMany = 1
                }
                if (lastPoint == players.size - 1 && players.size > 2) {
                    GameManager.winMany = 1
                }
            }
        }
        if (gameLogic is FriendNewGameLogic) {
            with(players[id] as HumanPlayer) {

                if (lastPoint == 0) {
                    GameManager.lossMany = 1
                }
                if (lastPoint == players.size - 1) {
                    GameManager.winMany = 1
                }
            }
        }
    }

    private fun initUi() {
        gameTable.top()
        gameTable.name = "game Table"

        gameTable.background = purpleSkinn.getDrawable("bg")


        val topTable = Table()
        boardTable = Board().apply { name = "board table" }
        val bottomTable = Table()

        topTable.background = purpleSkinn.getDrawable("game-up")
        val optionButton = Button(purpleSkinn, "option")

        optionButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {

                changeWindow(optionWindow)
            }
        })

        topTable.top()
        topTable.row().padTop(25f)
        topTable.add(optionButton).width(100f).height(150f).align(Align.top).padLeft(15f)
        topTable.add(nameRow).growX()
        topTable.row().padTop(50f)
        topTable.add(outComeTable2).colspan(2)

        bottomTable.background = purpleSkinn.getDrawable("game-down")
        bottomTable.top()
        bottomTable.padTop(50f)
        bottomTable.add(outComeTable)
        bottomTable.row().padTop(50f)
        bottomTable.add(nameRow2).growX()

        boardTable.background = gameSkin2.getDrawable("board")

        topTable.pack()
        //add option button


        boardTable.pack()
        bottomTable.pack()

        gameTable.add(topTable).expandX().fillX().height(600f)
        gameTable.row()
        gameTable.add(boardTable).expandX().fillX().height(1500f)
        gameTable.row()
        gameTable.add(bottomTable).expandX().fillX().height(600f)
        gameTable.setFillParent(true)
        gameTable.pack()
    }

    private fun initOptionWindow() {


        optionWindow.cancelButtonFunction = {
            optionWindow.isVisible = false
            resume()
        }
        optionWindow.addButton("Restart") {

//            changeScreen = Pair(true, GameScreen(naijaLudo))
        }

        optionWindow.addButton("Reset") {
            reset()
            optionWindow.isVisible = false

        }
        optionWindow.addButton("Resign") {

            changeScreen = Pair(true, MenuScreen(naijaLudo))

        }
        optionWindow.addButton("Exit") {
            changeScreen = Pair(true, MenuScreen(naijaLudo))

        }
    }

    private fun initFinishedWindow() {
        finishedWindow.clearButtonTable()
        finishedWindow.addButton("Play Again") {
            reset()
            finishedWindow.isVisible = false
        }
        finishedWindow.addButton("Home") {
            changeScreen = Pair(true, MenuScreen(naijaLudo))
        }
    }

}