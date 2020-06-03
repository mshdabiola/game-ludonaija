package com.mshdabiola.naijaludo.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.scenes.scene2d.Actor

open class BaseActor : Actor() {
    var isColorPath = false
    var isOutLinePath = false

    //private Rectangle bounds;
    private val region: TextureRegion? = null
    override fun drawDebug(renderer: ShapeRenderer) {
        // TODO: Implement this method
        val t = renderer.currentType
        val oldColor = renderer.color.cpy()
        if (isColorPath) {
            renderer.set(ShapeType.Filled)
            renderer.color = color
            renderer.rect(x, y, width, height)
            renderer.set(ShapeType.Line)
            renderer.color = Color.WHITE
            renderer.rect(x, y, width, height)
            renderer.set(t)
        } else {
            renderer.set(ShapeType.Line)
            renderer.color = Color.WHITE
            renderer.rect(x, y, width, height)
            renderer.set(t)
        }
        renderer.set(t)
        renderer.color = oldColor
    }
}