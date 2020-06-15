package com.expansemc.bending.plugin.listener

import com.expansemc.bending.api.bender.Bender
import com.expansemc.bending.api.bender.BenderService
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class BenderListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player
        val bender: Bender = BenderService.instance.getOrCreateBender(player)


    }
}