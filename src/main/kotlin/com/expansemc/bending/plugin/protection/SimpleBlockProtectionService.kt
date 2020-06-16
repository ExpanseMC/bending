package com.expansemc.bending.plugin.protection

import com.expansemc.bending.api.protection.BlockProtection
import com.expansemc.bending.api.protection.BlockProtectionService
import com.expansemc.bending.api.registry.CatalogRegistry
import com.expansemc.bending.api.registry.getAllOf
import com.expansemc.bending.api.util.Tristate
import org.bukkit.Location
import org.bukkit.entity.Player

class SimpleBlockProtectionService : BlockProtectionService {

    override fun isProtected(source: Player, target: Location): Boolean {
        /*
         * TODO: This works to protect blocks now, but in case 2 or more protection plugins
         *  allow/deny affecting the block, will lead to undefined behavior.
         */
        for (protection: BlockProtection in CatalogRegistry.instance.getAllOf<BlockProtection>()) {
            val value: Tristate = protection.isProtected(source, target)

            // Return now if the protection plugin allows or denies affecting this block.
            if (value !== Tristate.UNDEFINED) {
                return value.toBoolean()
            }
        }

        return false
    }
}