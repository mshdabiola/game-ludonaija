package com.mshdabiola.naijaludo.screen.loading

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mshdabiola.naijaludo.asset.AssetName
import com.mshdabiola.naijaludo.asset.MassetDescriptor
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.config.GameManager
import com.mshdabiola.naijaludo.screen.NaijaLudo
import com.mshdabiola.naijaludo.screen.menu.MenuScreen
import com.mshdabiola.naijaludo.util.GdxUtils
import com.mshdabiola.naijaludo.util.ViewportUtils

class LoadingScreen(val naijaLudo: NaijaLudo) : Screen {

    private var flagFinished = false
    private val logger = Logger(LoadingScreen::class.java.name, Logger.DEBUG)

    val viewport = FitViewport(Config.WORDLD_WIDTH, Config.WORLD_HEIGHT)
//    val shapeRenderer = ShapeRenderer()

//    val debugCameraController = DebugCameraController().apply {
//        setStartPosition(Config.WORDLD_WIDTH_HALF, Config.WORLD_HEIGHT_HALF)
//    }


    val stage = Stage(viewport, naijaLudo.batch)
    val table = Table()


    var waitTime = 0.0f
    var progress = 0f

    val progressWith = 1200f
    val progressHeight = 200f
//    val progressX = (Config.WORDLD_WIDTH_HALF - progressWith / 2f)
//    val progressY = (Config.WORLD_HEIGHT_HALF - progressHeight / 2f)

    val skin = Skin(Gdx.files.internal(AssetName.loadingUISkin))

    val progressMAx = 100f
    val progressBar = ProgressBar(0f, progressMAx, 10f, false, skin)


    val assetManager = naijaLudo.assetManager


    init {

//        stage.isDebugAll = true
        initUi()
        stage.addActor(table)

//        assetManager.load(MassetDescriptor.gameBackGround)
        assetManager.load(MassetDescriptor.gameSkinn2)
//        assetManager.load(MassetDescriptor.uiSkin)
//        assetManager.load(MassetDescriptor.gameSkinn)
        assetManager.load(MassetDescriptor.purpleSkin)


    }

    override fun show() {


    }

    override fun render(delta: Float) {
//        debugCameraController.handleDebugInput(delta)
//        debugCameraController.applyTo(viewport.camera as OrthographicCamera)

        GdxUtils.clearScreen()



        stage.act()
        stage.draw()

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
//        draw()
//        shapeRenderer.end()

//        ViewportUtils.drawGrid(viewport, shapeRenderer, 100)
        update(delta)

        progressBar.value = progress * progressMAx

        if (flagFinished) {
//            MassetDescriptor.gameSkin = assetManager[MassetDescriptor.gameSkinn]
//            MassetDescriptor.loadingSkin = Skin(Gdx.files.internal(AssetName.loadingSkin))
            MassetDescriptor.purpleSkinn = naijaLudo.assetManager[MassetDescriptor.purpleSkin]
            MassetDescriptor.gameSkin2 = naijaLudo.assetManager[MassetDescriptor.gameSkinn2]
            naijaLudo.readNewGameLogic()
            GameManager.loadMusic()
            MassetDescriptor.moveOutSound = getSound("moveOut")
            MassetDescriptor.moveSound = getSound("moving")
            MassetDescriptor.diceSound = getSound("dice")
            MassetDescriptor.selectSound = getSound("select")
            MassetDescriptor.killSound = getSound("kill")
            naijaLudo.screen = MenuScreen(naijaLudo)
        }


    }

    fun getSound(str: String): Sound {
        return Gdx.audio.newSound(Gdx.files.internal("sound/$str.wav"))
    }


    private fun update(delta: Float) {
        waitFor(0)
        progress = assetManager.progress

        if (assetManager.update()) {
            waitTime -= delta
            logger.debug("wait time is $waitTime")
            if (waitTime < 0) {
                flagFinished = true
            }

        }

    }

    private fun waitFor(time: Int) {
        try {
            Thread.sleep(time.toLong())
        } catch (e: Exception) {
            logger.debug("wait for exception", e)
        }
    }


    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        ViewportUtils.debugPixelsPerUnit(viewport)

    }

    override fun dispose() {
        stage.dispose()
    }

    override fun hide() {
        dispose()
    }

    private fun initUi() {


        val producer = Image(skin.getDrawable("mshdabiola"))
        table.background = skin.getDrawable("bg")
//        table.color = skin.getColor("lightBlue")

        val firstTable = Table()
        firstTable.top()

        val secondTable = Table()

        secondTable.add(progressBar).width(progressWith).height(progressHeight)

        val thirdTable = Table()
        thirdTable.bottom()
        thirdTable.add(producer)


        table.padTop(200f)
        table.padBottom(200f)
        table.defaults().expandX().fillX()
        table.add(firstTable)
        table.row()
        table.add(secondTable).fill().expand()
        table.row()
        table.add(thirdTable)

        table.setFillParent(true)
        table.pack()
    }

}