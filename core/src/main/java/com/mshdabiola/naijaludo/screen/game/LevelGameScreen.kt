package com.mshdabiola.naijaludo.screen.game

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.mshdabiola.naijaludo.asset.MassetDescriptor
import com.mshdabiola.naijaludo.config.GameManager
import com.mshdabiola.naijaludo.entity.display.OptionWindowNew
import com.mshdabiola.naijaludo.entity.player.HumanPlayer
import com.mshdabiola.naijaludo.screen.NaijaLudo
import com.mshdabiola.naijaludo.screen.game.logic.NewGameLogic
import com.mshdabiola.naijaludo.screen.menu.MenuScreen
import kotlinx.coroutines.launch

class LevelGameScreen(naijaLudo: NaijaLudo, gameLogic: GameLogic) : GameScreen(naijaLudo, gameLogic) {




    init {
        getNewCurrentLogic()


    }

    fun setLevel() {
        outComeTable2.add(commentLabel)
        commentLabel.setText("Level ${GameManager.currentLevel + 1}")
    }

    var nextButton: TextButton
    var checkUpdate = true

    override val finishedWindow = OptionWindowNew("Finished ", skin = MassetDescriptor.purpleSkinn).apply {
        clearButtonTable()
        nextButton = addButton("Next Level") {
            this@LevelGameScreen.reset()

            isVisible = false
        }
        addButton("Home") {
            changeScreen = Pair(true, MenuScreen(naijaLudo))
        }
    }


    override fun render(delta: Float) {
        super.render(delta)
        if (checkUpdate) {

            if (checkIfOpponentPlayerWin()) {
                nextButton.setText("Play Again")
                replay()
                checkUpdate = false
                changeWindow(finishedWindow)
            }
            if (checkIfPlayerWin()) {
                nextButton.setText("Next Level")
                next()
                checkUpdate = false
                changeWindow(finishedWindow)
            }


        }


    }

    fun checkIfOpponentPlayerWin(): Boolean {
        players.filter { it.lastPoint > 0 && it.id > 0 }
                .forEach { return true }
        return false
    }

    fun checkIfPlayerWin(): Boolean {

        return players[0].lastPoint > 0
    }



    fun playerWin(): Boolean {
        val id = gameController.playerId
        if (gameLogic is NewGameLogic) {
            with(players[id] as HumanPlayer) {
                if (lastPoint == 0 && players.size == 2) {

//                    GameManager.lossOne = 1
                    return false
                }
                if (lastPoint == 1 && players.size == 2) {
//                    GameManager.winOne = 1
                    return true
                }
                if (lastPoint == 0 && players.size > 2) {
//                    GameManager.lossMany = 1
                    return false
                }
                if (lastPoint == players.size - 1 && players.size > 2) {
//                    GameManager.winMany = 1
                    return true
                }
            }
        }
        return false

    }

    override fun initGame() {
        super.initGame()
        setLevel()
        players.forEach { it.playerPanel.hideScoreLabel() }
    }

    override fun reset() {



        nameRow.reset()
        nameRow2.reset()
        outComeTable.reset()
        outComeTable2.reset()
        boardTable.reset()

        diceController = gameLogic.diceController
        gameController = gameLogic.gameController
        gameController.winnerMap.clear()
        players = gameLogic.players
//        setLevel()

        initGame()

        pause()

        boardTable.rotation = 0f
        if (rotate) {
            boardTable.addAction(SequenceAction(
                    Actions.rotateBy(getRotateDegree(players[0].gamecolorsId[0]) + 360f, 2f),
                    Actions.run {
                        println("enter run")
                        resume()

                    }
            ))
        } else {
            boardTable.addAction(SequenceAction(
                    Actions.rotateBy(getRotateDegree(players[0].gamecolorsId[0]), 0f),
                    Actions.run {
                        println("enter run")
                        resume()

                    }
            ))
        }
        checkUpdate = true

    }

    var currentLogic: GameLogic? = null

    //    var prevLogic: GameLogic? = null
    var nextLogic: GameLogic? = null
    fun next() {

        gameLogic = nextLogic!!
        GameManager.currentLevel += 1

        naijaLudo.launch {
            if (GameManager.currentLevel > GameManager.missionLevel) {
                GameManager.missionLevel = GameManager.currentLevel
            }
        }

        getNewCurrentLogic()

    }

//    fun previous() {
//
//
//        gameLogic = prevLogic!!
//        GameManager.currentLevel -= 1
//        getNewCurrentLogic()
//    }

    fun replay() {
        gameLogic = currentLogic!!

        getNewCurrentLogic()
    }


    fun getNewCurrentLogic() {
        val level = GameManager.currentLevel
        naijaLudo.launch {
//            if (level >= 1)
//                prevLogic = naijaLudo.readNewGameLogicSaved(level - 1)
//

            currentLogic = naijaLudo.savedGameGenerator.getGameLogic(level)


            nextLogic = naijaLudo.savedGameGenerator.getGameLogic(level + 1)
        }
    }

    override fun initOptionWindow() {

        optionWindow.cancelButtonFunction = {
            optionWindow.isVisible = false
            resume()
        }
        optionWindow.addButton("Resume") {

            optionWindow.isVisible = false
            resume()
        }
        optionWindow.addButton("Restart") {

            replay()
            reset()
            optionWindow.isVisible = false
        }

        optionWindow.addButton("Resign") {

            changeScreen = Pair(true, MenuScreen(naijaLudo))

        }
        optionWindow.addButton("Exit") {
            changeScreen = Pair(true, MenuScreen(naijaLudo))

        }
    }
}