package com.mshdabiola.naijaludo.entity

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Pool
import com.mshdabiola.naijaludo.asset.MassetDescriptor
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.config.GameColor
import com.mshdabiola.naijaludo.config.GameManager
import com.mshdabiola.naijaludo.entity.board.Board
import com.mshdabiola.naijaludo.entity.board.Floor


class Seed(var colorId:
           Int, var id: Int,
           var playerId: Int
) : Pool.Poolable, Ui {

    constructor() : this(0, 0, 0)

    companion object {
        val MAX_MOVE = 57
        var speed = 1
        val seedPool = object : Pool<Seed>(6, 8) {
            override fun newObject(): Seed {
                println("From seed pooling create")
                return Seed(0, 0, 0).apply {
                    createUi()
                }
            }
        }
    }

    @Transient
    lateinit var actor: Actor

    @Transient
    lateinit var bgActor: Actor

    @Transient
    lateinit var animActor: Actor

    var moveRemain = MAX_MOVE

    var color = GameColor.values()[colorId]

    var homeFloor: Floor

    var startFloor: Floor

    lateinit var currentFloor: Floor

    var previousFloors: Array<Floor>

    lateinit var preFloor: Floor
    var frontNumber = 1

    override fun createUi() {


        actor = TextButton("", MassetDescriptor.gameSkin2, "seed-${GameColor.values()[colorId].name.toLowerCase()}")
        actor.touchable = Touchable.disabled
        actor.setSize(100f, 100f)

        bgActor = Button(MassetDescriptor.gameSkin2, "seed-bg")
        bgActor.setSize(120f, 120f)

        animActor = Button(MassetDescriptor.gameSkin2, "seed-indicator")
        animActor.setSize(120f, 120f)

        if (!this::currentFloor.isInitialized) {
            currentFloor = homeFloor
            preFloor = currentFloor
            println("current floor x ${currentFloor.coord.x} y ${currentFloor.coord.y}")
            actor.setPosition(currentFloor.coord.x, currentFloor.coord.y)
            bgActor.setPosition(currentFloor.coord.x - 10, currentFloor.coord.y - 10)
            animActor.setPosition(currentFloor.coord.x - 10, currentFloor.coord.y - 10)
            animActor.setOrigin(60f, 60f)
            (animActor as Button).isTransform = true
            animActor.addAction(Actions.fadeOut(0f))
            animActor.touchable = Touchable.disabled


        }

    }

    override fun updateUi() {
//        currentFloor = getFloor(currentFloor.gamecolor, currentFloor.position)

        actor = TextButton("", MassetDescriptor.gameSkin2, "seed-${GameColor.values()[colorId].name.toLowerCase()}")
        actor.setSize(100f, 100f)
        actor.setPosition(currentFloor.coord.x, currentFloor.coord.y)

        bgActor = Button(MassetDescriptor.gameSkin2, "seed-bg")
        bgActor.setSize(120f, 120f)
        bgActor.setPosition(homeFloor.coord.x - 10, homeFloor.coord.y - 10)
        if (moveRemain != MAX_MOVE) {
            bgActor.addAction(Actions.fadeOut(0f))
        }

        animActor = Button(MassetDescriptor.gameSkin2, "seed-indicator")
        animActor.setSize(120f, 120f)
        animActor.setPosition(currentFloor.coord.x - 10, currentFloor.coord.y - 10)
        animActor.setOrigin(60f, 60f)
        (animActor as Button).isTransform = true
        animActor.addAction(Actions.fadeOut(0f))
        animActor.touchable = Touchable.disabled

    }

    override fun resetUi() {

    }

    override fun reset() {
        moveRemain = MAX_MOVE
        currentFloor = homeFloor
        previousFloors = emptyArray()
    }

    init {
        homeFloor = getFloor(color, id * -1)
        startFloor = getFloor(color, 1)
        previousFloors = emptyArray()

        if (!Config.isTest) {


        }


    }

    fun initSeed() {
        homeFloor = getFloor(color, id * -1)
        startFloor = getFloor(color, 1)
        previousFloors = emptyArray()
    }

    fun startAnim() {
        actor.touchable = Touchable.enabled
        animActor.addAction(Actions.fadeIn(0f))
        animActor.addAction(Actions.forever(Actions.parallel(
                Actions.sequence(Actions.scaleBy(0.5f, 0.5f, 1f),
                        Actions.scaleBy(-0.5f, -0.5f, 1f)),
                Actions.rotateBy(360f, 2f)
        )))
    }

    fun stopAnim() {
        actor.touchable = Touchable.disabled
        animActor.setScale(1f)
        animActor.actions.clear()
        animActor.addAction(Actions.fadeOut(0f))
    }

    fun setText(text: String = "") {
        (actor as TextButton).setText(text)
    }

//    fun update() {
//
//    }


    fun getFloor(color: GameColor, position: Int): Floor {

        return Board.findFloor(color, position)
    }

    fun moveTo(floors: Array<Floor>) {
        moveRemain -= floors.size
        previousFloors = floors
        preFloor = currentFloor
        currentFloor = floors[floors.size - 1]
//        actor.setPosition(currentFloor.coord.x, currentFloor.coord.y)
        val acts = arrayOfNulls<Action>(floors.size)
        for (i in floors.indices) {
            val v = floors[i].coord
            acts[i] = Actions.parallel(Actions.moveTo(v.x, v.y, 0.1f - (speed / 100), Interpolation.slowFast), Actions.run { GameManager.playMove() })
//            acts[i] = Actions.moveTo(v.x, v.y, 0.1f - (speed / 100), Interpolation.slowFast)
        }
        val sa = Actions.sequence(*acts)
        actor.addAction(sa)
        animActor.setPosition(currentFloor.coord.x - 10, currentFloor.coord.y - 10)
    }

    fun moveToFloor(floors: Array<Floor>) {
        moveRemain -= floors.size
        previousFloors = floors
        preFloor = currentFloor
        currentFloor = floors[floors.size - 1]
//        actor.setPosition(currentFloor.coord.x, currentFloor.coord.y)

        actor.setPosition(currentFloor.coord.x, currentFloor.coord.y)
        GameManager.playMove()
        animActor.setPosition(currentFloor.coord.x - 10, currentFloor.coord.y - 10)
    }

    fun moveTo(dist: Int) {
        val floor = Board.getNextFloors(this, dist)
        moveRemain -= floor.size
        previousFloors = floor
        preFloor = currentFloor
        currentFloor = floor[floor.size - 1]
        actor.setPosition(currentFloor.coord.x, currentFloor.coord.y)
        animActor.setPosition(currentFloor.coord.x - 10, currentFloor.coord.y - 10)
    }

    fun kill(s: Seed) {
        actor.addAction(Actions.moveTo(Config.lastFloor.coord.x, Config.lastFloor.coord.y))
        moveRemain = 0

        s.movehome()
    }

    fun kill() {
        actor.addAction(Actions.moveTo(Config.lastFloor.coord.x, Config.lastFloor.coord.y))
        moveRemain = 0
    }

    fun isAtSameFloor(seed: Seed) = currentFloor.coord == seed.currentFloor.coord

    fun movehome() {
        previousFloors = arrayOf(homeFloor)
        preFloor = currentFloor
        currentFloor = homeFloor
        moveRemain = MAX_MOVE
        frontNumber = 1
        actor.setPosition(currentFloor.coord.x, currentFloor.coord.y)
        animActor.setPosition(currentFloor.coord.x - 10, currentFloor.coord.y - 10)
        bgActor.addAction(Actions.fadeIn(0f))
    }

    fun moveOut() {
        --moveRemain
        previousFloors = arrayOf(startFloor)
        currentFloor = startFloor
        actor.setPosition(startFloor.coord.x, startFloor.coord.y)
        animActor.setPosition(currentFloor.coord.x - 10, currentFloor.coord.y - 10)
        bgActor.addAction(Actions.fadeOut(0f))
        GameManager.playMoveOut()
    }

    fun clone(): Seed {
        val s = seedPool.obtain()
        s.id = id
        s.color = color
        s.playerId = playerId
        s.colorId = colorId
        s.currentFloor = currentFloor
        s.moveRemain = moveRemain
        s.previousFloors = previousFloors
        // TODO: Implement this method
        return s
    }


    override fun toString(): String { // TODO: Implement this method
        return if (!Config.isTest) "\n color $color id $id current floor $currentFloor frontNumber $frontNumber " else "\n color $color id $id frontNumber $frontNumber"
    }


}