package com.expansemc.bending.classic.util;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityControl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;

public final class AbilityUtil {

    private static final TextComponent COMMA_SEPARATOR = Component.text(", ");

    public static Component validControlsError(final Ability ability) {
        final List<Component> controls = new ArrayList<>();
        for (final AbilityControl abilityControl : ability.controls()) {
            controls.add(abilityControl.name());
        }
        return Component.text()
                .content("Valid ability controls: ")
                .append(Component.join(COMMA_SEPARATOR, controls))
                .build();
    }

    private AbilityUtil() {
    }
}