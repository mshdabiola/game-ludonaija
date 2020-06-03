package com.mshdabiola.naijaludo.entity.dice

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.mshdabiola.naijaludo.asset.MassetDescriptor

class Dice(val id: Int) {

    constructor() : this(0)

    val loc2 = Pair(750f, 675f)
    val loc1 = Pair(600f, 675f)

    @Transient
    var actor: Actor = Button(MassetDescriptor.gameSkin2, "dice-roll-${id + 1}")
    var value = 0
        set(value) {
            if (value == 0) {
                (actor as Button).style = MassetDescriptor.gameSkin2.get("dice-roll-${id + 1}", Button.ButtonStyle::class.java)

            } else {
                (actor as Button).style = MassetDescriptor.gameSkin2.get("dice-$value", Button.ButtonStyle::class.java)

            }
            field = value
        }

    init {
        actor.setSize(150f, 150f)
        (actor as Button).isTransform = true
        actor.setOrigin(150f / 2, 150f / 2)

    }

    fun moveToDefault() {
        if (id == 0)
            actor.setPosition(loc1.first, loc1.second)
        else {
            actor.setPosition(loc2.first, loc2.second)
        }
    }

}
