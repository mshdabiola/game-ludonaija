package com.mshdabiola.naijaludo.config

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Logger
import com.mshdabiola.naijaludo.asset.MassetDescriptor

object GameManager {
    private val logger = Logger(GameManager::class.java.name, Logger.DEBUG)
    val preferencesName = "Naijaludo"
    val pref = Gdx.app.getPreferences(preferencesName)

    val onOfseedIntArray = Array<Int>().apply {
        add(1, 2, 3, 4)
    }

    val nameS = "name"
    val avatarS = "avatar"
    val levelS = "level"
    val styleS = "style"
    val rotateS = "rotate"
    val seedNoS = "seedNo"
    val animatorS = "animator"
    val seedSpeedS = "seedSpeed"
    val assistantS = "assistant"
    val musicS = "music"
    val soundS = "sound"
    val musicNumbers = "musicNumber"

    val missionLevelS = "missionLevel"

    val winOneS = "winOne"
    val lossOneS = "lossOne"
    val winManyS = "winMany"
    val lossManyS = "lossMany"
    val winFriendS = "winFriend"
    val lossFriendS = "lossFriend"


    var seedNO: Int = 0
        get() = onOfseedIntArray.get(noOfSeed)

    var name: String
        get() {
            val nameStr = pref.getString(nameS, "Player")
//            logger.debug("read name")
            return if (nameStr.isEmpty()) "" else nameStr
        }
        set(value) {
//            logger.debug("write name $value")
            pref.putString(nameS, value)
            pref.flush()

        }
    var avatar: Int
        get() = pref.getInteger(avatarS, 7)
        set(value) {
            pref.putInteger(avatarS, value)
            pref.flush()
        }

    var level: Int
        get() = pref.getInteger(levelS, 0)
        set(value) {
            pref.putInteger(levelS, value)
            pref.flush()
        }

    var missionLevel: Int
        get() = pref.getInteger(missionLevelS, 0)
        set(value) {
            pref.putInteger(missionLevelS, value)
            pref.flush()
        }


    var style: Int
        get() = pref.getInteger(styleS, 0)
        set(value) {
            pref.putInteger(styleS, value)
            pref.flush()
        }

    var rotate: Boolean
        get() = pref.getBoolean(rotateS, true)
        set(value) {
            pref.putBoolean(rotateS, value)
            pref.flush()
        }

    var noOfSeed: Int
        get() = pref.getInteger(seedNoS, 3)
        set(value) {
            pref.putInteger(seedNoS, value)
            pref.flush()
        }
    var animator: Boolean
        get() = pref.getBoolean(animatorS, true)
        set(value) {
            pref.putBoolean(animatorS, value)
            pref.flush()
        }

    var seedSpeed: Int
        get() = pref.getInteger(seedSpeedS, 3)
        set(value) {
            pref.putInteger(seedSpeedS, value)
            pref.flush()
        }
    var assistant: Boolean
        set(value) {
            pref.putBoolean(assistantS, value)
            pref.flush()
        }
        get() = pref.getBoolean(assistantS, true)

    var isFirst: Boolean
        set(value) {
            pref.putBoolean("first", value)
            pref.flush()
        }
        get() = pref.getBoolean("first", true)

    var sound: Boolean
        set(value) {
            pref.putBoolean(soundS, value)
            pref.flush()
        }
        get() = pref.getBoolean(soundS, false)
    var music: Boolean
        set(value) {
            pref.putBoolean(musicS, value)
            pref.flush()
        }
        get() = pref.getBoolean(musicS, false)
    var musicNumber: Int
        set(value) {
            pref.putInteger(musicNumbers, value)

            MassetDescriptor.music.stop()
            pref.flush()
            loadMusic()

            playMusic()
        }
        get() = pref.getInteger(musicNumbers, 1)

    var winOne: Int
        get() = pref.getInteger(winOneS, 0)
        set(value) {

            pref.putInteger(winOneS, value + winOne)
            pref.flush()
        }

    var winMany: Int
        get() = pref.getInteger(winManyS, 0)
        set(value) {

            pref.putInteger(winManyS, value + winMany)
            pref.flush()
        }
    var winFriend: Int
        get() = pref.getInteger(winFriendS, 0)
        set(value) {

            pref.putInteger(winFriendS, value + winFriend)
            pref.flush()
        }

    var lossOne: Int
        get() = pref.getInteger(lossOneS, 0)
        set(value) {

            pref.putInteger(lossOneS, value + lossOne)
            pref.flush()
        }

    var lossMany: Int
        get() = pref.getInteger(lossManyS, 0)
        set(value) {

            pref.putInteger(lossManyS, value + lossMany)
            pref.flush()
        }
    var lossFriend: Int
        get() = pref.getInteger(lossFriendS, 0)
        set(value) {

            pref.putInteger(lossFriendS, value + lossFriend)
            pref.flush()
        }


    fun loadMusic() {
        MassetDescriptor.music = Gdx.audio.newMusic(Gdx.files.internal("sound/sound_${musicNumber + 1}.mp3"))
    }

    fun playSelect() {
        if (sound) {
            MassetDescriptor.selectSound.play()
        }
    }

    fun playMove() {
        if (sound) {
            MassetDescriptor.moveSound.play()
        }
    }

    fun playMoveOut() {
        if (sound) {
            MassetDescriptor.moveOutSound.play()
        }
    }

    fun playDice() {
        if (sound) {
            MassetDescriptor.diceSound.play()
        }
    }

    fun playKill() {
        if (sound) {
            MassetDescriptor.killSound.play()
        }
    }

    fun playMusic() {
        if (music) {
            MassetDescriptor.music.let {

                it.play()
                it.isLooping = true
                it.volume = 0.01f
            }
        }

    }


    var currentLevel: Int? = null

}