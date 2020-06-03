package com.mshdabiola.naijaludo.entity.display

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align

class ProfileWindow(name: String, skin: Skin) : Window(name, skin) {

    val table = Table()

    init {

        val cancelButton = Button(skin, "close")
        cancelButton.setSize(100f, 100f)
        cancelButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                isVisible = false
            }
        })
        titleTable.add(cancelButton)
//        isVisible=true
        initUi()
        add(ScrollPane(table, skin)).grow()
    }

    private fun initUi() {
        table.add(Image(skin.getDrawable("touchpad")).apply { setSize(500f, 500f) })
        table.row()
        table.defaults().growX()
        table.add(getTableRow("Name", TextField("", skin).apply {
            this.messageText = "name"
        }))

    }

    fun getTableRow(name: String, actor: Actor): Table {
        val tableRow = Table(skin)
        tableRow.left()
        tableRow.add(name).spaceRight(100f).expandX().align(Align.center)

        tableRow.add(actor).minWidth(500f)

        return tableRow
    }
}