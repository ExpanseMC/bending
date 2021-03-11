package com.expansemc.bending.classic;

import com.expansemc.bending.api.Bending;
import com.expansemc.bending.api.registry.BendingRegistryTypes;
import com.expansemc.bending.classic.ability.air.AbilityAirBlast;
import com.expansemc.bending.classic.ability.air.AbilityAirBurst;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterRegistryValueEvent;
import org.spongepowered.plugin.jvm.Plugin;

@Plugin("bending-classic")
public final class BendingClassic {

    private final Logger logger;

    @Inject
    public BendingClassic(final Logger logger) {
        this.logger = logger;
    }

    @Listener
    public void onRegisterRegistryValue(final RegisterRegistryValueEvent event) {
        this.logger.info("Registering abilities...");

        event.registry(BendingRegistryTypes.ABILITY)
                .register(Bending.key("air_blast"), AbilityAirBlast.ABILITY)
                .register(Bending.key("air_burst"), AbilityAirBurst.ABILITY);
    }
}