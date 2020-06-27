package com.mshdabiola.naijaludo.screen.game

import com.badlogic.gdx.utils.Logger
import com.mshdabiola.naijaludo.config.GameState
import com.mshdabiola.naijaludo.entity.Seed
import com.mshdabiola.naijaludo.entity.Seed.Companion.MAX_MOVE
import com.mshdabiola.naijaludo.entity.board.Board
import com.mshdabiola.naijaludo.entity.board.Floor
import com.mshdabiola.naijaludo.entity.connection.Factory
import com.mshdabiola.naijaludo.entity.player.BasePlayer


open class GameController {


    private var isActivate = true
    var playerId = 0

    @Transient
    private val logger = Logger(GameController::class.java.name, Logger.DEBUG)


    @Transient
    lateinit var diceController: DiceController

    lateinit var currentSeed: Seed

    @Transient
    lateinit var currentPlayer: BasePlayer

    @Transient
    lateinit var players: Array<BasePlayer>

    var currentState: GameState = GameState.PLAY
    var currentPlayerIndex: Int = 0
        set(value) {
            if (players == null)
                currentPlayer = players[value]
            field = value
        }
    var currentDiceIndex: Int = -1
    var currentDiceNo: Int = 0
    var noSeedActivated: Int = 0

    var animate = true
    var assistant = true

    @Transient
    var exitFunction: () -> Unit = {}

    @Transient
    var swapOutcome: () -> Unit = {}

    @Transient
    var pause: () -> Unit = {}

    @Transient
    var resume: () -> Unit = {}
    val winnerMap = HashMap<Int, Int>()


    init {

    }

//    var controlVariable = ControlVariable()


    private var prevState = currentState


    var timer = 0f
    var time = 1f


    fun update(delta: Float) {
        if (currentState != prevState) {

            logger.debug("current state ${currentState}")
            logger.debug("current player index ${currentPlayerIndex}")

            prevState = currentState
        }

        //for online game


        when (currentState) {
            GameState.PLAY -> {
                play()
            }
            GameState.HASPLAY -> {
                hasPlay()
            }
            GameState.TOSS -> {
                toss()
            }
            GameState.HASTOSS -> {
                hasToss()
            }
            GameState.CHOOSEDICE -> {
                chooseDice()
            }
            GameState.HASCHOOSEDICE -> {
                hasChooseDice()
//                GameConfig.log.debug("current  " + currentPlayerIndex);

            }
            GameState.CHOOSESEED -> {
                chooseSeed()

            }
            GameState.HASCHOOSESEED -> {
                hasChooseSeed()

//                GameConfig.log.debug("HasChooseSeed dice no " + currentDiceNo + " seed " + currentSeed);

            }
            GameState.COUNT -> {
                count()

            }
            GameState.HASCOUNT -> {
                hasCount(delta)

            }
        }
    }

    open fun play() {
        //remove indicator when player change
        if (this::currentPlayer.isInitialized) {
            currentPlayer.playerPanel.removeIndicator()
        }
        currentPlayer = players[currentPlayerIndex]
        currentPlayer.playerPanel.moveIndicator()
        diceController.diceDisplayTouchable(false)
        diceController.diceTouchable(false)
        deactivateSeed()
        currentState = GameState.TOSS
//        if(currentPlayer is HumanPlayer){
//            diceController.diceTouchable(true)
//        }
    }

    open fun hasPlay() {
        diceController.setToZero()

        if (currentPlayer.homeSeed.isEmpty() && !winnerMap.containsKey(currentPlayer.id)) {
            val size = winnerMap.size
            winnerMap[currentPlayer.id] = size
            val point = (players.size - winnerMap.size)

            logger.debug("playerid $currentPlayer and point is $point")
            currentPlayer.lastPoint = point

            currentPlayer.point += point


            currentPlayer.playerPanel.updateScore()

        }
        if (players.size - winnerMap.size == 1) {

            exitFunction()
            pause()

        }
        resetVariable()
        diceController.setDiceToZero()

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size

        //friend outcome logic
        if (GameScreen.isFriend) {
            swapOutcome()
        }
        //                GameConfig.log.debug("current player index " + currentPlayerIndex);
        currentState = GameState.PLAY
    }

