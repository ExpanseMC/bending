package com.expansemc.bending.plugin.protection

import com.expansemc.bending.api.protection.BlockProtection
import com.expansemc.bending.api.util.NamespacedKeys.bending
import com.expansemc.bending.api.util.Tristate
import com.expansemc.bending.api.util.toTristate
import com.palmergames.bukkit.towny.TownyAPI
import com.palmergames.bukkit.towny.`object`.TownBlock
import com.palmergames.bukkit.towny.`object`.TownyPermission
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.util.Location
import com.sk89q.worldedit.world.World
import com.sk89q.worldguard.LocalPlayer
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.regions.RegionContainer
import com.sk89q.worldguard.protection.regions.RegionQuery

object BlockProtections {

    val WORLDGUARD: BlockProtection = BlockProtection.builder()
        .key(bending("worldguard"))
        .protected { player, location ->
            val playerWG: LocalPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
            val worldWE: World = BukkitAdapter.adapt(player.world)

            if (WorldGuard.getInstance().platform.sessionManager.hasBypass(playerWG, worldWE)) {
                // Player bypasses protections in this world.
                return@protected Tristate.FALSE
            }

            val container: RegionContainer = WorldGuard.getInstance().platform.regionContainer
            val query: RegionQuery = container.createQuery()

            val locationWE: Location = BukkitAdapter.adapt(location)

            when (query.queryState(locationWE, playerWG, Flags.BUILD)) {
                null -> Tristate.UNDEFINED
                StateFlag.State.ALLOW -> Tristate.FALSE
                StateFlag.State.DENY -> Tristate.TRUE
            }
        }
        .build()

    val TOWNY: BlockProtection = BlockProtection.builder()
        .key(bending("towny"))
        .protected { player, location ->
            if (TownyAPI.getInstance().getTownBlock(location) == null) {
                // Not in a town block, so don't do permission checks.
                return@protected Tristate.UNDEFINED
            }

            val canBuild: Tristate = PlayerCacheUtil
                .getCachePermission(player, location, location.block.type, TownyPermission.ActionType.BUILD)
                .toTristate()
            val canDestroy: Tristate = PlayerCacheUtil
                .getCachePermission(player, location, location.block.type, TownyPermission.ActionType.BUILD)
                .toTristate()

            // If they can't build and destroy, the block is protected
            !(canBuild and canDestroy)
        }
        .build()
}