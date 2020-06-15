package com.expansemc.bending.plugin.element

import com.expansemc.bending.api.element.Element
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey

data class SimpleElement(
    override val key: NamespacedKey,
    override val name: String,
    override val color: ChatColor
) : Element {

    class Builder : Element.Builder {

        private var key: NamespacedKey? = null
        private var name: String? = null
        private var color: ChatColor? = null

        override fun key(key: NamespacedKey): Element.Builder {
            this.key = key
            return this
        }

        override fun name(name: String): Element.Builder {
            this.name = name
            return this
        }

        override fun color(color: ChatColor): Element.Builder {
            this.color = color
            return this
        }

        override fun build(): Element = SimpleElement(
            key = checkNotNull(this.key),
            name = checkNotNull(this.name),
            color = checkNotNull(this.color)
        )
    }
}