package com.expansemc.bending.plugin.ability

import com.expansemc.bending.api.ability.AbilityExecutionType
import org.bukkit.NamespacedKey

data class SimpleAbilityExecutionType(override val key: NamespacedKey) : AbilityExecutionType {

    class Builder: AbilityExecutionType.Builder {

        private var key: NamespacedKey? = null

        override fun key(key: NamespacedKey): AbilityExecutionType.Builder {
            this.key = key
            return this
        }

        override fun build(): AbilityExecutionType = SimpleAbilityExecutionType(
            key = checkNotNull(this.key)
        )
    }
}