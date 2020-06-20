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


    lateinit var currentGameLogic: GameLogic
    var nextGameLogic: GameLogic? = null

    init {
        naijaLudo.launch {
            println("current level is ${GameManager.currentLevel}")
            currentGameLogic = naijaLudo.readNewGameLogic(GameManager.currentLevel!!)
            nextGameLogic = naijaLudo.readNewGameLogic(GameManager.currentLevel!! + 1)
        }
    }

    var nextButton: TextButton

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

    fun updateNextGameLogic() {
        currentGameLogic = nextGameLogic!!

        naijaLudo.launch {
            var currentLevel = GameManager.currentLevel!!
            currentLevel += 1
            GameManager.currentLevel = currentLevel
            println("Next Level is ${GameManager.currentLevel}")

            if (currentLevel > GameManager.missionLevel) {
                GameManager.missionLevel = currentLevel
            }
            nextGameLogic = naijaLudo.readNewGameLogic(currentLevel)
        }

    }

    override fun scorePlayer() {
        if (playerWin()) {
            nextButton.setText("Next Level")
        } else {
            nextButton.setText("Play Again")
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

        val galogic: GameLogic = if (playerWin()) {
            updateNextGameLogic()
            val logic = currentGameLogic
            naijaLudo.launch {
                currentGameLogic = naijaLudo.readNewGameLogic(GameManager.currentLevel!!)
            }
            logic

        } else {
            val logic = currentGameLogic
            naijaLudo.launch {
                currentGameLogic = naijaLudo.readNewGameLogic(GameManager.currentLevel!!)
            }
            logic
        }
        galogic.update = true
        galogic.saveState = false
        galogic.update()

        gameLogic = galogic

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

    }
}