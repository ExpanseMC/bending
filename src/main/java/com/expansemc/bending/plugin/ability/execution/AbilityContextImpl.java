package com.expansemc.bending.plugin.ability.execution;

import com.expansemc.bending.api.ability.AbilityControl;
import com.expansemc.bending.api.ability.execution.AbilityCause;
import com.expansemc.bending.api.ability.execution.AbilityContext;
import com.expansemc.bending.api.util.Completion;
import com.expansemc.bending.plugin.bender.BenderImpl;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;

public final class AbilityContextImpl implements AbilityContext {

    private final BenderImpl bender;
    private final AbilityCause cause;

    private @Nullable ScheduledTask task = null;

    public AbilityContextImpl(final BenderImpl bender, final AbilityCause cause) {
        this.bender = bender;
        this.cause = cause;
    }

    @Override
    public BenderImpl bender() {
        return this.bender;
    }

    @Override
    public AbilityCause cause() {
        return this.cause;
    }

    @Override
    public Optional<ScheduledTask> task() {
        return Optional.ofNullable(this.task);
    }

    @Override
    public void setTask(final Task task) {
        this.task = Sponge.getServer().getScheduler().submit(task);
    }

    @Override
    public void cancel() {
        if (this.task != null) {
            this.task.cancel();
            this.bender.removeContext(this);
        }
        this.task = null;
    }

    @Override
    public Completion defer(final AbilityControl control) {
        // TODO
        return null;
    }

    @Override
    public boolean isFinished() {
        return this.task == null;
    }
}