package com.expansemc.bending.api.ability.execution;

import com.expansemc.bending.api.ability.AbilityControl;
import com.expansemc.bending.api.bender.Bender;
import com.expansemc.bending.api.util.Completion;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;

/**
 * The context of how a long running ability with an {@link AbilityCause} is
 * executed with {@link Task}s.
 */
public interface AbilityContext {

    /**
     * The {@link Bender} executing this ability.
     *
     * @return The bender.
     */
    Bender bender();

    /**
     * The {@link AbilityCause}.
     *
     * @return The ability cause.
     */
    AbilityCause cause();

    /**
     * The currently executing {@link ScheduledTask}.
     *
     * @return The task or empty if not executing.
     */
    Optional<ScheduledTask> task();

    /**
     * Sets the {@link Task} that should be executed for this ability.
     *
     * @param task The task.
     */
    void setTask(Task task);

    /**
     * Cancels this ability's execution ASAP.
     */
    void cancel();

    /**
     * Waits for this ability to be re-executed with the provided
     * {@link AbilityControl}.
     *
     * @param control The control to wait for.
     * @return The completion state.
     */
    Completion defer(AbilityControl control);

    /**
     * Whether this ability is done executing.
     *
     * @return True if finished, false otherwise.
     */
    boolean isFinished();
}