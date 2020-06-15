package com.expansemc.bending.plugin.util

import com.expansemc.bending.api.util.joinToComponent
import com.expansemc.bending.api.util.toText
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent

object Texts {

    val NEWLINE: TextComponent = TextComponent("\n")

    val HEADER: BaseComponent = arrayOf(
        "-- ".toText(color = ChatColor.DARK_GRAY),
        "Bending ".toText(color = ChatColor.GOLD),
        "------------------------------------------".toText(color = ChatColor.DARK_GRAY)
    ).joinToComponent()

    val FOOTER: BaseComponent = "----------------------------------------------------".toText(color = ChatColor.DARK_GRAY)

    val HELP_COMMAND: BaseComponent = arrayOf(
        commandDesc("/bending bind <ability>", "binds the given ability to the hotbar."),
        commandDesc("/bending clear", "removes all binded abilities from the hotbar."),
        commandDesc("/bending display [<player>]", "displays your currently binded abilities."),
        commandDesc("/bending elements", "lists all available elements."),
        commandDesc("/bending list <element>", "lists all available abilities.")
    ).joinToComponent(prefix = HEADER, separator = NEWLINE, postfix = FOOTER)

    private fun commandDesc(usage: String, description: String): BaseComponent {
        return arrayOf(
            "- ".toText(ChatColor.GRAY),
            "$usage ".toText(ChatColor.YELLOW),
            description.toText(ChatColor.GRAY)
        ).joinToComponent()
    }
}