    open fun toss() {
        if (currentPlayer.homeSeed.isNotEmpty()) {
            currentPlayer.toss()
        } else {
            currentState = GameState.HASPLAY
        }
    }

    open fun hasToss() {
        diceController.tossDice = true

//                GameConfig.log.debug("dice1 " + diceController.getValue(0) + " dice 2 " + diceController.getValue(1));
        currentState = if (somethingToDo(diceController.getDiceValue(0)) || somethingToDo(diceController.getDiceValue(1))) {
            if (somethingToDo(diceController.getDiceValue(0))) {
                diceController.outcomeDiceOutcome.outCome1.startRotate()
            }
            if (somethingToDo(diceController.getDiceValue(1))) {
                diceController.outcomeDiceOutcome.outCome2.startRotate()
            }
            if (somethingToDo(diceController.getDiceValue(2))) {
                diceController.outcomeDiceOutcome.outComeTotal.startRotate()
            }

            GameState.CHOOSEDICE
        } else if (diceController.isDoubleSix()) {
            GameState.TOSS

        } else {

            GameState.HASPLAY
        }
    }

    open fun chooseDice() {
        currentPlayer.chooseDice()
    }

    open fun hasChooseDice() {
        currentState = if (somethingToDo(currentDiceNo)) {
            GameState.CHOOSESEED
        } else {
            GameState.CHOOSEDICE
        }
    }

    open fun chooseSeed() {

        if (assistant && noSeedActivated == 1) {
            currentSeed = getOneSeed(currentDiceNo).apply {
                actor.toFront()
            }
            currentState = GameState.HASCHOOSESEED
        } else {
            currentPlayer.chooseSeed()
        }
    }

    open fun hasChooseSeed() {
        currentState = GameState.COUNT
    }

    open fun count() {
        time = 0.1f * currentDiceNo + 0.2f

        currentState = if (isHome(currentSeed)) {

            //count 6 dice to come out of house
            diceController.countDice(currentDiceIndex)
            currentSeed.moveOut()

            GameState.HASCOUNT

        } else {
            //count other dices
            diceController.countDice(currentDiceIndex)
            diceController.diceMoveToFront()
            moveTo(currentDiceNo)
            GameState.HASCOUNT
        }
        textSeedInFloorArea(currentSeed.currentFloor, currentSeed)
        textSeedInFloorArea(currentSeed.preFloor, null)

    }

    open fun hasCount(delta: Float) {
        timer += delta
        if (timer >= time) {
            timer = 0f

            //check seed finish race
            if (currentSeed.currentFloor.isInLastFloor()) {
                currentPlayer.homeSeed.remove(currentSeed)
                currentPlayer.seedOut.add(currentSeed)
                currentPlayer.playerPanel.addSeedOut(currentSeed)


            }
            //check seed kill
            checkKillTest()

            //check next move
            currentState = if (diceController.isDoubleSix() && diceController.hasCountFinished()) {
                GameState.TOSS

            } else if (diceController.hasCountFinished() || !somethingToDo(diceController.getSecondDiceValue(currentDiceIndex))) {
                GameState.HASPLAY
            } else {
                GameState.HASTOSS
            }

        }

    }

    fun isManySeedInThisArea(floor: Floor): Boolean {
        val num = currentPlayer.homeSeed
                .filter { it.currentFloor == floor }
                .distinctBy { it.colorId }
                .count()

        return num > 1
    }

