package com.expansemc.bending.classic;

import com.expansemc.bending.api.registry.BendingRegistryTypes;
import com.expansemc.bending.classic.ability.air.AirBlastAbility;
import com.expansemc.bending.classic.ability.air.AirBurstAbility;
import com.expansemc.bending.classic.ability.air.AirSwipeAbility;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterRegistryValueEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

@Plugin("bending-classic")
public final class BendingClassic {

    private final Logger logger;
    private final PluginContainer container;

    @Inject
    public BendingClassic(final Logger logger, final PluginContainer container) {
        this.logger = logger;
        this.container = container;
    }

    @Listener
    public void onRegisterRegistryValue(final RegisterRegistryValueEvent event) {
        this.logger.info("Registering abilities...");

        event.registry(BendingRegistryTypes.ABILITY)
                .register(ResourceKey.of(this.container, "air_blast"), AirBlastAbility.ABILITY)
                .register(ResourceKey.of(this.container, "air_burst"), AirBurstAbility.ABILITY)
                .register(ResourceKey.of(this.container, "air_swipe"), AirSwipeAbility.ABILITY);
    }
}