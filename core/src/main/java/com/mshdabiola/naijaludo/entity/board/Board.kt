package com.mshdabiola.naijaludo.entity.board

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Logger
import com.mshdabiola.naijaludo.config.BoardState
import com.mshdabiola.naijaludo.config.Config
import com.mshdabiola.naijaludo.config.GameColor
import com.mshdabiola.naijaludo.entity.Seed
import java.util.*


class Board : Table() {
    private val logger = Logger(Board::class.java.name, Logger.DEBUG)
    val oneCell = 100f
    // val boardFloors = ArrayList<Floor>()

    companion object {
        val boardFloors = ArrayList<Floor>()
        val lastPositionFloor = -100
        fun findFloor(color: GameColor, position: Int): Floor {
            return boardFloors.find { it.floorColor == color && it.position == position }
                    ?: Config.lastFloor
        }

        private fun getNextFloor(seed: Seed): Floor {
            val prev = seed.currentFloor
            var color = prev.floorColor
            var prevId = prev.position

            //check if seed is at front of its home
            return if (color == seed.color && seed.currentFloor.position >= 12) {

                findFloor(color, ++prevId)

            } else {
                //move other

                ++prevId  //increase position
                if (prevId == 12) {
                    //is at the front of another seed change color
                    val colorId = (color.ordinal + 1) % 4
                    color = GameColor.values()[colorId] //change color
                }

                //move past another seed house
                if (prevId == 13) {
                    prevId = 0
                }


                findFloor(color, prevId)

            }
        }


        fun getNextFloors(seed: Seed, dist: Int): Array<Floor> {
            val seed2 = seed.clone()
            val floors = Array(dist) {
                val floor = getNextFloor(seed2)
                seed2.currentFloor = floor

                floor
            }
            Seed.seedPool.free(seed2)
            return floors
        }
    }

    init {
        top()
        boardFloors.clear()
        createTable()
        isTransform = true


    }

    override fun setStage(stage: Stage?) {
        super.setStage(stage)
        if (stage != null) {
            setPostion()
//            logger.debug(" floors are "+ boardFloors.joinToString  (separator = "\n"))
        }
    }

    fun setPostion() {


        boardFloors.forEach {
            var vec = Vector2()
            vec = localToDescendantCoordinates(it.floorTiles, vec)

            it.coord = Vector2(Math.abs(vec.x), Math.abs(vec.y))

        }
    }


    fun createTable() {
        val firstRow = Table()
        val secondRow = Table()
        val thirdRow = Table()

        firstRow.add(createHome(GameColor.values()[1])).width(600f).height(600f)
        firstRow.add(createFloorOfColumn((6..11).reversed(), GameColor.values()[1]))
        firstRow.add(createFloorOfColumn(12..17, GameColor.values()[2], 13, 14, 15, 16, 17))
        firstRow.add(createFloorOfColumn(0..5, GameColor.values()[2], 1))
        firstRow.add(createHome(GameColor.values()[2])).width(600f).height(600f)

        val tableGroup1 = Table()
        tableGroup1.add(createFloorOfRow(0..5, GameColor.values()[1], 1))
        tableGroup1.row()
        tableGroup1.add(createFloorOfRow(12..17, GameColor.values()[1], 13, 14, 15, 16, 17))
        tableGroup1.row()
        tableGroup1.add(createFloorOfRow((6..11).reversed(), GameColor.values()[0]))

        val tableGroup2 = Table()
        tableGroup2.add(createFloorOfRow((6..11), GameColor.values()[2]))
        tableGroup2.row()
        tableGroup2.add(createFloorOfRow((12..17).reversed(), GameColor.values()[3], 13, 14, 15, 16, 17))
        tableGroup2.row()
        tableGroup2.add(createFloorOfRow((0..5).reversed(), GameColor.values()[3], 1))

        secondRow.add(tableGroup1)
        secondRow.add(Floor(GameColor.YELLOW, lastPositionFloor).apply {
            isColorPath = false
            //color = Color.GREEN
        }.floorTiles).width(300f).height(300f)
        secondRow.add(tableGroup2)

        thirdRow.add(createHome(GameColor.values()[0])).width(600f).height(600f)
        thirdRow.add(createFloorOfColumn((0..5).reversed(), GameColor.values()[0], 1))
        thirdRow.add(createFloorOfColumn((12..17).reversed(), GameColor.values()[0], 13, 14, 15, 16, 17))
        thirdRow.add(createFloorOfColumn((6..11), GameColor.values()[3]))
        thirdRow.add(createHome(GameColor.values()[3])).width(600f).height(600f)


        add(firstRow)
        row()
        add(secondRow)
        row()
        add(thirdRow)
    }


    fun createFloorOfRow(range: IntProgression, color: GameColor, vararg colorNo: Int): Table {
        val table = Table()

        for (i in range) {
            //  logger.debug("color is $color index is $i")
            val floor = Floor(color, i)


            if (i in colorNo) {
                floor.isColorPath = true

            }

            if (i in 9..11) {
                floor.location = BoardState.CORNER
            }
            if (i == 1) {
                floor.location = BoardState.START
            }
            if (i == 0 || i == 12) {
                floor.location = BoardState.CORNER
            }
            boardFloors.add(floor)
            table.add(floor.floorTiles).width(100f).height(100f)
        }
        return table
    }

    fun createFloorOfColumn(range: IntProgression, color: GameColor, vararg colorNo: Int): Table {
        val table = Table()
        for (i in range) {
            //logger.debug("color is $color index is $i")

            val floor = Floor(color, i)


            if (i in colorNo) {
                floor.isColorPath = true
            }
            if (i in 9..11) {
                floor.location = BoardState.CORNER
            }
            if (i == 1) {
                floor.location = BoardState.START
            }
            if (i == 0 || i == 12) {
                floor.location = BoardState.CORNER
            }
            boardFloors.add(floor)
            table.add(floor.floorTiles).width(100f).height(100f)
            table.row()
        }
        return table
    }


    fun createHome(color: GameColor): Table {
        val table = Table()

        val firstRow = Table()
        val floors = Array<Floor>(4) { getFloorWithColor(color, it + 1) }

        boardFloors.addAll(floors)

        firstRow.add(floors[0].floorTiles).width(oneCell).height(oneCell).padRight(2 * oneCell)
        firstRow.add(floors[1].floorTiles).width(oneCell).height(oneCell)


        val secondRow = Table()


        secondRow.add(floors[2].floorTiles).width(oneCell).height(oneCell).padRight(2 * oneCell)
        secondRow.add(floors[3].floorTiles).width(oneCell).height(oneCell)

        table.pad(oneCell)

        table.add(firstRow).padBottom(2 * oneCell)

        table.row()
        table.add(secondRow)

        return table
    }

    fun getFloorWithColor(color: GameColor, position: Int): Floor {
        val floor = Floor(color, -position)
        floor.isColorPath = true
        floor.location = BoardState.ATHOME
        return floor
    }

}