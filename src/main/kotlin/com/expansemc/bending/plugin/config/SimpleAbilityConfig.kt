package com.expansemc.bending.plugin.config

import com.expansemc.bending.api.ability.AbilityType
import com.expansemc.bending.api.config.AbilityConfig
import com.expansemc.bending.plugin.Bending
import ninja.leaping.configurate.ConfigurationNode
import org.bukkit.NamespacedKey
import java.util.logging.Level

data class SimpleAbilityConfig(
    override val key: NamespacedKey,
    private val provider: (AbilityType) -> ConfigurationNode?
) : AbilityConfig {

    override fun provide(type: AbilityType): ConfigurationNode? = try {
        this.provider.invoke(type)
    } catch (e: Exception) {
        Bending.PLUGIN.logger.log(Level.SEVERE, "An exception occurred while loading ability ${type.key} for config $key", e)
        null
    }

    class Builder : AbilityConfig.Builder {

        private var key: NamespacedKey? = null
        private var provider: ((AbilityType) -> ConfigurationNode?)? = null

        override fun key(key: NamespacedKey): AbilityConfig.Builder {
            this.key = key
            return this
        }

        override fun provider(provider: (AbilityType) -> ConfigurationNode?): AbilityConfig.Builder {
            this.provider = provider
            return this
        }

        override fun build(): AbilityConfig = SimpleAbilityConfig(
            key = checkNotNull(this.key),
            provider = checkNotNull(this.provider)
        )
    }
}