package com.expansemc.bending.plugin.ability

import com.expansemc.bending.api.ability.AbilityContext
import com.expansemc.bending.api.ability.AbilityContextKey
import java.util.*
import kotlin.NoSuchElementException

class SimpleAbilityContext : AbilityContext {

    private val map = IdentityHashMap<AbilityContextKey<Any>, Any>()

    @Suppress("UNCHECKED_CAST")
    override fun <E : Any> get(key: AbilityContextKey<E>): E? =
        this.map[key] as E?

    @Suppress("UNCHECKED_CAST")
    override fun <E : Any> require(key: AbilityContextKey<E>): E =
        (this.map[key] as E?) ?: throw NoSuchElementException(key.key.toString())

    override fun <E : Any> set(key: AbilityContextKey<E>, value: E) {
        this.map[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <E : Any> remove(key: AbilityContextKey<E>): E? =
        this.map.remove(key) as E?

    override fun contains(key: AbilityContextKey<*>): Boolean =
        key in this.map
}