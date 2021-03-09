package com.expansemc.bending.api.ability.task;

import com.expansemc.bending.api.ability.AbilityException;
import com.expansemc.bending.api.ability.execution.AbilityCause;

@FunctionalInterface
public interface AbilityTaskExecutor {

    AbilityTaskResult execute(AbilityCause cause) throws AbilityException;
}