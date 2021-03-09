package com.expansemc.bending.plugin.ability;

import com.expansemc.bending.api.ability.AbilityCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class AbilityCategoryImpl implements AbilityCategory {

    private final Component name;
    private final @Nullable AbilityCategory parent;
    private final TextColor color;

    public AbilityCategoryImpl(final Component name, final @Nullable AbilityCategory parent, final TextColor color) {
        this.name = name;
        this.parent = parent;
        this.color = color;
    }

    @Override
    public Component name() {
        return this.name;
    }

    @Override
    public Optional<AbilityCategory> parent() {
        return Optional.ofNullable(this.parent);
    }

    @Override
    public TextColor color() {
        return this.color;
    }

    public static final class BuilderImpl implements AbilityCategory.Builder {

        private @Nullable Component name;
        private @Nullable AbilityCategory parent;
        private @Nullable TextColor color;

        @Override
        public Builder name(final Component name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        @Override
        public Builder parent(final AbilityCategory parent) {
            this.parent = Objects.requireNonNull(parent, "parent");
            return this;
        }

        @Override
        public Builder color(final TextColor color) {
            this.color = Objects.requireNonNull(color, "color");
            return this;
        }

        @Override
        public Builder reset() {
            this.name = null;
            this.parent = null;
            this.color = null;
            return this;
        }

        @Override
        public AbilityCategory build() {
            return new AbilityCategoryImpl(
                    Objects.requireNonNull(this.name, "name"),
                    this.parent,
                    Objects.requireNonNullElse(this.color, Objects.requireNonNullElse(this.name.color(), NamedTextColor.WHITE))
            );
        }
    }
}