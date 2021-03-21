package com.expansemc.bending.api;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

public interface Bending {

    static Bending get() {
        return Sponge.game().factoryProvider().provide(Bending.class);
    }

    Logger logger();

    PluginContainer implementation();

    static ResourceKey key(final String value) {
        return ResourceKey.of(Bending.get().implementation(), value);
    }
}