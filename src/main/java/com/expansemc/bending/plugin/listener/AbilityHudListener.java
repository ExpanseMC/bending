package com.expansemc.bending.plugin.listener;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.data.BendingKeys;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;

import java.util.Map;

public final class AbilityHudListener {

    @Listener
    public void onChangeHeld(final ChangeInventoryEvent.Held event,
                             @First final ServerPlayer player) {
        final int oldIndex = event.getOriginalSlot().require(Keys.SLOT_INDEX);
        final int newIndex = event.getFinalSlot().require(Keys.SLOT_INDEX);

        if (oldIndex != newIndex) {
            final @Nullable Ability ability = player.getOrElse(BendingKeys.ABILITY_HOTBAR, Map.of())
                    .get(newIndex);

            if (ability != null) {
                player.sendActionBar(ability.name());
            }
        }
    }
}