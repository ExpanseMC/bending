package com.expansemc.bending.plugin.command

import com.expansemc.bending.api.ability.Ability
import com.expansemc.bending.api.ability.AbilityExecutionTypes
import com.expansemc.bending.api.ability.AbilityType
import com.expansemc.bending.api.bender.Bender
import com.expansemc.bending.api.bender.BenderService
import com.expansemc.bending.api.config.AbilityConfig
import com.expansemc.bending.api.element.Element
import com.expansemc.bending.api.registry.CatalogRegistry
import com.expansemc.bending.api.registry.get
import com.expansemc.bending.api.registry.getAllOf
import com.expansemc.bending.api.registry.getByName
import com.expansemc.bending.api.util.NamespacedKeys
import com.expansemc.bending.api.util.joinToComponent
import com.expansemc.bending.api.util.toText
import com.expansemc.bending.plugin.util.Texts
import net.md_5.bungee.api.ChatColor.DARK_RED
import net.md_5.bungee.api.ChatColor.WHITE
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BendingCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when (args.getOrNull(0)) {
            null, "help", "?" -> commandHelp(sender)
            "bind", "b" -> {
                if (sender !is Player) {
                    sender.sendMessage("${RED}You must be a player to use this command.")
                    return true
                }

                val abilityTypeArg: String? = args.getOrNull(1)

                if (abilityTypeArg == null) {
                    sender.sendMessage("${RED}Usage: /bending bind <ability> [<config>]")
                    return true
                }

                val abilityTypeKey: NamespacedKey = NamespacedKeys.of(abilityTypeArg)
                val abilityType: AbilityType? = CatalogRegistry.instance[abilityTypeKey]
                    ?: CatalogRegistry.instance.getByName<AbilityType>(abilityTypeArg, ignoreCase = true).singleOrNull()

                if (abilityType == null) {
                    sender.sendMessage("${RED}Unknown ability: $abilityTypeKey")
                    return true
                }

                val abilityConfigArg: String = args.getOrNull(2) ?: "default"
                val abilityConfigKey: NamespacedKey = NamespacedKeys.of(abilityConfigArg)
                val abilityConfig: AbilityConfig? = CatalogRegistry.instance[abilityConfigKey]

                if (abilityConfig == null) {
                    sender.sendMessage("${RED}Unknown ability config: $abilityConfigKey")
                    return true
                }

                val ability: Ability? = abilityConfig.load(abilityType)

                if (ability == null) {
                    sender.sendMessage("${DARK_RED}Failed to load ability ${abilityType.name} for config ${abilityConfig.key}")
                    return true
                }

                val bender: Bender = BenderService.instance.getOrCreateBender(sender)

                if (AbilityExecutionTypes.PASSIVE in abilityType.executionTypes) {
                    bender.addPassive(ability)
                    sender.sendMessage("${GREEN}Binded ${abilityType.element.color}${abilityType.name} ${GREEN}as a passive.")
                }

                bender.selected = ability
                sender.sendMessage("${GREEN}Binded ${ability.type.element.color}${ability.type.name} ${GREEN}to the currently selected hotbar slot.")
            }
            "clear", "c" -> {
                val bender: Bender = parseOptionalPlayerArgument(
                    sender, args.getOrNull(1),
                    "/bending clear <player>", "bending.display.other"
                ) ?: return true

                commandClear(sender, bender)
            }
            "display", "d" -> {
                val bender: Bender = parseOptionalPlayerArgument(
                    sender, args.getOrNull(1),
                    "/bending display <player>", "bending.display.other"
                ) ?: return true

                commandDisplay(sender, bender)
            }
            "elements", "e" -> {
                commandElements(sender)
            }
            "list", "l" -> {
                val elementArg: String? = args.getOrNull(1)

                if (elementArg == null) {
                    sender.sendMessage("${RED}Usage: /bending list <element>")
                    return true
                }

                val elementKey: NamespacedKey = NamespacedKeys.of(elementArg)
                val element: Element? = CatalogRegistry.instance[elementKey]

                if (element == null) {
                    sender.sendMessage("${RED}Unknown element: $elementKey")
                    return true
                }

                val abilities: BaseComponent = CatalogRegistry.instance.getAllOf<AbilityType>().asSequence()
                    .filter { it.element === element }
                    .sortedBy { it.name }
                    .map { it.show() }
                    .joinToComponent(prefix = Texts.HEADER, separator = Texts.NEWLINE, postfix = Texts.FOOTER)

                sender.spigot().sendMessage(abilities)
            }
            else -> {
                sender.sendMessage("${RED}Unknown subcommand. Type $WHITE/bending help $RED for help.")
            }
        }

        return true
    }

    private fun commandHelp(sender: CommandSender) {
        sender.spigot().sendMessage(Texts.HELP_COMMAND)
    }

    private fun commandClear(sender: CommandSender, bender: Bender) {
        bender.equipped.clear()
        sender.sendMessage("${GREEN}Cleared ${bender.player?.name}'s ability hotbar.")
    }

    private fun commandDisplay(sender: CommandSender, bender: Bender) {
        val components: List<TextComponent> = bender.equipped
            .map { it?.type?.show() ?: "---".toText(color = DARK_RED) }

        val message: BaseComponent = components.joinToComponent(prefix = Texts.HEADER, separator = Texts.NEWLINE, postfix = Texts.FOOTER)

        sender.spigot().sendMessage(message)
    }

    private fun commandElements(sender: CommandSender) {
        val components: List<TextComponent> = CatalogRegistry.instance.getAllOf<Element>()
            .map { it.name.toText(color = it.color.asBungee()) }

        val message: BaseComponent = components.joinToComponent(prefix = Texts.HEADER, separator = Texts.NEWLINE, postfix = Texts.FOOTER)

        sender.spigot().sendMessage(message)
    }

    private fun parseOptionalPlayerArgument(sender: CommandSender, arg: String?, consoleUsage: String, argPermission: String): Bender? =
        when (val player: Player? = arg?.let(Bukkit::getPlayer)) {
            null -> {
                if (sender !is Player) {
                    sender.sendMessage("${RED}Usage: $consoleUsage")
                    null
                } else {
                    BenderService.instance.getOrCreateBender(sender)
                }
            }
            else -> {
                if (!sender.hasPermission(argPermission)) {
                    sender.sendMessage("${RED}You don't have permission to use the <player> argument.")
                    null
                } else {
                    BenderService.instance.getOrCreateBender(player)
                }
            }
        }


    private fun String.toNamespacedKey(defaultNamespace: String = "bending"): NamespacedKey {
        if (':' !in this) {
            @Suppress("DEPRECATION")
            return NamespacedKey(defaultNamespace, this)
        }

        val (namespace: String, key: String) = this.split(':')

        @Suppress("DEPRECATION")
        return NamespacedKey(namespace, key)
    }
}