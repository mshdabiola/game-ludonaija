package com.mshdabiola.naijaludo.screen.menu

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mshdabiola.naijaludo.asset.MassetDescriptor.purpleSkinn
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.config.GameManager
import com.mshdabiola.naijaludo.config.PlayType
import com.mshdabiola.naijaludo.entity.connection.Factory
import com.mshdabiola.naijaludo.entity.connection.Join
import com.mshdabiola.naijaludo.entity.display.NewSettingWindow
import com.mshdabiola.naijaludo.entity.display.OptionWindowNew
import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.entity.player.HumanPlayer
import com.mshdabiola.naijaludo.entity.player.computer.*
import com.mshdabiola.naijaludo.entity.player.online.OnlineHumanPlayer
import com.mshdabiola.naijaludo.screen.NaijaLudo
import com.mshdabiola.naijaludo.screen.game.GameController
import com.mshdabiola.naijaludo.screen.game.GameScreen
import com.mshdabiola.naijaludo.screen.game.logic.FriendNewGameLogic
import com.mshdabiola.naijaludo.screen.game.logic.NewGameLogic
import com.mshdabiola.naijaludo.util.GdxUtils
import com.mshdabiola.naijaludo.util.ViewportUtils
import com.mshdabiola.naijaludo.util.debug.DebugCameraController

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch

class MenuScreen(val naijaLudo: NaijaLudo) : ScreenAdapter(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val logger = Logger(MenuScreen::class.java.name, Logger.DEBUG)


    val assetManager = naijaLudo.assetManager
    val viewport = FitViewport(Config.WORDLD_WIDTH, Config.WORLD_HEIGHT)

    val stage = Stage(viewport, naijaLudo.batch)
    val table = Table()

    val winOne = GameManager.winOne
    val lossOne = GameManager.lossOne
    val winMany = GameManager.winMany
    val lossMany = GameManager.lossMany
    val winFriend = GameManager.winFriend
    val lossFriend = GameManager.lossFriend


    var icon = ImageButton(purpleSkinn, "icon-${GameManager.avatar}")
    val label = Label(GameManager.name, purpleSkinn).apply {
        setFontScale(0.6f)
        color = purpleSkinn.getColor("orange")
        setEllipsis(true)
    }

    val iconProfile = ImageButton(purpleSkinn, "icon-${GameManager.avatar}")
    val labelProfile = Label(GameManager.name, purpleSkinn, "red")

    val windowTable = Table()
    var change = Pair<Boolean, Screen>(false, this)

    val debugCameraController = DebugCameraController().apply {
        setStartPosition(Config.WORDLD_WIDTH_HALF, Config.WORLD_HEIGHT_HALF)
    }

    val playerArray = ArrayList<BasePlayer>()
    var isCrossGame = true
    var style = GameManager.style

    var onlineHumanPlayer: OnlineHumanPlayer? = null

    //    var onlineHumanPlayer2: OnlineHumanPlayer? = null
    val join = Join(getNewPlayer(PlayType.HUMAN) as HumanPlayer).apply {
        setPlayerListUpdate(updatePlayerActorJoin())
    }
    val updatePlayerActor = updatePlayerActor()


    override fun show() {

//        stage.isDebugAll = true
        Gdx.input.inputProcessor = stage





        newInitUi()

        naijaLudo.readNewGameLogic()
        naijaLudo.runClient = { ip: String, isOwner: Boolean ->

            if (isOwner) {

                naijaLudo.server.updateActor = updatePlayerActor
                addOnlinePlayer(PlayType.HUMAN)
                naijaLudo.server.connect()
            } else {
                join.connect(ip)
            }
        }

        windowTable.setFillParent(true)

        stage.addActor(table)
        stage.addActor(windowTable)
        if (GameManager.isFirst) {
            changeWindow(editProfile)
            GameManager.isFirst = false
        }

    }

    override fun render(delta: Float) {
//        debugCameraController.handleDebugInput(delta)
//        debugCameraController.applyTo(viewport.camera as OrthographicCamera)
        GdxUtils.clearScreen()
        stage.act()
        stage.draw()

//        ViewportUtils.drawGrid(viewport, naijaLudo.shapeRenderer, 100)

        if (change.first) {
            naijaLudo.screen = change.second
        }
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


    private fun newInitUi() {
        table.top()
        table.background = purpleSkinn.getDrawable("bg")
        val row1headTable = Table()


        icon.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                icon.isChecked = false
                changeWindow(profileWindow)
            }
        })

        val iconTable = Table()
        iconTable.add(icon).size(200f)
        iconTable.row()
        iconTable.add(label).maxWidth(200f)

        row1headTable.add(iconTable).expandX().align(Align.left).width(250f)

        val heading = Table()

        heading.background = purpleSkinn.getDrawable("heading-bg")


        heading.add(row1headTable).padLeft(25f).padRight(25f).growX()
        table.add(heading).size(1500f, 300f).padBottom(50f)
        table.row()

        val row1Table = Table()
        row1Table.add(ImageButton(purpleSkinn, "exit").apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
//                    naijaLudo.saveNewGameLogic()
                    Gdx.app.exit()
                }
            })
        }).expandX().align(Align.left).size(200f)
//        val adsImage = Image(purpleSkinn.getDrawable("white20"))
//        row1Table.add(adsImage).height(200f).growX()

        //hide the group button
        row1Table.add(ImageButton(purpleSkinn, "group").apply { isVisible = false }).expandX().align(Align.right).size(200f)


        //hide this table
        val row2Table = Table().apply { isVisible = false }
        row2Table.add(ImageButton(purpleSkinn, "ads")).expandX().align(Align.left).size(200f)
