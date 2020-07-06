package com.mshdabiola.naijaludo.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.entity.board.Board
import com.mshdabiola.naijaludo.util.GdxUtils
import com.mshdabiola.naijaludo.util.ViewportUtils
import com.mshdabiola.naijaludo.util.debug.DebugCameraController


class GameBoardTest(val naijaLudo: NaijaLudo) : ScreenAdapter() {

    val assetManager = naijaLudo.assetManager
    val viewport = FitViewport(Config.WORDLD_WIDTH, Config.WORLD_HEIGHT)

    val stage = Stage(viewport)
    val table = Table()

    lateinit var board: Board

    val shapeRenderer = ShapeRenderer()

    val debugCameraController = DebugCameraController().apply {
        setStartPosition(Config.WORDLD_WIDTH_HALF, Config.WORLD_HEIGHT_HALF)
    }

    override fun show() {

        stage.isDebugAll = true
        Gdx.input.inputProcessor = stage

        initUi()

        stage.addActor(table)
        //  board.setPostion(table)
    }


    override fun render(delta: Float) {
        debugCameraController.handleDebugInput(delta)
        debugCameraController.applyTo(viewport.camera as OrthographicCamera)

        GdxUtils.clearScreen()
        stage.act()
        stage.draw()

        ViewportUtils.drawGrid(viewport, shapeRenderer, 100)

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        ViewportUtils.debugPixelsPerUnit(viewport)

    }

    override fun dispose() {
        shapeRenderer.dispose()
    }


    override fun hide() {
        dispose()
    }

    private fun initUi() {

        table.top()

        board = Board()

        table.add(board)
        // board.setPostion(table)
        table.setFillParent(true)
        table.pack()
    }
}