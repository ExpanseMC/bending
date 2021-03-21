package com.expansemc.bending.api.ability;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.DefaultedRegistryValue;

public interface AbilityControl extends DefaultedRegistryValue {

    static AbilityControl of(final Component name) {
        return builder().name(name).build();
    }

    static Builder builder() {
        return Sponge.game().builderProvider().provide(Builder.class);
    }

    Component name();

    interface Builder extends org.spongepowered.api.util.Builder<AbilityControl, Builder> {

        Builder name(Component name);
    }
}