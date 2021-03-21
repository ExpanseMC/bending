package com.expansemc.bending.api.registry;

import com.expansemc.bending.api.Bending;
import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityCategory;
import com.expansemc.bending.api.ability.AbilityControl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.DefaultedRegistryType;
import org.spongepowered.api.registry.RegistryRoots;
import org.spongepowered.api.registry.RegistryType;

/**
 * Extra {@link RegistryType}s used by the Bending API.
 */
public final class BendingRegistryTypes {

    public static final DefaultedRegistryType<Ability> ABILITY = registryType("ability");

    public static final DefaultedRegistryType<AbilityCategory> ABILITY_CATEGORY = registryType("ability_category");

    public static final DefaultedRegistryType<AbilityControl> ABILITY_CONTROL = registryType("ability_control");

    private static <T>DefaultedRegistryType<T> registryType(final String value) {
        return RegistryType.of(RegistryRoots.SPONGE, Bending.key(value))
                .asDefaultedType(() -> Sponge.game().registries());
    }

    private BendingRegistryTypes() {
    }
}