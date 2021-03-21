package com.expansemc.bending.classic;

import com.expansemc.bending.api.registry.BendingRegistryTypes;
import com.expansemc.bending.classic.ability.air.AirAgilityAbility;
import com.expansemc.bending.classic.ability.air.AirBlastAbility;
import com.expansemc.bending.classic.ability.air.AirBurstAbility;
import com.expansemc.bending.classic.ability.air.AirSwipeAbility;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterRegistryValueEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

@Plugin("bending-classic")
public final class BendingClassic {

    private final Logger logger;
    private final PluginContainer container;

    private @Nullable ScheduledTask agilityTask = null;

    @Inject
    public BendingClassic(final Logger logger, final PluginContainer container) {
        this.logger = logger;
        this.container = container;
    }

    @Listener
    public void onRegisterRegistryValue(final RegisterRegistryValueEvent event) {
        this.logger.info("Registering abilities...");

        event.registry(BendingRegistryTypes.ABILITY)
                .register(ResourceKey.of(this.container, "air_agility"), AirAgilityAbility.ABILITY)
                .register(ResourceKey.of(this.container, "air_blast"), AirBlastAbility.ABILITY)
                .register(ResourceKey.of(this.container, "air_burst"), AirBurstAbility.ABILITY)
                .register(ResourceKey.of(this.container, "air_swipe"), AirSwipeAbility.ABILITY);
    }

    @Listener
    public void onStartedServer(final StartedEngineEvent<Server> event) {
        this.logger.info("Starting passive tasks...");

        this.agilityTask = event.engine().scheduler().submit(AirAgilityAbility.TASK);
    }

    @Listener
    public void onStoppingServer(final StoppingEngineEvent<Server> event) {
        this.logger.info("Stopping passive tasks...");

        if (this.agilityTask != null) {
            this.agilityTask.cancel();
            this.agilityTask = null;
        }
    }
}