package com.expansemc.bending.api.data;

import com.expansemc.bending.api.Bending;
import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.util.BendingTypeTokens;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.MapValue;
import org.spongepowered.api.data.value.SetValue;

public final class BendingKeys {

    public static final Key<MapValue<Integer, Ability>> ABILITY_HOTBAR =
            Key.of(Bending.key("ability_hotbar"), BendingTypeTokens.INTEGER_ABILITY_MAP_VALUE_TOKEN);

    public static final Key<SetValue<Ability>> PASSIVE_ABILITIES =
            Key.of(Bending.key("passive_abilities"), BendingTypeTokens.ABILITY_SET_VALUE_TOKEN);

    private BendingKeys() {
    }
}