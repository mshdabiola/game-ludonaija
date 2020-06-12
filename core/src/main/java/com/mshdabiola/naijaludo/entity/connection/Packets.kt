package com.mshdabiola.naijaludo.entity.connection

import com.esotericsoftware.kryonet.EndPoint
import com.mshdabiola.naijaludo.config.GameState
import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.board.Floor
import com.mshdabiola.naijaludo.screen.game.DiceController
import com.mshdabiola.naijaludo.screen.game.GameController


class Connect(val ids: Int,
              val name: String) {
    constructor() : this(1, "")
}

class SendName(val name: String) {
    constructor() : this("0")
}

class GameControllerPacket(val gameController: GameController?) {
    constructor() : this(null)
}

object RegisterName
object Pause
object Resume
object AcceptName
object Start
object NewPlayerData
class TakePlayerData(val array: Array<PlayerData>)

class ControlVariableSendable(
        var currentStateNo: Int = 0,
        var currentPlayerIndex: Int = 0,
        var currentDiceIndex: Int = -1,
        var currentDiceNo: Int = 0,
        var noSeedActivated: Int = 0
)

class PlayerData(
        val playerId: Int,
        val gameColorId: IntArray,
        val name: String
) {
    constructor() : this(0, IntArray(0), "")
}

class SeedData(
        val id: Int,
        val colorId: Int,
        val x: Float,
        val y: Float,
        val moveRemain: Int
) {
    constructor() : this(0, 0, 0f, 0f, 1)
}


object Packets {

    val port = 8888

    val port2 = 8579
    val port3 = 1289
    fun registerPacketFor(endPoint: EndPoint) {
//        if (endPoint is Client) {
        with(endPoint.kryo) {
            register(Connect::class.java)
            references = true
            register(String::class.java)
            register(RegisterName::class.java)
            register(AcceptName::class.java)
            register(SendName::class.java)
            register(Pause::class.java)
            register(Resume::class.java)
            register(Start::class.java)
            register(GameController::class.java)
            register(GameControllerPacket::class.java)
            register(Seed::class.java)
            register(Floor::class.java)
            register(GameState::class.java)
            register(DiceController::class.java)


        }

    }
}