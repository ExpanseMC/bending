package com.expansemc.bending.api.event;

import com.expansemc.bending.api.Bending;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.api.event.EventContextKey;

/**
 * Extra {@link EventContextKey}s used by the Bending API.
 */
public final class BendingEventContextKeys {

    public static final EventContextKey<Double> FALL_DISTANCE = key("fall_distance", new TypeToken<>() {});

    private static <T> EventContextKey<T> key(final String value, final TypeToken<T> type) {
        return EventContextKey.builder().type(type).key(Bending.key(value)).build();
    }

    private BendingEventContextKeys() {
    }
}