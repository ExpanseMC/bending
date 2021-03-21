package com.expansemc.bending.plugin.ability;

import com.expansemc.bending.api.ability.AbilityControl;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public class AbilityControlImpl implements AbilityControl {

    private final Component name;

    public AbilityControlImpl(final Component name) {
        this.name = name;
    }

    @Override
    public Component name() {
        return this.name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }

        final AbilityControlImpl that = (AbilityControlImpl) o;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    public static final class BuilderImpl implements AbilityControl.Builder {

        private @Nullable Component name;

        @Override
        public Builder name(final Component name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        @Override
        public Builder reset() {
            this.name = null;
            return this;
        }

        @Override
        public AbilityControl build() {
            return new AbilityControlImpl(
                    Objects.requireNonNull(this.name, "name")
            );
        }
    }
}