package com.mshdabiola.naijaludo.entity.display

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener


open class OptionWindowNew(name: String, val table: Table = Table(), skin: Skin) : Window(name, skin) {
    private val bottomTable = Table()

    var cancelButtonFunction: () -> Unit = { isVisible = false }
    lateinit var homeButton: TextButton


    init {

        val title = titleLabel
        titleTable.reset()
        titleTable.background = skin.getDrawable("window-top")
//        titleTable.setSize(1000f,250f)
        titleTable.add(title)


//        setFillParent(true)

        add(table).grow().padBottom(50f).padTop(100f)

        table.defaults().padBottom(50f).uniform().fillX()
        table.skin = skin

        isMovable = false


//        bottomTable.add(TextButton("Home",skin,"red"))
        addReturnButton()
        row()

        val line = Image(skin.getDrawable("line"))
        add(Table().apply {
            add(line).growX().padBottom(12.5f).padTop(12.5f)
        }).growX()
        row()
        add(bottomTable).growX()

    }

    fun clearButtonTable() {
        bottomTable.clear()
    }

    fun addReturnButton() {
        val returnButton = ImageButton(skin, "return")
        returnButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                cancelButtonFunction()
            }
        })
        bottomTable.add(returnButton).width(200f)
    }

    fun addButton(buttonName: String, type: String = "rainbow", func: () -> Unit): TextButton {
        val button = TextButton(buttonName, skin, type).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    func()
                }
            })
        }
        table.add(button)
        table.row()
        return button
    }

    fun addLabel(text: String) {
        table.add(text, "red-1").uniform(false, false).fill(false, false)
        table.row()
    }

    fun addHomeButton(name: String = "Home", func: () -> Unit) {
        if (this::homeButton.isInitialized) {
            bottomTable.removeActor(homeButton)
        }
        homeButton = TextButton(name, skin, "red")
        homeButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                func()
            }
        })
        bottomTable.add(homeButton).padLeft(50f)
    }

    fun addPlayButton(name: String = "Play", func: () -> Unit) {

        val playButton = ImageTextButton(name, skin, "play")
        playButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                func()
            }
        })
        bottomTable.add(playButton).padLeft(50f)
    }

    fun addNextButton(name: String = "Next", func: () -> Unit) {

        val nextButton = ImageTextButton(name, skin, "next")
        nextButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                func()
            }
        })
        bottomTable.add(nextButton).padLeft(50f)
    }
}