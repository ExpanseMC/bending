package com.expansemc.bending.api.ability.execution;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityException;
import org.spongepowered.api.scheduler.Task;

/**
 * Interface containing the method directing how a certain ability will be executed.
 */
@FunctionalInterface
public interface AbilityExecutor {

     /**
      * The empty executor. Nothing runs when used.
      */
     AbilityExecutor EMPTY = context -> {};

     /**
      * Callback for the execution of an {@link Ability}.
      *
      * <p>Long running abilities should use {@link AbilityContext#setTask(Task)}.</p>
      *
      * @param context The ability context.
      * @throws AbilityException If an error occurs during ability execution.
      */
     void execute(AbilityContext context) throws AbilityException;
}