//        val adsImage2 = Image(purpleSkinn.getDrawable("white20"))
//        row2Table.add(adsImage2).height(200f).growX()
        row2Table.add(ImageButton(purpleSkinn, "phone")).expandX().align(Align.right).size(200f)


        val thirdTable = Table()
        thirdTable.background = purpleSkinn.getDrawable("logo")
        thirdTable.top()
        thirdTable.add(row1Table).growX().padTop(50f)
        thirdTable.row()
        thirdTable.add(row2Table).growX().padTop(50f)

        table.add(thirdTable).growX().height(1150f).padRight(50f).padLeft(50f)
        table.row()

        val buttonTable = Table()
        val computerButton = Button(purpleSkinn, "computer")
        computerButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                playerArray.clear()
                addPlayer(HumanPlayer(0, intArrayOf()))
                addPlayer(getNewPlayer(PlayType.PACIFISTIC))
//                computerGroupButton.buttons[PlayType.RANDOM.ordinal].isChecked = true
                changeWindow(computerWindow)
            }
        })
        val friendButton = Button(purpleSkinn, "friend")
        friendButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                playerArray.clear()
                addPlayer(HumanPlayer(0, intArrayOf()))
                updateFriendTable()

                computerGroupButton.buttons.forEach {
                    it.removeListener(oneComputerEventListener)
                    it.removeListener(manyComputerEventListener)
                    it.removeListener(onlineComputerEventListener)
                    it.addListener(friendComputerEventListener)
                }
                computerGroupButton.uncheckAll()
                changeWindow(friendsWindow)
            }
        })
        val multiplayerButton = Button(purpleSkinn, "friend")
        multiplayerButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                computerGroupButton.buttons.forEach {
                    it.removeListener(oneComputerEventListener)
                    it.removeListener(manyComputerEventListener)
                    it.removeListener(friendComputerEventListener)
                    it.addListener(onlineComputerEventListener)
                }
                computerGroupButton.uncheckAll()
                changeWindow(multiplayerWindow)
            }
        })
        buttonTable.add(computerButton).size(300f).padRight(300f)
        buttonTable.add(friendButton).size(300f)
        buttonTable.row()
        //for version1
//        buttonTable.add(multiplayerButton).padTop(50f).colspan(2)

        table.add(buttonTable).growX().height(700f).padBottom(50f)
        table.row()

        val lastTable = Table()
        val setting = ImageButton(purpleSkinn, "setting")
        setting.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                changeWindow(newSettingWindow)
            }
        })
        val share = ImageButton(purpleSkinn, "share")
        val more = ImageButton(purpleSkinn, "more")
        lastTable.defaults().size(200f)
        lastTable.add(share)
        lastTable.add(setting)
        lastTable.add(more)

        table.add(lastTable)

        table.setFillParent(true)
        table.pack()
    }

    fun getNewPlayer(type: PlayType): BasePlayer {
        return when (type) {
            PlayType.PACIFISTIC -> {
                PacifisticPlayer(0, intArrayOf())
            }
            PlayType.RANDOM -> {
                RandomPlayer(0, intArrayOf())
            }
            PlayType.FIFO -> {
                FifoPlayer(0, intArrayOf())
            }
            PlayType.SEMISMART -> {
                SemiSmartPlayer(0, intArrayOf())
            }
            PlayType.AGGRESSIVE -> {
                AggressivePlayer(0, intArrayOf())
            }
            PlayType.HUMAN -> {
                HumanPlayer(0, intArrayOf())
            }

        }
    }

    fun getColors(playerSize: Int): Array<IntArray> {
        return when (playerSize) {
            1 -> {
                arrayOf(intArrayOf(0, 1, 2, 3))
            }
            2 -> {
                when (style) {
                    0 -> {
                        arrayOf(intArrayOf(0, 2), intArrayOf(1, 3))
                    }
                    1 -> {
                        arrayOf(intArrayOf(0, 1), intArrayOf(2, 3))
                    }
                    else -> {
                        arrayOf(intArrayOf(0, 3), intArrayOf(1, 2))
                    }
                }
            }
            3 -> {
                arrayOf(intArrayOf(0), intArrayOf(1), intArrayOf(2))
            }
            else -> {
                arrayOf(intArrayOf(0), intArrayOf(1), intArrayOf(2), intArrayOf(3))
            }
        }
    }

    fun addPlayer(player: BasePlayer) {
        playerArray.add(player)
        val playerSize = playerArray.size
        val colors = getColors(playerSize)
        playerArray.forEachIndexed { index, basePlayer ->
            basePlayer.id = index
            if (index != 0) {
                basePlayer.iconId = changePlayerToIcon(basePlayer)
            } else {
                basePlayer.iconId = GameManager.avatar
            }
            basePlayer.gamecolorsId = colors[index]
        }

    }

    fun addOnlinePlayer(playerType: PlayType) {
        val server = naijaLudo.server
        if (server.isRunning) {
            val player = getNewPlayer(playerType)
            server.addLocalPlayer(player)
            playerArray.clear()
            playerArray.addAll(server.serverFactory.playerArray)
            updateHostTable()
        }
    }

    fun removeOnlinePlayer(removeIndex: Int) {
        val server = naijaLudo.server
        if (server.isRunning) {
            server.removeLocalPlayer(removeIndex)
            playerArray.clear()
            playerArray.addAll(server.serverFactory.playerArray)
            updateHostTable()
        }
    }

    fun changeWindow(win: Window) {

        windowTable.clear()
        windowTable.add(win).width(1200f).maxHeight(2200f)
        win.isVisible = true
    }

    fun getPlayerThumbnail(playerType: PlayType): ImageButton {
        val index = playerType.ordinal + 1
        return getIcon(index)
    }

    fun getIcon(index: Int) = ImageButton(purpleSkinn, "icon-$index")

    fun changePlayerToIcon(player: BasePlayer): Int {
        return when (player) {
            is RandomPlayer -> {
                PlayType.RANDOM.ordinal + 1
            }
            is FifoPlayer -> {
                PlayType.FIFO.ordinal + 1
            }
            is PacifisticPlayer -> {
                PlayType.PACIFISTIC.ordinal + 1
            }
            is AggressivePlayer -> {
                PlayType.AGGRESSIVE.ordinal + 1
            }
            is SemiSmartPlayer -> {
                PlayType.SEMISMART.ordinal + 1
            }
            else -> {
                7
            }
        }
    }

    fun getTableforWIn(heading: String, win: String, loss: String): Table {
        val up = Image(purpleSkinn.getDrawable("up-thump"))
        val down = Image(purpleSkinn.getDrawable("down-thump"))

        return Table(purpleSkinn).apply {
            background = skin.getDrawable("center")
            add(heading, "center").align(Align.center).colspan(6).expandX()
            row()
            add(up)
            add("Win ", "center")
            add(win, "red")
            add(down)
            add("Loss", "center")
            add(loss, "red")
        }
    }

    fun updateIcon() {
//    icon=ImageButton(purpleSkinn, "icon-${GameManager.avatar}")
        icon.style = purpleSkinn.get("icon-${GameManager.avatar}", ImageButton.ImageButtonStyle::class.java)

        label.setText(GameManager.name)

        iconProfile.style = purpleSkinn.get("icon-${GameManager.avatar}", ImageButton.ImageButtonStyle::class.java)
        labelProfile.setText(GameManager.name)
    }

    fun updateOneTable() {
        with(oneComputerWindow) {
            table.clear()
            table.defaults().uniform(false, false)

            if (playerArray.size == 2) {
                val playerTable = Table(purpleSkinn)


                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).maxWidth(400f).align(Align.center)

                table.add(playerTable).width(400f).pad(25f)
                table.row()
                addLabel("VS")
                val robotTable = Table(purpleSkinn)

//                val robotImage = ImageButton(skin, "icon-${computerGroupButton.checkedIndex + 1}")
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")
                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                val changeButton = Button(skin, "change").apply {
                    setSize(100f, 100f)
                    setPosition(150f, nameLabel2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            changeWindow(listOfComputerWindow)
                        }
                    })
                }
                robotTable.addActor(changeButton)
                table.add(robotTable).width(400f).pad(25f)