    fun textSeedInFloorArea(floor: Floor, seedSend: Seed?) {
        val num = currentPlayer.homeSeed.filter { it.currentFloor == floor }.count()

        logger.debug("number of seed in area is $num")

        if (num > 1) {
            val seeds = currentPlayer.homeSeed.filter { it.currentFloor == floor }
                    .onEach { it.setText() }.first()
            if (seedSend != null) {
                seedSend.actor.toFront()
                seedSend.setText("$num")
                seeds.frontNumber = num
            } else {
                seeds.actor.toFront()
                seeds.setText("$num")
                seeds.frontNumber = num
            }
        } else if (num == 1) {
            currentPlayer.homeSeed.filter { it.currentFloor == floor }
                    .onEach {
                        it.setText()
                        it.frontNumber = 1
                    }
        }
    }

    private fun resetVariable() {

        currentDiceIndex = -1
        currentDiceNo = 0
    }

    fun activateSeedNew(diceNo: Int) {

        //check if diceno is 0
        if (diceNo == 0) {
            noSeedActivated = 0
            return
        }
        if (isActivate) {
            val first = currentPlayer.homeSeed
                    .filter { (isHavingSeedAtHomeNew() && diceController.isHaveingSix() && diceNo == 6 && isHome(it) || isMovable(it, diceNo)) }
                    .onEach {
//                        it.actor.touchable = Touchable.enabled
//                        it.actor.addAction(Actions.repeat(10, SequenceAction(Actions.fadeIn(0.5f, Interpolation.fade), Actions.fadeOut(0.5f, Interpolation.fade))))
                        it.startAnim()
                    }
                    .count()


            noSeedActivated = first

            logger.debug("dice activated are ${noSeedActivated}")
            isActivate = false
        }
    }

    fun getOneSeed(diceNo: Int): Seed {


        return currentPlayer.homeSeed.first { (isHavingSeedAtHomeNew() && diceController.isHaveingSix() && diceNo == 6 && isHome(it) || isMovable(it, diceNo)) }

    }

    fun deactivateSeed() {
        currentPlayer.homeSeed.forEach {
//            it.actor.touchable = Touchable.disabled
//            it.actor.clearActions()
//            it.actor.addAction(Actions.alpha(1f))
            it.stopAnim()
            isActivate = true
        }

    }

    fun getFlatenPlayerSeed() = players.flatMap { it.homeSeed }


    fun somethingToDo(diceno: Int): Boolean {
        noSeedActivated = 0
        activateSeedNew(diceno)
        deactivateSeed()
        return noSeedActivated > 0
    }

    private fun isHavingSeedAtHomeNew(): Boolean {

        return currentPlayer.homeSeed.firstOrNull { isHome(it) } != null
    }


    fun isHome(seed: Seed): Boolean {
        return seed.moveRemain == MAX_MOVE
    }

    fun isStatingPoint(seed: Seed): Boolean {
        return seed.moveRemain == MAX_MOVE - 1
    }

    fun isAboutToFinish(seed: Seed): Boolean {
        return seed.moveRemain == 5
    }

    fun isMovable(seed: Seed, dice: Int): Boolean {
        return !isHome(seed) && seed.moveRemain >= dice
    }

    private fun moveTo(dist: Int) {
        val next: Array<Floor> = Board.getNextFloors(currentSeed, dist)

        if (animate) {
            currentSeed.moveTo(next)
        } else {
            currentSeed.moveToFloor(next)
        }
    }

    private fun moveTo(seed: Seed, dist: Int) {
        val next: Array<Floor> = Board.getNextFloors(seed, dist)

        seed.moveTo(next)

    }


