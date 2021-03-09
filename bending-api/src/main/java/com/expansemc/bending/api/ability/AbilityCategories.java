package com.expansemc.bending.api.ability;

import com.expansemc.bending.api.Bending;
import com.expansemc.bending.api.registry.BendingRegistryTypes;
import org.spongepowered.api.registry.DefaultedRegistryReference;

public final class AbilityCategories {

    public static final DefaultedRegistryReference<AbilityCategory> AIR = category("air");

    public static final DefaultedRegistryReference<AbilityCategory> EARTH = category("earth");

    public static final DefaultedRegistryReference<AbilityCategory> FIRE = category("fire");

    public static final DefaultedRegistryReference<AbilityCategory> WATER = category("water");

    private static DefaultedRegistryReference<AbilityCategory> category(final String value) {
        return BendingRegistryTypes.ABILITY_CATEGORY.defaultReferenced(Bending.key(value));
    }

    private AbilityCategories() {
    }
}