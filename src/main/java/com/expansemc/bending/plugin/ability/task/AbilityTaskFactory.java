package com.expansemc.bending.plugin.ability.task;

import com.expansemc.bending.api.Bending;
import com.expansemc.bending.api.ability.AbilityControl;
import com.expansemc.bending.api.ability.AbilityException;
import com.expansemc.bending.api.ability.execution.AbilityContext;
import com.expansemc.bending.api.ability.execution.AbilityExecutor;
import com.expansemc.bending.api.ability.task.AbilityTask;
import com.expansemc.bending.api.ability.task.AbilityTaskExecutor;
import com.expansemc.bending.api.ability.task.AbilityTaskResult;
import com.expansemc.bending.api.util.Completion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import java.util.function.Supplier;

public final class AbilityTaskFactory implements AbilityTask.Factory {

    private static final Component DEFAULT_ERROR_MESSAGE = Component.text("An error occurred while executing the ability: ", NamedTextColor.RED);

    @Override
    public AbilityTask immediate(final Supplier<AbilityTaskExecutor> executor) {
        return context -> this.executeOneShot(context, executor.get());
    }

    @Override
    public AbilityTask nextTick(final Supplier<AbilityTaskExecutor> executor) {
        return context -> {
            final Task task = Task.builder()
                    .plugin(Bending.get().implementation())
                    .execute(() -> this.executeOneShot(context, executor.get()))
                    .build();

            context.setTask(task);
        };
    }

    @Override
    public AbilityTask delayed(final Ticks delay, final Supplier<AbilityTaskExecutor> executor) {
        return context -> {
            final Task task = Task.builder()
                    .plugin(Bending.get().implementation())
                    .delay(delay)
                    .execute(() -> this.executeOneShot(context, executor.get()))
                    .build();

            context.setTask(task);
        };
    }

    private void executeOneShot(final AbilityContext context, final AbilityTaskExecutor executor) {
        if (!context.cause().isValid()) {
            // Cause is no longer valid (player dead or offline), so end ability execution now.
            context.cancel();
            return;
        }

        try {
            final AbilityTaskResult result = executor.execute(context.cause());

            result.next().ifPresentOrElse(
                    task -> task.execute(context),
                    context::cancel
            );
        } catch (final AbilityException e) {
            context.cause().audience().sendMessage(e.toComponent(() -> DEFAULT_ERROR_MESSAGE.append(context.cause().ability().name())));
            context.cancel();
        }
    }

    @Override
    public AbilityTask repeating(final Ticks interval, final Supplier<AbilityTaskExecutor> executor) {
        return context -> {
            final Task task = Task.builder()
                    .plugin(Bending.get().implementation())
                    .interval(interval)
                    .execute(new Repeating(context, executor.get(), null, null))
                    .build();

            context.setTask(task);
        };
    }

    @Override
    public AbilityTask repeatingUntil(final AbilityControl control, final AbilityExecutor onControl,
                                      final Ticks interval, final Supplier<AbilityTaskExecutor> executor) {
        return context -> {
            final Completion completion = context.defer(control);
            final Task task = Task.builder()
                    .plugin(Bending.get().implementation())
                    .interval(interval)
                    .execute(new Repeating(context, executor.get(), completion, onControl))
                    .build();

            context.setTask(task);
        };
    }

    private static final class Repeating implements Runnable {

        private final AbilityContext context;
        private final AbilityTaskExecutor executor;
        private final @Nullable Completion completion;
        private final @Nullable AbilityExecutor onComplete;

        public Repeating(final AbilityContext context, final AbilityTaskExecutor executor,
                         final @Nullable Completion completion, final @Nullable AbilityExecutor onComplete) {
            this.context = context;
            this.executor = executor;
            this.completion = completion;
            this.onComplete = onComplete;
        }

        @Override
        public void run() {
            if (this.completion != null && this.onComplete != null && this.completion.isCompleted()) {
                if (!this.completion.isCancelled()) {
                    // Completion finished successfully, execute onComplete.
                    try {
                        this.onComplete.execute(this.context);
                    } catch (final AbilityException e) {
                        this.context.cause().audience().sendMessage(e.toComponent(() -> DEFAULT_ERROR_MESSAGE.append(this.context.cause().ability().name())));
                        this.context.cancel();
                    }
                }

                // Completion finished, return now.
                return;
            }

            if (!this.context.cause().isValid()) {
                // Cause is no longer valid (player dead or offline), so end ability execution now.
                this.context.cancel();
                return;
            }

            try {
                final AbilityTaskResult result = this.executor.execute(this.context.cause());

                if (result.isRepeat()) {
                    return;
                }

                result.next().ifPresentOrElse(
                        task -> task.execute(this.context),
                        this.context::cancel
                );
            } catch (final AbilityException e) {
                this.context.cause().audience().sendMessage(e.toComponent(() -> DEFAULT_ERROR_MESSAGE.append(this.context.cause().ability().name())));
                this.context.cancel();
            }
        }
    }
}