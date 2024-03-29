package com.mshdabiola.naijaludo.entity.board

import com.badlogic.gdx.math.Vector2
import com.mshdabiola.naijaludo.config.BoardState
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.config.GameColor

//position is relative to it housse
class Floor(val floorColor: GameColor, val position: Int) {

    constructor() : this(GameColor.RED, 0)

    var location = BoardState.SPACE

    //for location all the floor around the board
    var positionIndex = 0
    var isColorPath = false

    @Transient
    val floorTiles = FloorTiles()


    lateinit var coord: Vector2


    override fun toString(): String {
        return "Position is $position Position index is $positionIndex color is $floorColor coor $coord"
    }

    fun isInLastFloor(): Boolean {
        return coord == Config.lastFloor.coord
    }

    override fun equals(floor: Any?): Boolean {
        if ((floor as? Floor) == null)
            return false
        else {
            return floor.coord == coord
        }

    }


}