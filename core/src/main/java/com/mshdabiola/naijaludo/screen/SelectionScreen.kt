package com.mshdabiola.naijaludo.screen

//class SelectionScreen(val naijaLudo: NaijaLudo) : Screen {
//
//    private var flagFinished = false
//    private val logger = Logger(SelectionScreen::class.java.name, Logger.DEBUG)
//
//    val viewport = FitViewport(Config.WORDLD_WIDTH, Config.WORLD_HEIGHT)
//    val shapeRenderer = ShapeRenderer()
//
//    val debugCameraController = DebugCameraController().apply {
//        setStartPosition(Config.WORDLD_WIDTH_HALF, Config.WORLD_HEIGHT_HALF)
//    }
//
//
//    val stage = Stage(viewport, naijaLudo.batch)
//    val table = Table()
//    val loading = Table()
//
//    val skin = naijaLudo.assetManager[MassetDescriptor.gameSkin]
//
//
//    init {
//
//        Gdx.input.inputProcessor = stage
//        stage.isDebugAll = true
//        initUi()
//        stage.addActor(table)
//
//
//    }
//
//    override fun show() {
//
//
//    }
//
//    override fun render(delta: Float) {
//        debugCameraController.handleDebugInput(delta)
//        debugCameraController.applyTo(viewport.camera as OrthographicCamera)
//
//        GdxUtils.clearScreen()
//
//
//
//        stage.act()
//        stage.draw()
//
//        ViewportUtils.drawGrid(viewport, shapeRenderer, 100)
//
//
//        if (flagFinished) {
//            naijaLudo.screen = GameScreen(naijaLudo)
//        }
//
//
//    }
//
//
//    override fun pause() {
//
//    }
//
//    override fun resume() {
//
//    }
//
//    override fun resize(width: Int, height: Int) {
//        viewport.update(width, height, true)
//        ViewportUtils.debugPixelsPerUnit(viewport)
//
//    }
//
//    override fun dispose() {
//        stage.dispose()
//    }
//
//    override fun hide() {
//        dispose()
//    }
//
//    private fun initUi() {
//
//        val ninePatchDrawable = NinePatchDrawable(NinePatch(Texture(Gdx.files.internal("$dir/fill.9.png"))))
//        val selectionbg = TextureRegionDrawable(Texture(Gdx.files.internal("$dir/select background.png")))
//
//        val buttonStyle = TextButton.TextButtonStyle()
//        buttonStyle.up = TextureRegionDrawable(Texture(Gdx.files.internal("${LoadingScreen.dir}/loading.9.png")))
//        buttonStyle.down = TextureRegionDrawable(Texture(Gdx.files.internal("${LoadingScreen.dir}/loading.9.png")))
//        buttonStyle.font = skin.getFont("default-font")
//
//
//
//
//        table.background = ninePatchDrawable
//        table.color = Color.LIGHT_GRAY
//
//
//        val firstTable = Table(skin)
//        firstTable.add("Game Option")
//
//
//        val secondTable = Table(skin)
//        secondTable.bottom()
//        secondTable.background = selectionbg
//        val row1secondTable = Table()
//        row1secondTable.defaults().width(600f).pad(50f)
//        row1secondTable.add(TextButton("Diagonal", buttonStyle).apply { color = Color.RED })
//        row1secondTable.add(TextButton("Vertical", buttonStyle).apply { color = Color.RED })
//        val row2secondTable = Table()
//        row2secondTable.defaults().width(600f).pad(50f)
//        row2secondTable.add(TextButton("Horizontal", buttonStyle).apply { color = Color.RED })
//        row2secondTable.add(TextButton("8 in one", buttonStyle).apply { color = Color.RED })
//
//        secondTable.add("Game type")
//        secondTable.row()
//        secondTable.add(row1secondTable)
//        secondTable.row()
//        secondTable.add(row2secondTable)
//
//
//        val thirdTable = Table(skin)
//        thirdTable.background = selectionbg
//        thirdTable.bottom()
//        val row1thirdTable = Table()
//        row1thirdTable.defaults().width(300f).pad(50f)
//        row1thirdTable.add(TextButton("2", buttonStyle).apply { color = Color.GREEN })
//        row1thirdTable.add(TextButton("3", buttonStyle).apply { color = Color.GREEN })
//        row1thirdTable.add(TextButton("4", buttonStyle).apply { color = Color.GREEN })
//
//        thirdTable.add("Number Player")
//        thirdTable.row()
//        thirdTable.add(row1thirdTable)
//
//        val fourTable = Table(skin)
//        fourTable.background = selectionbg
//        fourTable.bottom()
//        val row1fourTable = Table()
//        row1fourTable
//        row1fourTable.defaults().width(600f).pad(50f)
//        row1fourTable.add(TextButton("Aggressive", buttonStyle).apply { color = Color.BLUE })
//        row1fourTable.add(TextButton("Fifo", buttonStyle).apply { color = Color.BLUE })
//        val row2fourTable = Table()
//        row2fourTable.defaults().width(600f).pad(50f)
//        row2fourTable.add(TextButton("Human", buttonStyle).apply { color = Color.BLUE })
//        row2fourTable.add(TextButton("Pacifistic", buttonStyle).apply { color = Color.BLUE })
//
//        fourTable.add("Choose color")
//        fourTable.row()
//        fourTable.add(row1fourTable)
//        fourTable.row()
//        fourTable.add(row2fourTable)
//
//        val fifthTable = Table()
//        val row1fiveTable = Table()
//        row1fiveTable.defaults().width(600f)
//        row1fiveTable.add(TextButton("Play", buttonStyle).apply { color = Color.PINK })
//        fifthTable.add(row1fiveTable)
//
//        table.top()
//
//
//        table.padTop(100f)
//        table.defaults().expand().fill().spaceBottom(50f)
//        table.add(firstTable).height(300f)
//        table.row()
//        table.add(secondTable).height(600f)
//        table.row()
//        table.add(thirdTable).height(330f)
//        table.row()
//        table.add(fourTable).height(600f)
//        table.row()
//        table.add(fifthTable)
//
//        //test window
//        table.addActor(OptionWindow("name", Table().apply { }, skin))
//
//
//        table.setFillParent(true)
//        table.pack()
//    }
//
//    companion object {
//        val dir = "ludo/data512_1024/ludo"
//    }
//}