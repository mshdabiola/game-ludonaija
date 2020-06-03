package com.mshdabiola.naijaludo.entity.display

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.mshdabiola.naijaludo.config.GameManager

class NewSettingWindow(name: String, skin: Skin) : OptionWindowNew(name, skin = skin) {
    val table1 = Table()

    init {
        initUi()
        table.add(ScrollPane(table1, skin)).grow()
    }

    fun initUi() {


        table1.pad(50f)
        table1.skin = skin
        table1.defaults().fillX().expandX().padBottom(50f)

        val secondTable = getTableWithSetting("Computer")

        val levelTable = getTableRow("Level", SelectBox<String>(skin).apply {
            setItems("Easy", "Medium", "Hard", "Advanced")
            selectedIndex = GameManager.level
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    GameManager.level = selectedIndex
                }
            })
        })

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


//        table1.add(firstTable)
//        table1.row()
        table1.add(secondTable)
        table1.row()
        table1.add(thirdTable)
        table1.row()
        table1.add(fourthTable)
        table1.row()
        table1.add(fifth)




        table1.pack()

    }

    fun getTableWithSetting(name: String): Table {
        return Table(skin).apply {

            pad(50f)
            defaults().fillX().expandX().padBottom(50f)
            add(Table(skin).apply { add(name, "center").growX() })
            row()
        }


    }

    fun getTableRow(name: String, actor: Actor): Table {
        val tableRow = Table(skin)
        tableRow.left()
        tableRow.add(name, "red-1").spaceRight(100f)

        tableRow.add(actor).fillX().expandX()

        return tableRow
    }
}