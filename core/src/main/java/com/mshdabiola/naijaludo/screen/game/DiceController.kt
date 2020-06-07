package com.mshdabiola.naijaludo.screen.game

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.entity.Ui
import com.mshdabiola.naijaludo.entity.dice.Dice
import com.mshdabiola.naijaludo.entity.display.OutComeDisplayPanel

class DiceController : Ui {

    @Transient
    lateinit var outcomeDiceOutcome: OutComeDisplayPanel

    @Transient
    lateinit var outcomeDiceOutcomeCopy: OutComeDisplayPanel

    @Transient
    lateinit var dice1: Dice

    @Transient
    lateinit var dice2: Dice

    var tossDice = true

    init {
        if (!Config.isTest) {

//            createOutcomeDisplayAndDice()
        }

    }

    val locArray = Array(13) {
        (it + 1) * 100f
    }

    override fun createUi() {
        createOutcomeDisplayAndDice()
        diceTouchable(false)
    }

    override fun updateUi() {
        updateDiceOutCome()
    }

    override fun resetUi() {
        TODO("Not yet implemented")
    }

    private fun createOutcomeDisplayAndDice() {
        if (!this::outcomeDiceOutcome.isInitialized) {
            outcomeDiceOutcome = OutComeDisplayPanel()
            outcomeDiceOutcomeCopy = OutComeDisplayPanel()

            dice1 = Dice(0)

            dice2 = Dice(1)
        }
    }


    val value = intArrayOf(0, 0, 0)
    val valueHolder = intArrayOf(0, 0, 0)

    fun setDiceToZero() = value.fill(0)


    fun toss(func: () -> Unit = {}) {
        diceTouchable(false)
        tossDice = false
        dice2.value = 0
        dice1.value = 0

        dice1.actor.toFront()
        dice2.actor.toFront()

        for (i in 0..1) {
            value[i] = MathUtils.random(4, 6)
            valueHolder[i] = value[i]
        }
        value[2] = value[0] + value[1]
        valueHolder[2] = value[2]
        try {
            if (!Config.isTest) {

                dice1.actor.addAction(
                        Actions.sequence(
                                getAction(),
                                getAction(),
                                getAction(dice1.loc1),
                                Actions.run {
                                    dice1.value = value[0]
                                    dice2.value = value[1]
                                    updateDiceOutCome()
                                    func()
                                }
                        )
                )

                dice2.actor.addAction(
                        Actions.sequence(
                                getAction(),
                                getAction(),
                                getAction(dice2.loc2),
                                Actions.run {

                                }
                        )
                )


//                dice1.value = value[0]
//                dice2.value = value[1]
//                updateDiceOutCome()

            }
        } catch (e: Exception) {
            println("skin not Init")
            e.printStackTrace()
        }

    }

    fun tossWithValue(vararg v: Int) {
        if (!Config.isTest) {
            dice2.value = 0
            dice1.value = 0

            dice1.actor.toFront()
            dice2.actor.toFront()
        }

        for (i in 0..1) {
            value[i] = v[i]
            valueHolder[i] = value[i]
        }
        value[2] = value[0] + value[1]
        valueHolder[2] = value[2]
        try {
            if (!Config.isTest) {
                dice1.value = value[0]
                dice2.value = value[1]
                updateDiceOutCome()

            }
        } catch (e: Exception) {
            println("skin not Init")
            e.printStackTrace()
        }

    }


    fun getAction(pair: Pair<Float, Float>): Action {
        return Actions.parallel(Actions.rotateBy(360f * 2, 1f, Interpolation.fastSlow), Actions.moveTo(pair.first, pair.second, 1f, Interpolation.linear))
    }

    fun getAction(): Action {
        val pair = Pair(locArray[MathUtils.random(locArray.size - 1)], locArray[MathUtils.random(locArray.size - 1)])
        return getAction(pair)
    }

