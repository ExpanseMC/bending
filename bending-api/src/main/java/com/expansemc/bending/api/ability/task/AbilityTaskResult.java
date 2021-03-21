package com.expansemc.bending.api.ability.task;

import org.spongepowered.api.Sponge;

import java.util.Optional;

/**
 * The tri-value result type returned by {@link AbilityTaskExecutor}s in {@link AbilityTask}s.
 */
public interface AbilityTaskResult {

    /**
     * Ends the current task.
     *
     * @return The result.
     */
    static AbilityTaskResult end() {
        return Sponge.game().factoryProvider().provide(Factory.class).end();
    }

    /**
     * Ends the current task and schedules the specified {@link AbilityTask} next.
     *
     * @param next The next task.
     * @return The result.
     */
    static AbilityTaskResult next(final AbilityTask next) {
        return Sponge.game().factoryProvider().provide(Factory.class).next(next);
    }

    /**
     * Repeats the current task, if its a repeating task.
     *
     * @return The result.
     */
    static AbilityTaskResult repeat() {
        return Sponge.game().factoryProvider().provide(Factory.class).repeat();
    }

    /**
     * The next {@link AbilityTask} to schedule after the current one finishes.
     *
     * @return The next task, or empty if ability execution is stopping.
     */
    Optional<AbilityTask> next();

    /**
     * Whether this result signifies that the task should repeat.
     *
     * <p>This value is ignored when used on a non-repeating task.</p>
     *
     * @return True if repeating, false otherwise.
     */
    boolean isRepeat();

    /**
     * A factory interface for creating {@link AbilityTaskResult}s.
     */
    interface Factory {

        /**
         * @see AbilityTaskResult#end()
         */
        AbilityTaskResult end();

        /**
         * @see AbilityTaskResult#next(AbilityTask)
         */
        AbilityTaskResult next(AbilityTask next);

        /**
         * @see AbilityTaskResult#repeat()
         */
        AbilityTaskResult repeat();
    }
}