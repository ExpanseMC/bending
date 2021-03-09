package com.expansemc.bending.plugin.ability.execution;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityControl;
import com.expansemc.bending.api.ability.execution.AbilityCause;
import org.spongepowered.api.event.Cause;

public final class AbilityCauseFactory implements AbilityCause.Factory {

    @Override
    public AbilityCause of(final Ability ability, final AbilityControl control, final Cause cause) {
        return new AbilityCauseImpl(ability, control, cause);
    }
}