    private fun updateDiceOutCome() {
        outcomeDiceOutcome.outCome1.setText(value[0].toString())
        outcomeDiceOutcome.outCome2.setText(value[1].toString())
        outcomeDiceOutcome.outComeTotal.setText(value[2].toString())


//        outcomeDiceOutcome.outComeTotal.indictor.addAction(Actions.forever(Actions.sequence(Actions.rotateBy(90f))))
    }


    fun setToZero() {
        outcomeDiceOutcome.outCome1.setText("0")
        outcomeDiceOutcome.outCome2.setText("0")
        outcomeDiceOutcome.outComeTotal.setText("0")
//        dice1.value=0
//        dice2.value=0


//        outcomeDiceOutcome.outComeTotal.indictor.addAction(Actions.forever(Actions.sequence(Actions.rotateBy(90f))))
    }

    fun isHaveingSix(): Boolean {
        return value[0] == 6 || value[1] == 6
    }

    fun isDoubleSix(): Boolean {
        return valueHolder[2] == 12
    }

    fun hasCountFinished(): Boolean {
        return value[0] == 0 && value[1] == 0
    }

    fun hasCountThis(i: Int): Boolean {
        return value[i] == 0
    }


    fun getDiceValue(i: Int): Int {
        if (i < 0) {
            return 0
        }
        return value[i]
    }


    fun getSecondDiceValue(diceIndex: Int): Int {
        return if (diceIndex == 0) value[1] else value[0]
    }

    fun countDice(index: Int): Int {
        return if (index == 0) {
            countOne()
        } else if (index == 1) {
            countTwo()
        } else {
            countTotal()
        }
    }

    private fun countOne(): Int {
        val v = value[0]
        value[0] = 0
        value[2] = 0
//        dice1.value=0
        updateDiceOutCome()
        return v
    }

    private fun countTwo(): Int {
        val v = value[1]
        value[1] = 0
        value[2] = 0
//        dice2.value=0
        updateDiceOutCome()
        return v
    }

    fun getIndexHavingSix(): Int {
        return if (value[0] == 6) {
            0
        } else {
            1
        }
    }

    private fun countTotal(): Int {
        val v = value[2]
        value[0] = 0
        value[1] = 0
        value[2] = 0
        updateDiceOutCome()
//        outcomeDiceOutcome.outComeTotal.removeRotate()
//        outcomeDiceOutcome.outComeTotal.indictor.actions.clear()
        return v
    }

    fun diceTouchable(touch: Boolean) {
        if (touch) {
            dice1.actor.touchable = Touchable.enabled
            dice2.actor.touchable = Touchable.enabled
        } else {
            dice1.actor.touchable = Touchable.disabled
            dice2.actor.touchable = Touchable.disabled
        }
    }

    fun diceDisplayTouchable(touch: Boolean) {
        if (touch) {
            outcomeDiceOutcome.outCome1.touchable = Touchable.enabled
            outcomeDiceOutcome.outCome2.touchable = Touchable.enabled
            outcomeDiceOutcome.outComeTotal.touchable = Touchable.enabled

        } else {
            outcomeDiceOutcome.outCome1.touchable = Touchable.disabled
            outcomeDiceOutcome.outCome2.touchable = Touchable.disabled
            outcomeDiceOutcome.outComeTotal.touchable = Touchable.disabled
        }
    }

    fun addDicetoTable(table: Table) {

        dice1.moveToDefault()
        dice2.moveToDefault()

        table.addActor(dice1.actor)
        table.addActor(dice2.actor)
    }

    fun addListenerToOutCome(eventListener: EventListener) {
        if (!Config.isTest) {
            outcomeDiceOutcome.outComeTotal.addListener(eventListener)
            outcomeDiceOutcome.outCome1.addListener(eventListener)
            outcomeDiceOutcome.outCome2.addListener(eventListener)
        }
    }

    fun addListenerToDice(eventListener: EventListener) {
        if (!Config.isTest) {
            dice1.actor.addListener(eventListener)
            dice2.actor.addListener(eventListener)
        }
    }

    fun diceMoveToFront() {
        dice1.actor.toFront()
        dice2.actor.toFront()
    }

}