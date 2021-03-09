package com.expansemc.bending.api.ability.task;

import com.expansemc.bending.api.ability.AbilityControl;
import com.expansemc.bending.api.ability.execution.AbilityContext;
import com.expansemc.bending.api.ability.execution.AbilityExecutor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.Ticks;

import java.util.function.Supplier;

public interface AbilityTask extends AbilityExecutor {

    static AbilityTask immediate(final Supplier<AbilityTaskExecutor> executor) {
        return Sponge.getGame().getFactoryProvider().provide(Factory.class).immediate(executor);
    }

    static AbilityTask nextTick(final Supplier<AbilityTaskExecutor> executor) {
        return Sponge.getGame().getFactoryProvider().provide(Factory.class).nextTick(executor);
    }

    static AbilityTask delayed(final Ticks delay, final Supplier<AbilityTaskExecutor> executor) {
        return Sponge.getGame().getFactoryProvider().provide(Factory.class).delayed(delay, executor);
    }

    static AbilityTask repeating(final Ticks interval, final Supplier<AbilityTaskExecutor> executor) {
        return Sponge.getGame().getFactoryProvider().provide(Factory.class).repeating(interval, executor);
    }

    static AbilityTask repeatingUntil(final AbilityControl control, final AbilityExecutor onControl,
                                      final Ticks interval, final Supplier<AbilityTaskExecutor> executor) {
        return Sponge.getGame().getFactoryProvider().provide(Factory.class).repeatingUntil(control, onControl, interval, executor);
    }

    @Override
    void execute(AbilityContext context);

    interface Factory {

        AbilityTask immediate(Supplier<AbilityTaskExecutor> executor);

        AbilityTask nextTick(Supplier<AbilityTaskExecutor> executor);

        AbilityTask delayed(Ticks delay, Supplier<AbilityTaskExecutor> executor);

        AbilityTask repeating(Ticks interval, Supplier<AbilityTaskExecutor> executor);

        AbilityTask repeatingUntil(AbilityControl control, AbilityExecutor onControl,
                                   Ticks interval, Supplier<AbilityTaskExecutor> executor);
    }
}