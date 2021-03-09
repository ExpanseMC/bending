package com.expansemc.bending.api.ability;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.util.ComponentMessageException;

import java.util.Objects;
import java.util.function.Supplier;

public class AbilityException extends ComponentMessageException {

    public AbilityException() {
        super();
    }

    public AbilityException(final Component message) {
        super(message);
    }

    public AbilityException(final Component message, final Throwable throwable) {
        super(message, throwable);
    }

    public AbilityException(final Throwable throwable) {
        super(throwable);
    }

    public final Component toComponent(final Supplier<? extends Component> defaultErrorMsg) {
        return Objects.requireNonNullElseGet(this.componentMessage(), defaultErrorMsg)
                .colorIfAbsent(NamedTextColor.RED);
    }
}