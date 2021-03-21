package com.expansemc.bending.api.ability;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.DefaultedRegistryValue;

import java.util.Optional;

public interface AbilityCategory extends DefaultedRegistryValue {

    static AbilityCategory of(final Component name) {
        return builder().name(name).build();
    }

    static AbilityCategory of(final Component name, final AbilityCategory parent) {
        return builder().name(name).parent(parent).build();
    }

    static Builder builder() {
        return Sponge.game().builderProvider().provide(Builder.class);
    }

    Component name();

    Optional<AbilityCategory> parent();

    TextColor color();

    interface Builder extends org.spongepowered.api.util.Builder<AbilityCategory, Builder> {

        Builder name(Component name);

        Builder parent(AbilityCategory parent);

        Builder color(TextColor color);
    }
}