    fun checkKillTest() {

        val list = getFlatenPlayerSeed()
                .filter { it.playerId != currentPlayer.id && it.currentFloor == currentSeed.currentFloor }
                .sortedBy { -it.frontNumber }
        logger.debug("checkKillTest list is  $list")
        if (list.size == 1) {
            list[0].let {
                somethingToDo(diceController.getSecondDiceValue(currentDiceIndex))
                if (!diceController.hasCountFinished() && noSeedActivated == 1)
                    return

                currentPlayer.homeSeed.remove(currentSeed)
                currentPlayer.seedOut.add(currentSeed)
                it.setText()

                currentSeed.kill(it)
                diceController.diceMoveToFront()

                textSeedInFloorArea(it.preFloor, null)
                currentPlayer.playerPanel.addSeedOut(currentSeed)
            }
        } else if (list.size == 2) {
            list[0].let {
                somethingToDo(diceController.getSecondDiceValue(currentDiceIndex))
                if (!diceController.hasCountFinished() && noSeedActivated == 1)
                    return

                currentPlayer.homeSeed.remove(currentSeed)
                currentPlayer.seedOut.add(currentSeed)
                it.setText()

                currentSeed.kill(it)
                diceController.diceMoveToFront()

                list[1].frontNumber = list.size - 1
                list[1].setText("")
                list[1].actor.toFront()

                currentPlayer.playerPanel.addSeedOut(currentSeed)
            }
        } else if (list.size > 2) {
            list[0].let {
                somethingToDo(diceController.getSecondDiceValue(currentDiceIndex))
                if (!diceController.hasCountFinished() && noSeedActivated == 1)
                    return

                currentPlayer.homeSeed.remove(currentSeed)
                currentPlayer.seedOut.add(currentSeed)
                it.setText()

                currentSeed.kill(it)
                diceController.diceMoveToFront()

                list[1].frontNumber = list.size - 1
                list[1].setText("${list.size - 1}")
                list[1].actor.toFront()
                currentPlayer.playerPanel.addSeedOut(currentSeed)
            }
        }

    }

    fun getNewPosition(diceNo: Int): ArrayList<Seed> {
        val first = currentPlayer.homeSeed
                .filter { (isHavingSeedAtHomeNew() && diceController.isHaveingSix() && diceNo == 6 && isHome(it) || isMovable(it, diceNo)) }
                .map {
                    val clone = it.clone()
                    if (isMovable(it, diceNo))
                        moveTo(clone, diceNo)
                    else
                        clone.moveOut()

                    clone
                }
        return first as ArrayList<Seed>
    }

    open fun dispose() {

    }


    //online code and function


    var seedToSend: Factory.SeedToSend? = null
    var sendCurrentPlayerIndex = -1

    var sendChooseDiceIndex = -1
    var sendDiceValue: Factory.DiceValue? = null
    fun onlineToss() {
        logger.debug("currentPlayer ${currentPlayer.name} index $currentPlayerIndex sendDiceValue ${sendDiceValue != null}")
        if (currentPlayerIndex != playerId && sendDiceValue != null) {
            diceController.tossWithValue(sendDiceValue!!.dice1Value, sendDiceValue!!.dice2Value)
            sendDiceValue = null
            currentState = GameState.HASTOSS
        }
    }

    fun onlineChooseDice() {
        logger.debug("currentPlayer ${currentPlayer.name} index $currentPlayerIndex sendChooseDiceIndex ${sendChooseDiceIndex}")
        if (currentPlayerIndex != playerId && sendChooseDiceIndex != -1) {
            currentDiceIndex = sendChooseDiceIndex
            currentDiceNo = diceController.getDiceValue(currentDiceIndex)
            sendChooseDiceIndex = -1
            currentState = GameState.HASCHOOSEDICE
        }
    }

    fun onlineChooseSeed() {
        logger.debug("currentPlayer ${currentPlayer.name} index $currentPlayerIndex seedTosend ${seedToSend}")
        if (currentPlayerIndex != playerId && seedToSend != null) {
            println("onlinechooseSeed() ")
            currentSeed = currentPlayer.homeSeed.find { it.playerId == seedToSend!!.ids[0] && it.colorId == seedToSend!!.ids[1] && it.id == seedToSend!!.ids[2] }!!
            println("onlinechooseseed $currentSeed")
            seedToSend = null
            currentState = GameState.HASCHOOSESEED
        }
    }


}