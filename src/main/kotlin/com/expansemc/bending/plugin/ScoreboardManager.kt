package com.expansemc.bending.plugin

import com.expansemc.bending.api.ability.Ability
import com.expansemc.bending.api.bender.Bender
import com.expansemc.bending.api.bender.BenderService
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.scoreboard.*

class ScoreboardManager : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onSlotChange(event: PlayerItemHeldEvent) {
        val bender: Bender = BenderService.instance.getOrCreateBender(event.player)
        event.player.scoreboard = newScoreboard(bender, event.newSlot)
    }

    private fun newScoreboard(bender: Bender, curIndex: Int): Scoreboard {
        val scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard

        val objective: Objective = scoreboard.registerNewObjective("abilities", "dummy", "Abilities", RenderType.INTEGER)
        objective.displaySlot = DisplaySlot.SIDEBAR

        for ((index: Int, ability: Ability?) in bender.equipped.withIndex()) {
            var display: String = when {
                ability == null -> " ".repeat(index + 1)
                bender.hasCooldown(ability.type) -> "${ChatColor.BLACK}${ability.type.name}"
                else -> ability.type.element.color.toString() + ability.type.name
            }

            if (index == curIndex) {
                display = "*$display"
            }

            val score: Score = objective.getScore(display)
            score.score = 9 - index
        }

        return scoreboard
    }
}