package com.mshdabiola.naijaludo.entity

import com.mshdabiola.naijaludo.config.GameState
import com.mshdabiola.naijaludo.entity.connection.ControlVariableSendable

data class ControlVariable(
        var currentState: GameState = GameState.PLAY,
        var currentPlayerIndex: Int = 0,
        var currentDiceIndex: Int = -1,
        var currentDiceNo: Int = 0,
        var noSeedActivated: Int = 0
) {
    fun setClass(controlVariableSendable: ControlVariableSendable) {
        currentState = GameState.values()[controlVariableSendable.currentStateNo]
        currentDiceIndex = controlVariableSendable.currentDiceIndex
        currentDiceNo = controlVariableSendable.currentDiceNo
        currentPlayerIndex = controlVariableSendable.currentPlayerIndex
        noSeedActivated = controlVariableSendable.noSeedActivated

    }

    fun getClass(): ControlVariableSendable {
        return ControlVariableSendable(currentState.ordinal, currentPlayerIndex, currentDiceIndex, currentDiceNo, noSeedActivated)
    }

}