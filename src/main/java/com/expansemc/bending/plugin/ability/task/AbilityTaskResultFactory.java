package com.expansemc.bending.plugin.ability.task;

import com.expansemc.bending.api.ability.task.AbilityTask;
import com.expansemc.bending.api.ability.task.AbilityTaskResult;

import java.util.Optional;

public final class AbilityTaskResultFactory implements AbilityTaskResult.Factory {

    @Override
    public AbilityTaskResult end() {
        return End.INSTANCE;
    }

    @Override
    public AbilityTaskResult next(final AbilityTask next) {
        return new Next(next);
    }

    @Override
    public AbilityTaskResult repeat() {
        return Repeat.INSTANCE;
    }

    private static final class End implements AbilityTaskResult {

        public static final End INSTANCE = new End();

        private End() {
        }

        @Override
        public Optional<AbilityTask> next() {
            return Optional.empty();
        }

        @Override
        public boolean isRepeat() {
            return false;
        }
    }

    private static final class Next implements AbilityTaskResult {

        private final AbilityTask task;

        public Next(final AbilityTask task) {
            this.task = task;
        }

        @Override
        public Optional<AbilityTask> next() {
            return Optional.of(this.task);
        }

        @Override
        public boolean isRepeat() {
            return false;
        }
    }

    private static final class Repeat implements AbilityTaskResult {

        public static final Repeat INSTANCE = new Repeat();

        private Repeat() {
        }

        @Override
        public Optional<AbilityTask> next() {
            return Optional.empty();
        }

        @Override
        public boolean isRepeat() {
            return true;
        }
    }
}