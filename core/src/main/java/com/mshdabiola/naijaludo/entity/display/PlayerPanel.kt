package com.mshdabiola.naijaludo.entity.display

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.mshdabiola.naijaludo.asset.MassetDescriptor
import com.mshdabiola.naijaludo.asset.MassetDescriptor.purpleSkinn
import com.mshdabiola.naijaludo.config.GameManager
import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.player.BasePlayer
import com.mshdabiola.naijaludo.entity.player.HumanPlayer

class PlayerPanel(val player: BasePlayer) : Table(MassetDescriptor.gameSkin2) {

    private val avater = getPlayerThumbnail(player.iconId)
    private val indicatorTable = Table()
    private val label = Label(player.name, purpleSkinn).apply {
        setFontScale(0.6f)
        if (text.toString().length > 6 && player is HumanPlayer) {
            setText(text.toString().substring(0..5))
        }
        color = purpleSkinn.getColor("purple")
    }

    val scorelabel = Label("${player.point}", purpleSkinn).apply {
        setFontScale(0.6f)
        color = purpleSkinn.getColor("purple")
    }
    private val horizontalSeeds = Table()
    private val labelTable = Table().apply {
        add(label).expandX()
        row()
    }
    private val secondColumn = Table().apply {
        background = purpleSkinn.getDrawable("button-bg-orange")
    }
    private val indicator = Button(MassetDescriptor.gameSkin2, "player-indicator").apply {
        setSize(200f, 200f)
        setPosition(0f, 0f)
        addAction(Actions.alpha(0f))
    }

    private fun setColors() {
        for (gcolor in player.getGameColors()) {

            val seedOutButton = Button(skin, "seed-out")
            seedOutButton.color = gcolor.color
            indicatorTable.add(seedOutButton)
            indicatorTable.row()
        }

    }

    fun updateScore() {
        scorelabel.setText(player.point)
    }

    init {

        defaults().width(600f)

        if (player.id <= 1) {
            if (player.id % 2 == 0) avaterleft() else avaterRight()
        } else {
            if (player.id % 2 != 0) avaterleft() else avaterRight()
        }
        setColors()
    }

    fun moveIndicator() {
        indicator.addAction(Actions.alpha(1f))
        indicator.addAction(Actions.forever(Actions.sequence(Actions.moveTo(200f, 0f, 1f, Interpolation.slowFast), Actions.moveTo(0f, 0f, 1f, Interpolation.fastSlow))))
    }

    fun removeIndicator() {

        indicator.actions.clear()
        indicator.addAction(Actions.alpha(0f))
    }

    private fun avaterleft() {
        indicatorTable.defaults().width(50f).height(50f)
        indicatorTable.top()


        secondColumn.add(indicatorTable).fillY().expandY()
//        labelTable.add(Table()).growX()
        labelTable.add(scorelabel).align(Align.right)
        secondColumn.add(labelTable).fillX().expandX()

        horizontalSeeds.right()

        val row2 = Table()
        val row1 = Table()
        row1.add(avater).width(200f).height(200f)
        secondColumn.addActor(indicator)
        row1.add(secondColumn).fill().expand()
        row2.add(horizontalSeeds).fill().expand()

        add(row1)
        row()
        add(row2).height(50f)

//        add(indicator)
    }


    private fun avaterRight() {
        indicatorTable.defaults().width(50f).height(50f)
        indicatorTable.top()

        labelTable.add(scorelabel).align(Align.left)
        secondColumn.add(labelTable).fillX().expandX()
        secondColumn.add(indicatorTable).fillY().expandY()

        horizontalSeeds.left()

        val row2 = Table()
        val row1 = Table()
        secondColumn.addActor(indicator)
        row1.add(secondColumn).fill().expand()
        row1.add(avater).width(200f).height(200f)

        row2.add(horizontalSeeds).fill().expand()

        add(row1)
        row()
        add(row2).height(50f)

    }

    fun addSeedOut(seed: Seed) {
//        player.seedOut.add(seed)
        val image = Button(skin, "seed-out")
        GameManager.playKill()
        image.color = seed.color.color
        horizontalSeeds.add(image).width(50f).height(50f).padLeft(5f).padRight(5f)
    }

    fun getPlayerThumbnail(iconId: Int): ImageButton {

        return ImageButton(purpleSkinn, "icon-$iconId")
    }

    fun hideScoreLabel() {
        scorelabel.isVisible = false
    }

}