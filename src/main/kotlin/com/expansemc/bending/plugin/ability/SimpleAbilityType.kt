package com.expansemc.bending.plugin.ability

import com.expansemc.bending.api.ability.Ability
import com.expansemc.bending.api.ability.AbilityExecutionType
import com.expansemc.bending.api.ability.AbilityType
import com.expansemc.bending.api.element.Element
import com.expansemc.bending.api.util.toText
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import ninja.leaping.configurate.ConfigurationNode
import org.bukkit.NamespacedKey

data class SimpleAbilityType(
    override val key: NamespacedKey,
    override val name: String,
    override val element: Element,
    override val executionTypes: Set<AbilityExecutionType>,
    override val instructions: String?,
    override val description: String?,
    private val loader: (ConfigurationNode) -> Ability?
) : AbilityType {

    override val instructionsComponent: BaseComponent? = this.instructions?.toText(color = ChatColor.GOLD)

    override val descriptionComponent: BaseComponent? = this.description?.toText(color = this.element.color.asBungee())

    private val richDisplay: TextComponent = this.name.toText(
        color = this.element.color.asBungee(),
        hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            arrayOf(
                descriptionComponent ?: "<no description>".toText(ChatColor.DARK_RED),
                "\n\nInstructions:\n".toText(ChatColor.WHITE),
                instructionsComponent ?: "<no instructions>".toText(ChatColor.DARK_RED)
            )
        )
    )

    override fun show(): TextComponent = richDisplay

    override fun load(node: ConfigurationNode): Ability? = this.loader(node)

    class Builder : AbilityType.Builder {

        private var key: NamespacedKey? = null
        private var name: String? = null
        private var element: Element? = null
        private var executionTypes: Set<AbilityExecutionType>? = null
        private var instructions: String? = null
        private var description: String? = null
        private var loader: ((ConfigurationNode) -> Ability?)? = null

        override fun key(key: NamespacedKey): AbilityType.Builder {
            this.key = key
            return this
        }

        override fun name(name: String): AbilityType.Builder {
            this.name = name
            return this
        }

        override fun element(element: Element): AbilityType.Builder {
            this.element = element
            return this
        }

        override fun executionTypes(executionTypes: Collection<AbilityExecutionType>): AbilityType.Builder {
            this.executionTypes = executionTypes.toSet()
            return this
        }

        override fun executionTypes(vararg executionTypes: AbilityExecutionType): AbilityType.Builder {
            this.executionTypes = executionTypes.toSet()
            return this
        }

        override fun instructions(instructions: String): AbilityType.Builder {
            this.instructions = instructions
            return this
        }

        override fun description(description: String): AbilityType.Builder {
            this.description = description
            return this
        }

        override fun loader(loader: (ConfigurationNode) -> Ability?): AbilityType.Builder {
            this.loader = loader
            return this
        }

        override fun build(): AbilityType = SimpleAbilityType(
            key = checkNotNull(this.key),
            name = checkNotNull(this.name),
            element = checkNotNull(this.element),
            executionTypes = checkNotNull(this.executionTypes),
            instructions = this.instructions,
            description = this.description,
            loader = checkNotNull(this.loader)
        )
    }
}