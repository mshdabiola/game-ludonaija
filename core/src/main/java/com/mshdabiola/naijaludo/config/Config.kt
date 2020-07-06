package com.mshdabiola.naijaludo.config

import com.badlogic.gdx.math.Vector2
import com.mshdabiola.naijaludo.entity.board.Floor


object Config {

    var isTest = false


    val WORDLD_WIDTH = 1500f
    val WORLD_HEIGHT = 2700f

    val WORDLD_WIDTH_HALF = WORDLD_WIDTH / 2f
    val WORLD_HEIGHT_HALF = WORLD_HEIGHT / 2f

    val lastFloor = Floor(GameColor.BLUE, 100).apply {
        coord = Vector2(700f, 700f)
    }


    val width = 400
    val height = 600

    val port = 8000
    val ip = 800
}