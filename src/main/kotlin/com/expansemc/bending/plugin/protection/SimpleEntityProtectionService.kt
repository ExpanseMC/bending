package com.expansemc.bending.plugin.protection

import com.expansemc.bending.api.protection.EntityProtection
import com.expansemc.bending.api.protection.EntityProtectionService
import com.expansemc.bending.api.registry.CatalogRegistry
import com.expansemc.bending.api.registry.getAllOf
import com.expansemc.bending.api.util.Tristate
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class SimpleEntityProtectionService : EntityProtectionService {

    override fun isProtected(source: Player, target: Entity): Boolean {
        /*
         * TODO: This works to protect entities now, but in case 2 or more protection plugins
         *  allow/deny affecting the entity, will lead to undefined behavior.
         */
        for (protection: EntityProtection in CatalogRegistry.instance.getAllOf<EntityProtection>()) {
            val value: Tristate = protection.isProtected(source, target)

            // Return now if the protection plugin allows or denies affecting this entity.
            if (value !== Tristate.UNDEFINED) {
                return value.toBoolean()
            }
        }

        return false
    }
}