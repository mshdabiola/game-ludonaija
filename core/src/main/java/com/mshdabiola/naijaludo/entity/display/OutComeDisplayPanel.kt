package com.mshdabiola.naijaludo.entity.display

import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.mshdabiola.naijaludo.asset.MassetDescriptor
import com.mshdabiola.naijaludo.entity.dice.DiceOutcome

class OutComeDisplayPanel : Table() {

    val outCome1: DiceOutcome
    val outCome2: DiceOutcome
    val outComeTotal: DiceOutcome

    init {


        outCome1 = DiceOutcome("0", "outcome-1", 0)
        outCome2 = DiceOutcome("0", "outcome-2", 1)
        outComeTotal = DiceOutcome("0", "outcome-3", 2)


        background = MassetDescriptor.gameSkin2.getDrawable("outcome-bg")
//        color = Color.LIGHT_GRAY
        defaults().width(200f).height(200f)

        add(Stack(outCome1, outCome1.indictor))
        add(Stack(outComeTotal, outComeTotal.indictor)).padLeft(100f).padRight(100f)
        add(Stack(outCome2, outCome2.indictor))


    }


}