//
                table.row()
                table.add(Label("click icon button to change type", skin, "red-1").apply { setFontScale(0.4f) }).uniform(false, false)
            }

            clearButtonTable()
            addReturnButton()
            addPlayButton("PLAY") {
                val players = Array(playerArray.size) {
                    playerArray[it]
                }
                val logic = NewGameLogic(players = players, gameController = GameController())
                change = Pair(true, GameScreen(naijaLudo, logic))


            }

        }

    }

    fun updateManyTable() {
        with(manyComputerWindow) {
            table.clear()
            table.defaults().uniform(false, false)

            if (playerArray.size == 2) {


                val changeLabel = TextButton("Add Robot", skin)
                changeLabel.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        changeWindow(listOfComputerWindow)
                    }
                })
                table.add(changeLabel).width(1100f).uniform(false, false)
                table.row()
                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).maxWidth(400f).align(Align.center)

                table.add(playerTable).width(400f).pad(25f)
                table.row()

                addLabel("VS")

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")

                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                val changeButton = Button(skin, "change").apply {
                    setSize(100f, 100f)

                    setPosition(150f, nameLabel2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(1)
                            changeWindow(listOfComputerWindow)
                        }
                    })
                }
                robotTable.addActor(changeButton)
                table.add(robotTable).width(400f).pad(25f)

                table.row()
//                table.add(Label("click icon button to change type",skin,"red-1").apply { setFontScale(0.4f) }).uniform(false,false)
            }

            if (playerArray.size == 3) {
                table.row().colspan(2)
                val changeLabel = TextButton("Add Robot", skin)
                changeLabel.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        changeWindow(listOfComputerWindow)
                    }
                })
                table.add(changeLabel).width(1100f)
                table.row()

                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).maxWidth(400f).align(Align.center)
                table.add(playerTable).width(400f).padRight(50f)

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")

                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                val changeButton = Button(skin, "change").apply {
                    setSize(100f, 100f)
                    setPosition(150f, nameLabel2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(1)
                            changeWindow(listOfComputerWindow)
                        }
                    })
                }
                robotTable.addActor(changeButton)
                table.add(robotTable).width(400f).padLeft(50f)

                table.row().colspan(2)
                addLabel("VS")

                val robot2Table = Table(purpleSkinn)
                val robot2Image = ImageButton(skin, "icon-${playerArray[2].iconId}")

                robot2Image.touchable = Touchable.disabled
                robot2Table.add(robot2Image).size(300f)

                robot2Table.row()
                val name2Label2 = Label(playerArray[2].name, skin, "center")
                name2Label2.setFontScale(0.8f)
                robot2Table.add(name2Label2).maxWidth(400f).align(Align.center)
                val cancel2Button = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, name2Label2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(2)
                            updateManyTable()
                        }
                    })
                }
                robot2Table.addActor(cancel2Button)
                table.add(robot2Table).width(400f).pad(25f)

                table.row()
