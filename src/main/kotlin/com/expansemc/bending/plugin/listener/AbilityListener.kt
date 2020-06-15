package com.expansemc.bending.plugin.listener

import com.expansemc.bending.api.ability.Ability
import com.expansemc.bending.api.ability.AbilityExecutionTypes.LEFT_CLICK
import com.expansemc.bending.api.ability.AbilityExecutionTypes.SNEAK
import com.expansemc.bending.api.ability.AbilityExecutionTypes.SPRINT_OFF
import com.expansemc.bending.api.ability.AbilityExecutionTypes.SPRINT_ON
import com.expansemc.bending.api.ability.AbilityType
import com.expansemc.bending.api.bender.Bender
import com.expansemc.bending.api.bender.BenderService
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.player.PlayerToggleSprintEvent
import org.bukkit.inventory.EquipmentSlot

/**
 * Handles the executing of abilities given ability execution types.
 */
class AbilityListener : Listener {

    /**
     * Handles displaying the currently selected ability on the action bar.
     */
    @EventHandler(ignoreCancelled = true)
    fun onSlotChange(event: PlayerItemHeldEvent) {
        val player = event.player

        val bender: Bender = BenderService.instance.getOrCreateBender(player)
        val type: AbilityType = bender.equipped[event.newSlot]?.type ?: return // Return early if no ability is selected
        val onCooldown: Boolean = bender.hasCooldown(type)

        val message = TextComponent(type.name).also {
            it.color = type.element.color.asBungee()
        }

        if (onCooldown) {
            message.isStrikethrough = true
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message)
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message)
        }
    }

    /**
     * Handles ability execution for left clicks.
     */
    @EventHandler
    fun onLeftClick(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) {
            // Only checking for main hand.
            return
        }
        if (event.action != Action.LEFT_CLICK_AIR && event.action != Action.LEFT_CLICK_BLOCK) {
            // Only checking for left clicks.
            return
        }
        if (event.action == Action.LEFT_CLICK_BLOCK && event.isCancelled) {
            return
        }

        val bender: Bender = BenderService.instance.getOrCreateBender(event.player)
        val ability: Ability = bender.selected ?: return // Return early if no ability is selected

        bender.execute(ability, LEFT_CLICK)
    }

    /**
     * Handles ability execution for sneaking.
     */
    @EventHandler(ignoreCancelled = true)
    fun onSneak(event: PlayerToggleSneakEvent) {
        if (!event.isSneaking) {
            // Player stopped sneaking.
            return
        }

        val bender: Bender = BenderService.instance.getOrCreateBender(event.player)
        val ability: Ability = bender.selected ?: return // Return early if no ability is selected

        bender.execute(ability, SNEAK)
    }

    /**
     * Handles ability execution for sprinting.
     */
    @EventHandler(ignoreCancelled = true)
    fun onSprint(event: PlayerToggleSprintEvent) {
        val bender: Bender = BenderService.instance.getOrCreateBender(event.player)
        val ability: Ability = bender.selected ?: return // Return early if no ability is selected

        bender.execute(ability, if (event.isSprinting) SPRINT_ON else SPRINT_OFF)
    }
}