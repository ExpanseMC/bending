package com.expansemc.bending.api.ability;

import com.expansemc.bending.api.ability.execution.AbilityExecutor;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.DefaultedRegistryValue;

import java.util.Collection;
import java.util.function.Supplier;

public interface Ability extends DefaultedRegistryValue {

    static Builder builder() {
        return Sponge.getGame().getBuilderProvider().provide(Builder.class);
    }

    Component name();

    Collection<AbilityControl> controls();

    AbilityCategory category();

    AbilityExecutor executor();

    interface Builder extends org.spongepowered.api.util.Builder<Ability, Builder> {

        Builder name(Component name);

        Builder addControls(AbilityControl... controls);

        Builder addControls(Iterable<AbilityControl> controls);

        @SuppressWarnings("unchecked")
        Builder addControls(Supplier<AbilityControl>... controls);

        Builder category(AbilityCategory category);

        default Builder category(final Supplier<AbilityCategory> category) {
            return this.category(category.get());
        }

        Builder executor(AbilityExecutor executor);
    }
}