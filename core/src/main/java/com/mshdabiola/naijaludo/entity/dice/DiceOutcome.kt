package com.mshdabiola.naijaludo.entity.dice

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.mshdabiola.naijaludo.asset.MassetDescriptor

class DiceOutcome(str: String, option: String, val id: Int) : TextButton(str, MassetDescriptor.gameSkin2, option) {

    val rotateAction = Actions.forever(Actions.rotateBy(360f))

    val indictor = Button(MassetDescriptor.gameSkin2, "outcome-indicator").apply {
        touchable = Touchable.disabled
//        color= Color.RED
        isTransform = true
        setOrigin(100f, 100f)
        addAction(Actions.alpha(0f))
    }


    fun startRotate() {
//        removeRotate()
        indictor.addAction(Actions.alpha(1f))
        indictor.addAction(Actions.forever(Actions.sequence(Actions.rotateBy(359f, 2f, Interpolation.circle))))
    }

    fun removeRotate() {


        indictor.actions.clear()
        indictor.addAction(Actions.alpha(0f))
    }


    override fun setText(text: String?) {
        super.setText(text)
        if (text == "0") {
            removeRotate()
        } else {


//            startRotate()
        }
    }

}