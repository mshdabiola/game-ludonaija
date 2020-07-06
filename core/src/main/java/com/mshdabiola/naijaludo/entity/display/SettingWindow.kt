package com.mshdabiola.naijaludo.entity.display

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.mshdabiola.naijaludo.config.GameManager

class SettingWindow(name: String, skin: Skin) : Window(name, skin) {

    val table = Table()

    init {

        val cancelButton = Button(skin, "cancel")
        cancelButton.setSize(100f, 100f)
        cancelButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                isVisible = false
            }
        })
        titleLabel.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                isVisible = false
            }
        })
        titleTable.add(cancelButton)
        isVisible = false
        initUi()
        add(ScrollPane(table, skin)).grow()
    }

    fun initUi() {

//        table.setFillParent(true)
//        table.width=1200f
        table.background = skin.getDrawable("bg")
        table.top()

        table.pad(50f)
        table.skin = skin
        table.defaults().fillX().expandX().padBottom(50f)

        table.row()
        val firstTable = getTableWithSetting("Player")
        val nameTable = getTableRow("Name", TextField("", skin).apply {
            text = GameManager.name
            messageText = "Name"
            maxLength = 10
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    GameManager.name = text
                }
            })
        })


        val avaterGroup = ButtonGroup<ImageButton>()
        avaterGroup.add(ImageButton(skin))
        avaterGroup.add(ImageButton(skin))
        avaterGroup.add(ImageButton(skin))
//        avaterGroup.checkedIndex=GameManager.avatar
//        avaterGroup.

        val playerAvater = Table()
        playerAvater.defaults().padRight(50f).width(200f).height(200f)
        for (i in avaterGroup.buttons) {
            playerAvater.add(i)
        }
        val scrollPane = ScrollPane(playerAvater, skin)






        firstTable.add(nameTable)
        firstTable.row()
        firstTable.add(scrollPane).height(200f)


        val secondTable = getTableWithSetting("Computer")

        val computerGroup = ButtonGroup<ImageButton>()
        computerGroup.add(ImageButton(skin))
        computerGroup.add(ImageButton(skin))
        computerGroup.add(ImageButton(skin))

        val computerPlayerTable = Table()
        computerPlayerTable.defaults().padRight(50f).width(200f).height(200f)
        for (i in computerGroup.buttons) {
            computerPlayerTable.add(i)
        }
        val playercrollPane = ScrollPane(computerPlayerTable, skin)


        val levelTable = getTableRow("Level", SelectBox<String>(skin).apply {
            setItems("Easy", "Medium", "Hard", "Advanced")
            selectedIndex = GameManager.level
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    GameManager.level = selectedIndex
                }
            })
        })



        secondTable.add(playercrollPane).height(200f)
        secondTable.row()
        secondTable.add(levelTable)

        val thirdTable = getTableWithSetting("Board")

        val boardStyle = getTableRow("Style", SelectBox<String>(skin).apply {
            setItems("Diagonal", "Horizontal", "Vertical")
            selectedIndex = GameManager.style
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    GameManager.style = selectedIndex
                }
            })
        })
        val rotate = getTableRow("Rotate", CheckBox("", skin).apply {
            isChecked = GameManager.rotate
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    GameManager.rotate = isChecked
                }
            })
        })

        thirdTable.add(boardStyle)
        thirdTable.row()
        thirdTable.add(rotate)

        val fourthTable = getTableWithSetting("Pawn")

        val seedNumber = getTableRow("Number", SelectBox<Int>(skin).apply {
            items = GameManager.onOfseedIntArray
            selectedIndex = GameManager.noOfSeed
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    GameManager.noOfSeed = selectedIndex
                }
            })
        })
        val seedSpeed = getTableRow("Speed", SelectBox<Int>(skin).apply {
            setItems(1, 2, 3, 4, 5, 6)
            selectedIndex = GameManager.seedSpeed
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    GameManager.seedSpeed = selectedIndex
                }
            })
        })
        val seedAnimation = getTableRow("Animation", CheckBox("", skin).apply {
            isChecked = GameManager.animator
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    GameManager.animator = isChecked
                }
            })
        })
        val seedAssistant = getTableRow("Assistant", CheckBox("", skin).apply {
            isChecked = GameManager.assistant
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    GameManager.assistant = isChecked
                }
            })
        })

        fourthTable.add(seedNumber)
        fourthTable.row()
        fourthTable.add(seedAnimation)
        fourthTable.row()
        fourthTable.add(seedSpeed)
        fourthTable.row()
        fourthTable.add(seedAssistant)

        val fifth = getTableWithSetting("Audio")
        val sound = getTableRow("Sound", CheckBox("", skin).apply {
            isChecked = GameManager.sound
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    GameManager.sound = isChecked
                }
            })
        })
        val music = getTableRow("Music", CheckBox("", skin).apply {
            isChecked = GameManager.music
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    GameManager.music = isChecked
                }
            })
        })

        fifth.add(sound)
        fifth.row()
        fifth.add(music)



        table.add(firstTable)
        table.row()
        table.add(secondTable)
        table.row()
        table.add(thirdTable)
        table.row()
        table.add(fourthTable)
        table.row()
        table.add(fifth)




        table.pack()

    }

    fun getTableWithSetting(name: String): Table {
        return Table(skin).apply {
//            setBackground("maroon")
            pad(50f)
            defaults().fillX().expandX().padBottom(50f)
            add(Table(skin).apply { add(name, "title") })
            row()
        }


    }

    fun getTableRow(name: String, actor: Actor): Table {
        val tableRow = Table(skin)
        tableRow.left()
        tableRow.add(name).spaceRight(100f)

        tableRow.add(actor).fillX().expandX()

        return tableRow
    }

}