//                table.add(TextButton("Add Computer",skin).apply { addListener(object : ChangeListener() {
//                    override fun changed(event: ChangeEvent?, actor: Actor?) {
//                        changeWindow(listOfComputerWindow)
//                    }
//                }) })
            }
            if (playerArray.size == 4) {
//
                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).align(Align.center)
                table.add(playerTable).width(400f).padRight(50f)

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")

                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                val changeButton = Button(skin, "change").apply {
                    setSize(100f, 100f)
                    setPosition(150f, nameLabel2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(1)
                            changeWindow(listOfComputerWindow)
                        }
                    })
                }
                robotTable.addActor(changeButton)
                table.add(robotTable).width(400f).padLeft(50f)

                table.row().colspan(2)
                addLabel("VS")

                val robot2Table = Table(purpleSkinn)
                val robot2Image = ImageButton(skin, "icon-${playerArray[2].iconId}")

                robot2Image.touchable = Touchable.disabled
                robot2Table.add(robot2Image).size(300f)

                robot2Table.row()
                val name2Label2 = Label(playerArray[2].name, skin, "center")
                name2Label2.setFontScale(0.8f)
                robot2Table.add(name2Label2).maxWidth(400f).align(Align.center)
                val cancel2Button = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, name2Label2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(2)
                            updateManyTable()
                        }
                    })
                }
                robot2Table.addActor(cancel2Button)
                table.add(robot2Table).width(400f).padRight(50f)

                val robot3Table = Table(purpleSkinn)
                val robot3Image = ImageButton(skin, "icon-${playerArray[3].iconId}")

                robot3Image.touchable = Touchable.disabled
                robot3Table.add(robot3Image).size(300f)

                robot3Table.row()
                val name3Label2 = Label(playerArray[3].name, skin, "center")
                name3Label2.setFontScale(0.8f)
                robot3Table.add(name3Label2).maxWidth(400f).align(Align.center)
                val cancel3Button = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, name3Label2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(3)
                            updateManyTable()
                        }
                    })
                }
                robot3Table.addActor(cancel3Button)
                table.add(robot3Table).width(400f).padLeft(50f)

                table.row()
//                table.add(TextButton("Add Computer",skin).apply { addListener(object : ChangeListener() {
//                    override fun changed(event: ChangeEvent?, actor: Actor?) {
//                        changeWindow(listOfComputerWindow)
//                    }
//                }) })
            }

            clearButtonTable()
            addReturnButton()
            addPlayButton("PLAY") {
                val players = Array(playerArray.size) {
                    playerArray[it]
                }
                change = Pair(true, GameScreen(naijaLudo, NewGameLogic(players = players)))

            }


        }

    }

    fun updateFriendTable() {
        with(friendsWindow) {
            table.clear()
            table.defaults().uniform(false, false)

            if (playerArray.size == 1) {
                val playerName = TextField("", purpleSkinn)
                playerName.maxLength = 9
                playerName.messageText = "Enter frnd name"
                val addPlayer = TextButton("Add", purpleSkinn)
                addPlayer.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        val name = playerName.text
                        if (name.length == 0) {
                            val newPlayer = getNewPlayer(PlayType.HUMAN)
                            newPlayer.name = "Player ${playerArray.size}"
                            addPlayer(newPlayer)
                            updateFriendTable()

                        } else {
                            val newPlayer = getNewPlayer(PlayType.HUMAN)
                            newPlayer.name = name
                            addPlayer(newPlayer)
                            updateFriendTable()
                        }
                    }
                })
                val nameTable = Table().apply {
                    add(playerName).width(800f)
                    add(addPlayer)
                }
                table.add(nameTable)

                table.row()

                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)


                playerTable.add(nameLabel).maxWidth(400f).align(Align.center)

                table.add(playerTable).width(400f).pad(25f)
                table.row()

            }
            if (playerArray.size == 2) {
                val playerName = TextField("", purpleSkinn)
                playerName.maxLength = 9
                playerName.messageText = "Enter frnd name"
                val addPlayer = TextButton("Add", purpleSkinn)
                addPlayer.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        val name = playerName.text
                        if (name.isEmpty()) {
                            val newPlayer = getNewPlayer(PlayType.HUMAN)
                            newPlayer.name = "Player ${playerArray.size}"
                            addPlayer(newPlayer)
                            updateFriendTable()

                        } else {
                            val newPlayer = getNewPlayer(PlayType.HUMAN)
                            newPlayer.name = name
                            addPlayer(newPlayer)
                            updateFriendTable()
                        }
                    }
                })
                val nameTable = Table().apply {
                    add(playerName).width(800f)
                    add(addPlayer)
                }
                table.add(nameTable)
                table.row()

                val changeLabel = TextButton("Add Robot", skin)
                changeLabel.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        changeWindow(listOfComputerWindow)
                    }
                })
                table.add(changeLabel)
                table.row()

                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).maxWidth(400f).align(Align.center)

                table.add(playerTable).width(400f).pad(25f)
                table.row()

                addLabel("VS")

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")

                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                val cancelButton = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, nameLabel2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(1)
                            updateFriendTable()
                        }
                    })
                }
                robotTable.addActor(cancelButton)
                table.add(robotTable).width(400f).pad(25f)

                table.row()
