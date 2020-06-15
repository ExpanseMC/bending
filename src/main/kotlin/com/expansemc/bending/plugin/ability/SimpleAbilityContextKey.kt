package com.expansemc.bending.plugin.ability

import com.expansemc.bending.api.ability.AbilityContextKey
import com.google.common.reflect.TypeToken
import org.bukkit.NamespacedKey

data class SimpleAbilityContextKey<out E : Any>(
    override val key: NamespacedKey,
    override val allowedType: TypeToken<out E>
) : AbilityContextKey<E> {

    class Builder<E : Any> : AbilityContextKey.Builder<E> {

        private var key: NamespacedKey? = null
        private var type: TypeToken<E>? = null

        override fun key(key: NamespacedKey): AbilityContextKey.Builder<E> {
            this.key = key
            return this
        }

        override fun type(type: TypeToken<E>): AbilityContextKey.Builder<E> {
            this.type = type
            return this
        }

        override fun build(): AbilityContextKey<E> = SimpleAbilityContextKey(
            key = checkNotNull(this.key),
            allowedType = checkNotNull(this.type)
        )
    }
}