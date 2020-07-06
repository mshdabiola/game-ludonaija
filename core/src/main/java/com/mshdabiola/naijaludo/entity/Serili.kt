package com.mshdabiola.naijaludo.entity

import com.esotericsoftware.kryo.Kryo

interface Serili {
    fun read(kryo: Kryo, any: Any)
    fun write(kryo: Kryo, any: Any)
}