//                table.add(Label("click icon button to change type",skin,"red-1").apply { setFontScale(0.4f) }).uniform(false,false)
            }

            if (playerArray.size == 3) {
                val playerName = TextField("", purpleSkinn)
                playerName.maxLength = 9
                playerName.messageText = "Enter frnd name"
                val addPlayer = TextButton("Add", purpleSkinn)
                addPlayer.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        val name = playerName.text
                        if (name.length == 0) {
                            val newPlayer = getNewPlayer(PlayType.HUMAN)
                            newPlayer.name = "Player ${playerArray.size}"
                            addPlayer(newPlayer)
                            updateFriendTable()

                        } else {
                            val newPlayer = getNewPlayer(PlayType.HUMAN)
                            newPlayer.name = name
                            addPlayer(newPlayer)
                            updateFriendTable()
                        }
                    }
                })
                val nameTable = Table().apply {
                    add(playerName).width(800f)
                    add(addPlayer)
                }
                table.add(nameTable).colspan(2)
                table.row()

                val changeLabel = TextButton("Add Robot", skin)
                changeLabel.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        changeWindow(listOfComputerWindow)
                    }
                })
                table.add(changeLabel).colspan(2)
                table.row()

                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).maxWidth(400f).align(Align.center)
                table.add(playerTable).width(400f).padRight(50f)

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")
                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)
                robotTable.row()

                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                val cancelButton = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, nameLabel2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(1)
                            updateFriendTable()
                        }
                    })
                }
                robotTable.addActor(cancelButton)
                table.add(robotTable).width(400f).padLeft(50f)

                table.row().colspan(2)
                addLabel("VS")

                val robot2Table = Table(purpleSkinn)
                val robot2Image = ImageButton(skin, "icon-${playerArray[2].iconId}")

                robot2Image.touchable = Touchable.disabled
                robot2Table.add(robot2Image).size(300f)

                robot2Table.row()
                val name2Label2 = Label(playerArray[2].name, skin, "center")
                name2Label2.setFontScale(0.8f)
                robot2Table.add(name2Label2).maxWidth(400f).align(Align.center)
                val cancel2Button = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, name2Label2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(2)
                            updateFriendTable()
                        }
                    })
                }
                robot2Table.addActor(cancel2Button)
                table.add(robot2Table).width(400f).padRight(50f)

                table.row()

            }
            if (playerArray.size == 4) {
//
                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).align(Align.center)
                table.add(playerTable).width(400f).padRight(50f)

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")

                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                val cancelButton = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, nameLabel2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(1)
                            updateFriendTable()
                        }
                    })
                }
                robotTable.addActor(cancelButton)
                table.add(robotTable).width(400f).padLeft(50f)

                table.row().colspan(2)
                addLabel("VS")

                val robot2Table = Table(purpleSkinn)
                val robot2Image = ImageButton(skin, "icon-${playerArray[2].iconId}")

                robot2Image.touchable = Touchable.disabled
                robot2Table.add(robot2Image).size(300f)

                robot2Table.row()
                val name2Label2 = Label(playerArray[2].name, skin, "center")
                name2Label2.setFontScale(0.8f)
                robot2Table.add(name2Label2).maxWidth(400f).align(Align.center)
                val cancel2Button = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, name2Label2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(2)
                            updateFriendTable()
                        }
                    })
                }
                robot2Table.addActor(cancel2Button)
                table.add(robot2Table).width(400f).padRight(50f)

                val robot3Table = Table(purpleSkinn)
                val robot3Image = ImageButton(skin, "icon-${playerArray[3].iconId}")

                robot3Image.touchable = Touchable.disabled
                robot3Table.add(robot3Image).size(300f)

                robot3Table.row()
                val name3Label2 = Label(playerArray[3].name, skin, "center")
                name3Label2.setFontScale(0.8f)
                robot3Table.add(name3Label2).maxWidth(400f).align(Align.center)
                val cancel3Button = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, name3Label2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            playerArray.removeAt(3)
                            updateFriendTable()
                        }
                    })
                }
                robot3Table.addActor(cancel3Button)
                table.add(robot3Table).width(400f).padLeft(50f)

                table.row()
//                table.add(TextButton("Add Computer",skin).apply { addListener(object : ChangeListener() {
//                    override fun changed(event: ChangeEvent?, actor: Actor?) {
//                        changeWindow(listOfComputerWindow)
//                    }
//                }) })
            }

            clearButtonTable()
            addReturnButton()
            if (playerArray.size > 1) {
                addPlayButton("PLAY") {
                    if (playerArray.size > 1) {

                        val players = Array(playerArray.size) {
                            playerArray[it]
                        }
                        change = Pair(true, GameScreen(naijaLudo, FriendNewGameLogic(players = players)))

                    }

                }
            }


        }
    }

    fun updateHostTable() {
        with(hostWindow) {
            table.clear()
            table.defaults().uniform(false, false)
            if (playerArray.size == 1) {
                val changeLabel = TextButton("Add Robot", skin)
                changeLabel.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        changeWindow(listOfComputerWindow)
                    }
                })
                table.add(changeLabel).width(1100f).uniform(false, false)
                table.row()
                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).maxWidth(400f).align(Align.center)

                table.add(playerTable).width(400f).pad(25f)

            }

            if (playerArray.size == 2) {


                val changeLabel = TextButton("Add Robot", skin)
                changeLabel.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        changeWindow(listOfComputerWindow)
                    }
                })
                table.add(changeLabel).width(1100f).uniform(false, false)
                table.row()
                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).maxWidth(400f).align(Align.center)

                table.add(playerTable).width(400f).pad(25f)
                table.row()

                addLabel("VS")

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")

                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                val changeButton = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)

                    setPosition(150f, nameLabel2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            removeOnlinePlayer(1)
                        }
                    })
                }
                robotTable.addActor(changeButton)
                table.add(robotTable).width(400f).pad(25f)

                table.row()
