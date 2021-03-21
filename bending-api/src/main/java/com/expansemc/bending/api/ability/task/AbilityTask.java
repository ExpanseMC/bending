package com.expansemc.bending.api.ability.task;

import com.expansemc.bending.api.ability.AbilityControl;
import com.expansemc.bending.api.ability.execution.AbilityContext;
import com.expansemc.bending.api.ability.execution.AbilityExecutor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import java.util.function.Supplier;

/**
 * An interface implementing structural concurrency around {@link Task}s.
 */
public interface AbilityTask extends AbilityExecutor {

    /**
     * Creates an {@link AbilityTask} that executes the supplied
     * {@link AbilityTaskExecutor} immediately, not wrapped into a
     * {@link Task}.
     *
     * @param executor The executor.
     * @return The new ability task.
     */
    static AbilityTask immediate(final Supplier<AbilityTaskExecutor> executor) {
        return Sponge.game().factoryProvider().provide(Factory.class).immediate(executor);
    }

    /**
     * Creates an {@link AbilityTask} that schedules the supplied
     * {@link AbilityTaskExecutor} to run on the next tick, using a
     * {@link Task}.
     *
     * @param executor The executor.
     * @return The new ability task.
     */
    static AbilityTask nextTick(final Supplier<AbilityTaskExecutor> executor) {
        return Sponge.game().factoryProvider().provide(Factory.class).nextTick(executor);
    }

    /**
     * Creates an {@link AbilityTask} that schedules the supplied
     * {@link AbilityTaskExecutor} to run after a certain {@link Ticks delay},
     * using a {@link Task}.
     *
     * @param delay The number of ticks to wait.
     * @param executor The executor.
     * @return The new ability task.
     */
    static AbilityTask delayed(final Ticks delay, final Supplier<AbilityTaskExecutor> executor) {
        return Sponge.game().factoryProvider().provide(Factory.class).delayed(delay, executor);
    }

    /**
     * Creates an {@link AbilityTask} that schedules the supplied
     * {@link AbilityTaskExecutor} to run every certain {@link Ticks interval},
     * using a {@link Task}.
     *
     * <p>Use {@link AbilityTaskResult#repeat()} to continue repetition of the task.</p>
     *
     * @param interval The number of ticks between executions.
     * @param executor The executor.
     * @return The new ability task.
     */
    static AbilityTask repeating(final Ticks interval, final Supplier<AbilityTaskExecutor> executor) {
        return Sponge.game().factoryProvider().provide(Factory.class).repeating(interval, executor);
    }

    /**
     * Creates an {@link AbilityTask} that schedules the supplied
     * {@link AbilityTaskExecutor} to run every certain {@link Ticks interval},
     * using a {@link Task}, or until the provided {@link AbilityControl} is
     * executed for this ability, which causes the provided
     * {@link AbilityExecutor} to be called.
     *
     * <p>Use {@link AbilityTaskResult#repeat()} to continue repetition of the task.</p>
     *
     * @param control The control to wait for.
     * @param onControl The callback to call when the control is executed.
     * @param interval The number of ticks between executions.
     * @param executor The executor.
     * @return The new ability task.
     */
    static AbilityTask repeatingUntil(final AbilityControl control, final AbilityExecutor onControl,
                                      final Ticks interval, final Supplier<AbilityTaskExecutor> executor) {
        return Sponge.game().factoryProvider().provide(Factory.class).repeatingUntil(control, onControl, interval, executor);
    }

    @Override
    void execute(AbilityContext context);

    /**
     * A factory interface for creating {@link AbilityTask}s.
     */
    interface Factory {

        /**
         * @see AbilityTask#immediate(Supplier)
         */
        AbilityTask immediate(Supplier<AbilityTaskExecutor> executor);

        /**
         * @see AbilityTask#nextTick(Supplier)
         */
        AbilityTask nextTick(Supplier<AbilityTaskExecutor> executor);

        /**
         * @see AbilityTask#delayed(Ticks, Supplier)
         */
        AbilityTask delayed(Ticks delay, Supplier<AbilityTaskExecutor> executor);

        /**
         * @see AbilityTask#repeating(Ticks, Supplier)
         */
        AbilityTask repeating(Ticks interval, Supplier<AbilityTaskExecutor> executor);

        /**
         * @see AbilityTask#repeatingUntil(AbilityControl, AbilityExecutor, Ticks, Supplier)
         */
        AbilityTask repeatingUntil(AbilityControl control, AbilityExecutor onControl,
                                   Ticks interval, Supplier<AbilityTaskExecutor> executor);
    }
}