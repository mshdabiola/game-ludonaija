package com.mshdabiola.naijaludo.asset

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.ui.Skin


object MassetDescriptor {


    //    val gameBackGround = AssetDescriptor(AssetName.gameBackGround, Texture::class.java)
//    val uiSkin = AssetDescriptor(AssetName.uiSkin, Skin::class.java)
//    val gameSkinn = AssetDescriptor(AssetName.gameSkinn, Skin::class.java)
    val gameSkinn2 = AssetDescriptor(AssetName.gameSkin2, Skin::class.java)
    val purpleSkin = AssetDescriptor(AssetName.purpleSkin, Skin::class.java)

    lateinit var gameSkin2: Skin

    lateinit var purpleSkinn: Skin
    lateinit var killSound: Sound
    lateinit var moveSound: Sound
    lateinit var moveOutSound: Sound
    lateinit var selectSound: Sound
    lateinit var diceSound: Sound
    lateinit var music: Music
}