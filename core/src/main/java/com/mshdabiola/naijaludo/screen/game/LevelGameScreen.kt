package com.mshdabiola.naijaludo.screen.game

import com.badlogic.gdx.Gdx
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
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.N)) {
            next()
            reset()
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.P)) {
            previous()
            reset()
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.R)) {
            replay()
            reset()
        }

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

    override fun scorePlayer() {
        if (playerWin()) {
            nextButton.setText("Next Level")
            next()
        } else {
            nextButton.setText("Play Again")
            replay()
        }

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

    override fun reset() {


        gameLogic.update = true
        gameLogic.saveState = false
        gameLogic.update()

        nameRow.reset()
        nameRow2.reset()
        outComeTable.reset()
        outComeTable2.reset()
        boardTable.reset()

        diceController = gameLogic.diceController
        gameController = gameLogic.gameController
        gameController.winnerMap.clear()
        players = gameLogic.players

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
    var prevLogic: GameLogic? = null
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

    fun previous() {


        gameLogic = prevLogic!!
        GameManager.currentLevel -= 1
        getNewCurrentLogic()
    }

    fun replay() {
        gameLogic = currentLogic!!

        getNewCurrentLogic()
    }


    fun getNewCurrentLogic() {
        val level = GameManager.currentLevel
        naijaLudo.launch {
            if (level >= 1)
                prevLogic = naijaLudo.readNewGameLogic(level - 1)
        }
        naijaLudo.launch {
            currentLogic = naijaLudo.readNewGameLogic(level)
        }
        naijaLudo.launch {
            nextLogic = naijaLudo.readNewGameLogic(level + 1)
        }
    }
}