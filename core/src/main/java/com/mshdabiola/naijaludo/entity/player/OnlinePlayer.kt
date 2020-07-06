package com.mshdabiola.naijaludo.entity.player

class OnlinePlayer(id: Int, gameColor: IntArray, name: String) : BasePlayer(name, id, gameColor) {
    constructor() : this(0, intArrayOf(), "")

    override fun chooseDice() {

    }

    override fun chooseSeed() {

    }

    override fun toss() {

    }


}