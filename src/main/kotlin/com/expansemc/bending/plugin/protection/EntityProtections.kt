package com.expansemc.bending.plugin.protection

import com.expansemc.bending.api.protection.EntityProtection
import com.expansemc.bending.api.util.NamespacedKeys.bending
import com.expansemc.bending.api.util.Tristate
import org.bukkit.Bukkit
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object EntityProtections {

    val GENERIC: EntityProtection = EntityProtection.builder()
        .key(bending("generic"))
        .protected { player, entity ->
            val damageEvent = EntityDamageByEntityEvent(
                player,
                entity,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                0.0
            )
            Bukkit.getPluginManager().callEvent(damageEvent)

            if (damageEvent.isCancelled) {
                Tristate.TRUE
            } else {
                Tristate.UNDEFINED
            }
        }
        .build()
}