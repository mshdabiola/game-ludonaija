package com.mshdabiola.naijaludo.entity.display

import com.badlogic.gdx.graphics.Color

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener


class OptionWindow(name: String, val table: Table = Table(), skin: Skin) : Window("", skin, "test") {




    init {
        table.defaults().width(800f).height(250f).padBottom(30f)
        table.skin = skin
    }

    val bgTable = Table()


    val cancel = Button(skin, "window-cancel").apply {
        setSize(200f, 200f)
        setPosition(1050f, 1850f)
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                this@OptionWindow.isVisible = false
            }
        })
    }

    init {
        setFillParent(true)


        val labelTable = Table()
        labelTable.background = skin.getDrawable("ribbon-up")
        labelTable.add(Label(name, skin))
        labelTable.row()
        isVisible = false


        val scrollPane = ScrollPane(table, skin)

        bgTable.add(labelTable).height(200f).expandX().fillX()
        bgTable.row()
        bgTable.add(scrollPane).fill().expand()
        bgTable.addActor(cancel)



        bgTable.background = skin.getDrawable("window-bg")

        add(bgTable).width(1200f).height(2000f)


    }

    fun addButton(buttonName: String, func: () -> Unit): TextButton {
        val button = TextButton(buttonName, skin, "win-button").apply {
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

    fun addButton(text: String): TextButton {
        return TextButton(text, skin, "win-button")
    }

    fun addCheckTable(name: String, func: () -> Unit) {
        val checkTable = Table(skin)
        checkTable.background = skin.getDrawable("setting-gb")

        checkTable.add(name).fillX().expandX()
        checkTable.add(CheckBox("", skin))

        table.add(checkTable)
        table.row()
    }

    fun addHeader(name: String) {
        val t = Table()
        val label = Label(name, skin).apply {
            setFontScale(1.5f)
            color = Color.RED
        }
        t.add(label)
        table.add(t)
        table.row()
    }


}