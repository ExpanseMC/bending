package com.expansemc.bending.api.util;

import com.expansemc.bending.api.ability.Ability;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.api.data.value.MapValue;

public final class BendingTypeTokens {

    public static final TypeToken<Ability> ABILITY_TOKEN = new TypeToken<>() {};

    public static final TypeToken<MapValue<Integer, Ability>> INTEGER_ABILITY_MAPVALUE_TOKEN = new TypeToken<>() {};

    private BendingTypeTokens() {
    }
}