//                table.add(Label("click icon button to change type",skin,"red-1").apply { setFontScale(0.4f) }).uniform(false,false)
            }

            if (playerArray.size == 3) {
                table.row().colspan(2)
                val changeLabel = TextButton("Add Robot", skin)
                changeLabel.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        changeWindow(listOfComputerWindow)
                    }
                })
                table.add(changeLabel).width(1100f)
                table.row()

                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).maxWidth(400f).align(Align.center)
                table.add(playerTable).width(400f).padRight(50f)

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")

                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                val changeButton = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, nameLabel2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            removeOnlinePlayer(1)
                        }
                    })
                }
                robotTable.addActor(changeButton)
                table.add(robotTable).width(400f).padLeft(50f)

                table.row().colspan(2)
                addLabel("VS")

                val robot2Table = Table(purpleSkinn)
                val robot2Image = ImageButton(skin, "icon-${playerArray[2].iconId}")

                robot2Image.touchable = Touchable.disabled
                robot2Table.add(robot2Image).size(300f)

                robot2Table.row()
                val name2Label2 = Label(playerArray[2].name, skin, "center")
                name2Label2.setFontScale(0.8f)
                robot2Table.add(name2Label2).maxWidth(400f).align(Align.center)
                val cancel2Button = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, name2Label2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            removeOnlinePlayer(2)
                        }
                    })
                }
                robot2Table.addActor(cancel2Button)
                table.add(robot2Table).width(400f).pad(25f)

                table.row()

            }
            if (playerArray.size == 4) {
//
                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).align(Align.center)
                table.add(playerTable).width(400f).padRight(50f)

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")

                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                val changeButton = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, nameLabel2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            removeOnlinePlayer(1)
                        }
                    })
                }
                robotTable.addActor(changeButton)
                table.add(robotTable).width(400f).padLeft(50f)

                table.row().colspan(2)
                addLabel("VS")

                val robot2Table = Table(purpleSkinn)
                val robot2Image = ImageButton(skin, "icon-${playerArray[2].iconId}")

                robot2Image.touchable = Touchable.disabled
                robot2Table.add(robot2Image).size(300f)

                robot2Table.row()
                val name2Label2 = Label(playerArray[2].name, skin, "center")
                name2Label2.setFontScale(0.8f)
                robot2Table.add(name2Label2).maxWidth(400f).align(Align.center)
                val cancel2Button = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, name2Label2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            removeOnlinePlayer(2)
                        }
                    })
                }
                robot2Table.addActor(cancel2Button)
                table.add(robot2Table).width(400f).padRight(50f)

                val robot3Table = Table(purpleSkinn)
                val robot3Image = ImageButton(skin, "icon-${playerArray[3].iconId}")

                robot3Image.touchable = Touchable.disabled
                robot3Table.add(robot3Image).size(300f)

                robot3Table.row()
                val name3Label2 = Label(playerArray[3].name, skin, "center")
                name3Label2.setFontScale(0.8f)
                robot3Table.add(name3Label2).maxWidth(400f).align(Align.center)
                val cancel3Button = Button(skin, "cancel-2").apply {
                    setSize(100f, 100f)
                    setPosition(150f, name3Label2.prefHeight)
                    addListener(object : ChangeListener() {
                        override fun changed(event: ChangeEvent?, actor: Actor?) {
                            removeOnlinePlayer(3)
                        }
                    })
                }
                robot3Table.addActor(cancel3Button)
                table.add(robot3Table).width(400f).padLeft(50f)

                table.row()

            }

            clearButtonTable()
            cancelButtonFunction = {
                naijaLudo.server.stop()
                isVisible = false
            }
            addReturnButton()
            addPlayButton("PLAY") {
                val players = Array(playerArray.size) {
                    playerArray[it]
                }
                change = Pair(true, GameScreen(naijaLudo, NewGameLogic(players = players, gameController = naijaLudo.server.serverGameController)))

            }


        }

    }

    fun updateJoinTable() {
        with(joinWindow) {
            table.clear()
            table.defaults().uniform(false, false)


            if (playerArray.size == 2) {

                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).maxWidth(400f).align(Align.center)

                table.add(playerTable).width(400f).pad(25f)
                table.row()

                addLabel("VS")

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")

                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)


                table.add(robotTable).width(400f).pad(25f)


            }

            if (playerArray.size == 3) {

                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).maxWidth(400f).align(Align.center)
                table.add(playerTable).width(400f).padRight(50f)

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")

                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                table.add(robotTable).width(400f).padLeft(50f)

                table.row().colspan(2)
                addLabel("VS")

                val robot2Table = Table(purpleSkinn)
                val robot2Image = ImageButton(skin, "icon-${playerArray[2].iconId}")

                robot2Image.touchable = Touchable.disabled
                robot2Table.add(robot2Image).size(300f)

                robot2Table.row()
                val name2Label2 = Label(playerArray[2].name, skin, "center")
                name2Label2.setFontScale(0.8f)
                robot2Table.add(name2Label2).maxWidth(400f).align(Align.center)

                table.add(robot2Table).width(400f).pad(25f)

                table.row()
            }

            if (playerArray.size == 4) {

                val playerTable = Table(purpleSkinn)
                playerTable.add(ImageButton(skin, "icon-${playerArray[0].iconId}").apply { touchable = Touchable.disabled }).size(300f)
                playerTable.row()
                val nameLabel = Label(playerArray[0].name, skin, "center")
                nameLabel.setFontScale(0.8f)
                playerTable.add(nameLabel).align(Align.center)
                table.add(playerTable).width(400f).padRight(50f)

                val robotTable = Table(purpleSkinn)
                val robotImage = ImageButton(skin, "icon-${playerArray[1].iconId}")

                robotImage.touchable = Touchable.disabled
                robotTable.add(robotImage).size(300f)

                robotTable.row()
                val nameLabel2 = Label(playerArray[1].name, skin, "center")
                nameLabel2.setFontScale(0.8f)
                robotTable.add(nameLabel2).maxWidth(400f).align(Align.center)
                table.add(robotTable).width(400f).padLeft(50f)

                table.row().colspan(2)
                addLabel("VS")

                val robot2Table = Table(purpleSkinn)
                val robot2Image = ImageButton(skin, "icon-${playerArray[2].iconId}")

                robot2Image.touchable = Touchable.disabled
                robot2Table.add(robot2Image).size(300f)

                robot2Table.row()
                val name2Label2 = Label(playerArray[2].name, skin, "center")
                name2Label2.setFontScale(0.8f)
                robot2Table.add(name2Label2).maxWidth(400f).align(Align.center)
                table.add(robot2Table).width(400f).padRight(50f)

                val robot3Table = Table(purpleSkinn)
                val robot3Image = ImageButton(skin, "icon-${playerArray[3].iconId}")

                robot3Image.touchable = Touchable.disabled
                robot3Table.add(robot3Image).size(300f)

                robot3Table.row()
                val name3Label2 = Label(playerArray[3].name, skin, "center")
                name3Label2.setFontScale(0.8f)
                robot3Table.add(name3Label2).maxWidth(400f).align(Align.center)
                table.add(robot3Table).width(400f).padLeft(50f)

                table.row()
            }

            clearButtonTable()
            addReturnButton()
            cancelButtonFunction = {
                join.disconnected()
                isVisible = false
            }
            addPlayButton("PLAY") {
                val players = Array(playerArray.size) {
                    playerArray[it]
                }
                change = Pair(true, GameScreen(naijaLudo, NewGameLogic(players = players, gameController = join.client.clientGameController)))

            }

        }

    }

    var computerGroupButton = ButtonGroup<ImageButton>().apply {
        for (i in PlayType.values()) {
            add(getPlayerThumbnail(i))

        }
    }
    val iconList = ButtonGroup<ImageButton>().apply {
        for (i in 1..9) {
            add(getIcon(i))
        }
    }
    var oneComputerEventListener = object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
            if (computerGroupButton.checkedIndex > -1) {
                val index = computerGroupButton.checkedIndex
                computerGroupButton.uncheckAll()
                val player = PlayType.values()[index]
                println("player clicked is ${player.name}")
                playerArray.removeAt(1)
                addPlayer(getNewPlayer(player))
                updateOneTable()
            }
            changeWindow(oneComputerWindow)

        }
    }
    var manyComputerEventListener = object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
            if (computerGroupButton.checkedIndex > -1 && playerArray.size != 4) {
                val index = computerGroupButton.checkedIndex
                computerGroupButton.uncheckAll()
                val player = PlayType.values()[index]
                println("player clicked is ${player.name}")
                addPlayer(getNewPlayer(player))
                updateManyTable()
            }
            changeWindow(manyComputerWindow)
        }
    }
    var onlineComputerEventListener = object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
            if (computerGroupButton.checkedIndex > -1 && playerArray.size != 4) {
                val index = computerGroupButton.checkedIndex
                computerGroupButton.uncheckAll()
                val player = PlayType.values()[index]
                println("player clicked is ${player.name}")
                addOnlinePlayer(player)

            }
            changeWindow(hostWindow)
        }
    }
    var friendComputerEventListener = object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
            if (computerGroupButton.checkedIndex > -1 && playerArray.size != 4) {
                val index = computerGroupButton.checkedIndex
                computerGroupButton.uncheckAll()
                val player = PlayType.values()[index]
                println("player clicked is ${player.name}")
                addPlayer(getNewPlayer(player))
                updateFriendTable()
            }
            changeWindow(friendsWindow)
        }
    }

    val friendsWindow = OptionWindowNew("Friend", skin = purpleSkinn)

    val computerWindow = OptionWindowNew("Select Game", skin = purpleSkinn).apply {
        addButton("One Computer") {
            updateOneTable()
            computerGroupButton.buttons.forEach {
                it.removeListener(manyComputerEventListener)
                it.removeListener(friendComputerEventListener)
                it.removeListener(onlineComputerEventListener)
                it.addListener(oneComputerEventListener)
            }
            changeWindow(oneComputerWindow)

        }
        addButton("Many Computer") {
            updateManyTable()
            computerGroupButton.buttons.forEach {
                it.removeListener(oneComputerEventListener)
                it.removeListener(friendComputerEventListener)
                it.removeListener(onlineComputerEventListener)
                it.addListener(manyComputerEventListener)
            }
            changeWindow(manyComputerWindow)
        }
        addButton("Continue Game") {
            if (naijaLudo.newGameLogic != null) {

                naijaLudo.newGameLogic!!.update = true
                naijaLudo.newGameLogic!!.update()
                change = Pair(true, GameScreen(naijaLudo, naijaLudo.newGameLogic!!))
            }
        }

    }

    val listOfComputerWindow = OptionWindowNew("Select Robot", skin = purpleSkinn).apply {
        val list = Table()
        computerGroupButton.buttons.forEachIndexed { index, imageButton ->
            val table = Table(purpleSkinn)

            if (PlayType.values()[index] != PlayType.HUMAN) {
                table.add(imageButton).size(300f)
                table.row()
                val nameLabel = Label(PlayType.values()[index].name.toLowerCase(), skin)
                nameLabel.setFontScale(0.8f)

                table.add(nameLabel).align(Align.center)
                list.add(table).size(400f).pad(50f)
                if (index % 2 == 1) {
                    list.row()
                }
            }
        }
        val scrollPane = ScrollPane(list, purpleSkinn)
//scrollPane.setFillParent(true)
        table.add(scrollPane).growX()
    }
    val manyComputerWindow = OptionWindowNew("Many Robots", skin = purpleSkinn)
    val oneComputerWindow = OptionWindowNew("One Robot", skin = purpleSkinn)
    val newSettingWindow = NewSettingWindow("Settings", purpleSkinn)
    val profileWindow = OptionWindowNew("Profile", skin = purpleSkinn).apply {
        with(table) {
            defaults().uniform(false)
            row()
//


            add(iconProfile).size(200f)

            add(labelProfile).growX()
            row()
            add(TextButton("Edit Profile", skin).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        changeWindow(editProfile)
                    }
                })
            }).width(600f).colspan(2)
            row()
            val scrollTable = Table()
            val scrollPane = ScrollPane(scrollTable, skin)

            scrollTable.add(getTableforWIn("Vs One Robot", "$winOne", "$lossOne"))
            scrollTable.row()
            scrollTable.add(getTableforWIn("Vs Many Robots", "$winMany", "$lossMany"))
            scrollTable.row()
            scrollTable.add(getTableforWIn("Vs Friends", "$winFriend", "$lossFriend"))

            add(scrollPane).colspan(2)
        }
    }


    val editProfile = OptionWindowNew("Edit Profile", skin = purpleSkinn).apply {

        with(table) {
            defaults().uniform(false)

            val icon = ImageButton(purpleSkinn, "icon-${GameManager.avatar}")
//            val label = Label(GameManager.name, purpleSkinn,"red")
            val iconTable = Table()
            iconList.buttons.forEach {
                it.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        if (iconList.checkedIndex != -1) {
                            val num = iconList.checkedIndex + 1
                            GameManager.avatar = num
                            icon.style = purpleSkinn.get("icon-${num}", ImageButton.ImageButtonStyle::class.java)

                        }
                    }
                })
                iconTable.add(it).size(200f)
            }
            val scrollPane = ScrollPane(iconTable, skin)

            add("Name", "red")
            add(TextField(GameManager.name, skin).apply {
                messageText = "Enter name"
                this.maxLength = 9
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {

                        if (this@apply.text.isEmpty()) {
                            GameManager.name = "Player"
                        } else {
                            GameManager.name = this@apply.text
                        }
                    }
                })

            }).growX()
            row()
            add("Icon", "red").expandX().fill(false).align(Align.center).colspan(2)
            row()
            add(icon).width(800f).colspan(2)
            row()
            add(scrollPane).growX().colspan(2)


        }

        cancelButtonFunction = {

            isVisible = false
            remove()
            updateIcon()
        }
    }


    val multiplayerWindow = OptionWindowNew("MultiPlayer", skin = purpleSkinn).apply {
        addButton("Host") {
            naijaLudo.launch {

                naijaLudo.connectInterfaceAnd?.startDiscovery()
                naijaLudo.server.updateActor = updatePlayerActor
                naijaLudo.server.connect()
                addOnlinePlayer(PlayType.HUMAN)

//

                changeWindow(hostWindow)
            }

        }
        addButton("Join") {
//            onlineHumanPlayer2 = OnlineHumanPlayer(0, intArrayOf(), "")

            naijaLudo.launch {

                naijaLudo.connectInterfaceAnd?.discoverPeer()
                if (Gdx.app.type == Application.ApplicationType.Desktop) {
                    join.connect("192.168.49.1")
                }

//                onlineHumanPlayer2?.let {
//
//                    it.updateActor = updatePlayerActorJoin()
//                    it.connectClient("10.90.241.36")
//
                changeWindow(joinWindow)
            }

        }


    }


    val hostWindow = OptionWindowNew("Host", skin = purpleSkinn)

    val joinWindow = OptionWindowNew("Join", skin = purpleSkinn)

    fun CoroutineScope.updatePlayerActor() = actor<Factory.Message> {
        println("from updatePlayer Actror")
        for (msg in channel) {
            when (msg) {
                is Factory.Message.Play -> {
                }
                is Factory.Message.SendPlayer -> {
                    playerArray.clear()
                    playerArray.addAll(msg.players)
                    updateHostTable()
                }
            }


        }

    }

    fun CoroutineScope.updatePlayerActorJoin() = actor<Factory.Message> {
        println("from updatePlayer Actror join")
        for (msg in channel) {
            when (msg) {
                is Factory.Message.Play -> {

                }
                is Factory.Message.SendPlayer -> {
                    playerArray.clear()
                    playerArray.addAll(msg.players)
                    updateJoinTable()
                }
            }

        }

    }

}
