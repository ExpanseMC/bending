package com.expansemc.bending.api.ability;

import com.expansemc.bending.api.Bending;
import com.expansemc.bending.api.registry.BendingRegistryTypes;
import org.spongepowered.api.registry.DefaultedRegistryReference;

public final class AbilityControls {

    public static final DefaultedRegistryReference<AbilityControl> FALL = control("fall");

    public static final DefaultedRegistryReference<AbilityControl> PRIMARY = control("primary");

    public static final DefaultedRegistryReference<AbilityControl> SECONDARY = control("secondary");

    public static final DefaultedRegistryReference<AbilityControl> SNEAK = control("sneak");

    private static DefaultedRegistryReference<AbilityControl> control(final String value) {
        return BendingRegistryTypes.ABILITY_CONTROL.defaultReferenced(Bending.key(value));
    }

    private AbilityControls() {
    }
}