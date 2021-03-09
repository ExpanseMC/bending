package com.expansemc.bending.plugin.ability;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityCategory;
import com.expansemc.bending.api.ability.AbilityControl;
import com.expansemc.bending.api.ability.execution.AbilityExecutor;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Supplier;

public final class AbilityImpl implements Ability {

    private final Component name;
    private final Collection<AbilityControl> controls;
    private final AbilityCategory category;
    private final AbilityExecutor executor;

    public AbilityImpl(final Component name, final Collection<AbilityControl> controls,
                       final AbilityCategory category, final AbilityExecutor executor) {
        this.name = name;
        this.controls = controls;
        this.category = category;
        this.executor = executor;
    }

    @Override
    public Component name() {
        return this.name;
    }

    @Override
    public Collection<AbilityControl> controls() {
        return this.controls;
    }

    @Override
    public AbilityCategory category() {
        return this.category;
    }

    @Override
    public AbilityExecutor executor() {
        return this.executor;
    }

    public static final class BuilderImpl implements Ability.Builder {

        private @Nullable Component name;
        private final Set<AbilityControl> controls = new HashSet<>();
        private @Nullable AbilityCategory category;
        private AbilityExecutor executor = AbilityExecutor.EMPTY;

        @Override
        public Builder name(final Component name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        @Override
        public Builder addControls(final AbilityControl... controls) {
            Collections.addAll(this.controls, controls);
            return this;
        }

        @Override
        public Builder addControls(final Iterable<AbilityControl> controls) {
            for (final AbilityControl control : controls) {
                this.controls.add(control);
            }
            return this;
        }

        @SafeVarargs
        @Override
        public final Builder addControls(final Supplier<AbilityControl>... controls) {
            for (final Supplier<AbilityControl> control : controls) {
                this.controls.add(control.get());
            }
            return this;
        }

        @Override
        public Builder category(final AbilityCategory category) {
            this.category = Objects.requireNonNull(category, "category");
            return this;
        }

        @Override
        public Builder executor(final AbilityExecutor executor) {
            this.executor = Objects.requireNonNull(executor, "executor");
            return this;
        }

        @Override
        public Builder reset() {
            this.name = null;
            this.category = null;
            this.executor = AbilityExecutor.EMPTY;
            return this;
        }

        @Override
        public Ability build() {
            final AbilityCategory category = Objects.requireNonNull(this.category, "category");
            return new AbilityImpl(
                    Objects.requireNonNull(this.name, "name").colorIfAbsent(category.color()),
                    this.controls,
                    category,
                    this.executor
            );
        }
    }
}