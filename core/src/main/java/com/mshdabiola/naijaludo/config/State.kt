package com.mshdabiola.naijaludo.config

import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.player.BasePlayer


data class State(val players: Array<BasePlayer>, val currentState: GameState, var diceValue: IntArray) {


    var currentPlayerIndex = 0
    var currentPlayer = players.first()
    var currentDiceIndex = 0
    lateinit var currentSeed: Seed
    var currentDiceNo = 6


    fun setGame() {
        val seed1 = players[0].homeSeed[0]
        seed1.moveOut()
        seed1.moveTo(50)
//        val floor=GameController.getNextFloors(seed1,50)
//
//        seed1.currentFloor=floor[44]
//        seed1.moveRemain-=50
//        seed1.setPosition(floor[49].coord.x,floor[49].coord.y)
        currentSeed = seed1


    }

}