package com.mshdabiola.naijaludo.entity.player.computer


import com.mshdabiola.naijaludo.config.BoardState
import com.mshdabiola.naijaludo.config.GameColor
import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.board.Board
import com.mshdabiola.naijaludo.entity.board.Floor
import com.mshdabiola.naijaludo.entity.player.BasePlayer


class Probability2Player(id: Int, gameColor: IntArray, name: String = "Probability", isOnlineGame: Boolean = false) : ComputerPlayerBase(name, id, gameColor, isOnlineGame) {
    constructor() : this(0, intArrayOf(), "")


    init {
        //println("enter pacifistic player")
    }

    val map = mapOf(1 to 2.78f,
            2 to 2.78f,
            3 to 5.58f,
            4 to 8.88f,
            5 to 11.11f,
            6 to 13.89f,
            7 to 16.67f,
            8 to 13.89f,
            9 to 11.11f,
            10 to 8.88f,
            11 to 5.58f
    )

    override fun checkBestMove(diceIndex: Int) {
        val diceNo: Int = diceController.getDiceValue(diceIndex)
        if (diceNo == 0) {
            return
        }
        list = gameController.getNewPosition(diceNo)


        bestValueSeed = -1f
        for (seed in list) {
            val value = seedAnalyse2(seed)
            logger.debug("value ---- $value diceno----- $diceNo location id  = ${seed.preFloor.location} seed ${seed.id} color ${seed.color}")
            logger.debug("value ---- $value diceno----- $diceNo location id  = ${seed.currentFloor.location} seed ${seed.id} color ${seed.color}")
            if (value > bestValueSeed) {

                bestValueSeed = value

                bestSeed = changeSeed(seed)!!
            }
        }
        //   logger.debug("test seed from kill opponent $testSeedDelete")
    }

    override fun seedAnalyse(seed: Seed): Float {
        val players: Array<BasePlayer> = gameController.players
        for (player in players) {
            if (player !== gameController.currentPlayer) {
                for (otherPlayerSeed in player.homeSeed) {
                    if (seed.isAtSameFloor(otherPlayerSeed)) {
                        return 1 + random.nextFloat()
                    }
                }
                return if (gameController.isStatingPoint(seed)) {
                    4 + random.nextFloat()
                } else {
                    2 + random.nextFloat()
                }
            }
        }
        return 0f
    }

    fun seedAnalyse2(seed: Seed): Float {
        val otherplayerSeed = gameController.players
                .filter { it != gameController.currentPlayer }
                .flatMap { it.homeSeed }
                .filter { !gameController.isHome(it) }


        //check if it kill opponent     91-80

        if (otherplayerSeed.any { it.isAtSameFloor(seed) }) {
            return 80 + random.nextFloat()
        }
//        otherplayerSeed
//                .firstOrNull { it.isAtSameFloor(seed) }
//                ?.let { return 4 + random.nextFloat() }

        //come out 101-90

        if (gameController.isStatingPoint(seed)) {

            getWhoCornerYou(seed).forEach { cornerFloor ->
                if (otherplayerSeed.any { it.currentFloor == cornerFloor })
                    return 94 + random.nextFloat()
            }
            getHomeFloor(seed, 4).forEach { homeFloor ->
                if (otherplayerSeed.any { it.currentFloor == homeFloor }) {
                    return 100 + random.nextFloat()
                }
            }
            getHomeFloor(seed, 10).forEach { homeFloor ->
                if (otherplayerSeed.any { it.currentFloor == homeFloor }) {
                    return 99 + random.nextFloat()
                }
            }
            return 95 + random.nextFloat()
        }

        //check back 21-10 dont move if seed is at the back
        seed.previousFloors
                .forEach { prevFloor ->
                    if (otherplayerSeed.any { it.currentFloor == prevFloor }) {
                        return 10 + random.nextFloat()
                    }
//
                }

        if (seed.currentFloor.location == BoardState.START && seed.currentFloor.floorColor !in getGameColors()) {
            logger.debug("is start at home ${isSeedAtHomeForCorner(seed)}")
            return 11 + random.nextFloat()
        }

        if (seed.preFloor.location == BoardState.CORNER && isCorner(seed) && isSeedAtHomeForCorner(seed)) {
            logger.debug("is seed at home ${isSeedAtHomeForCorner(seed)} is corner ${isCorner(seed)}")
            return 12 + random.nextFloat()
        }


        //check front 41-30   running after game
        Board.getNextFloors(seed, 6)
                .forEach { nextFloor ->
                    if (otherplayerSeed.any { it.currentFloor == nextFloor }) {
                        return 39 + random.nextFloat()
                    }
//
                }
        Board.getNextFloors(seed, 12)
                .forEach { nextFloor ->
                    if (otherplayerSeed.any { it.currentFloor == nextFloor }) {
                        return 38 + random.nextFloat()
                    }
//
                }
        Board.getNextFloors(seed, 18)
                .forEach { nextFloor ->
                    if (otherplayerSeed.any { it.currentFloor == nextFloor }) {
                        return 37 + random.nextFloat()
                    }
//
                }

        //31-20
        return 29 + random.nextFloat()
    }

    fun getHomeFloor(seed: Seed, num: Int) = Array(num) { Board.findFloor(seed.color, it + 1) }

    fun isSeedAtHomeForCorner(seed: Seed): Boolean {
        val corner = seed.previousFloors.firstOrNull { it.location == BoardState.CORNER }
        corner?.let {
            val nextColorvalue = (it.floorColor.ordinal + 1) % 4
            val nextColor = GameColor.values()[nextColorvalue]

            return getFlatenOtherPlayer().filter { it.color == nextColor }.onEach { logger.debug("seed is $it") }.any { gameController.isHome(it) }
        }

        return false
    }

    fun getWhoCornerYou(seed: Seed): Array<Floor> {
        var prevColorvalue = seed.color.ordinal - 1
        prevColorvalue = if (prevColorvalue < 0) 3 else prevColorvalue
        val prevColor = GameColor.values()[prevColorvalue]
        return Array(4) {
            if (it == 0) Board.findFloor(seed.color, 0)
            else if (it == 1) Board.findFloor(seed.color, 12)
            else Board.findFloor(prevColor, 6 + it)
        }
    }

    fun getFlatenOtherPlayer() = gameController.players.filter { it != gameController.currentPlayer }.flatMap { it.homeSeed }
    fun getNextColor(seed: Seed) = GameColor.values()[(seed.currentFloor.floorColor.ordinal + 1) % 4]

    fun isCorner(seed: Seed): Boolean {
        seed.currentFloor.run {
            if (this.floorColor in getGameColors() && position in 8..11)
                return true
            if (floorColor !in getGameColors() && (position == 12 || position == 0))
                return true

            return false
        }


    }

    fun checkifKill(seed: Seed): Float? {
        val kills = getFlatenOtherPlayer()
                .filter { (it.currentFloor.positionIndex - seed.currentFloor.positionIndex == 0) }
        //send kill
        if (kills.size > 0) {
            return 99f
        }
        //println(" list of seed distance is $list")
        val list = getFlatenOtherPlayer().map {
            val diistance = seed.currentFloor.positionIndex - it.currentFloor.positionIndex

            if (diistance >= 12)
                map[diistance]
            else {
                0f
            }
        }
        return list.reduce { acc, fl -> acc!! + fl!! }

    }
}