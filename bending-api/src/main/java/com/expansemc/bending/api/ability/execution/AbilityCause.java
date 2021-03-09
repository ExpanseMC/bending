package com.expansemc.bending.api.ability.execution;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityControl;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

/**
 * The who, what, where, why, and how of ability execution.
 */
public interface AbilityCause {

    static AbilityCause current(final Ability ability, final AbilityControl control) {
        return of(ability, control, Sponge.getServer().getCauseStackManager().getCurrentCause());
    }

    static AbilityCause of(final Ability ability, final AbilityControl control, final Cause cause) {
        return Sponge.getGame().getFactoryProvider().provide(Factory.class).of(ability, control, cause);
    }

    /**
     * The {@link Ability} being executed.
     *
     * @return The ability.
     */
    Ability ability();

    /**
     * How the ability is being executed.
     *
     * @return The ability control.
     */
    AbilityControl control();

    /**
     * Who or what is executing the ability.
     *
     * @return The cause.
     */
    Cause cause();

    /**
     * The {@link Subject} that should be used for permission checks.
     *
     * @return The subject.
     */
    Subject subject();

    /**
     * The {@link Audience} that should be used for sending messages to.
     *
     * @return The audience.
     */
    Audience audience();

    /**
     * The target location of the {@link #cause()}, up to a maximum distance.
     *
     * @param maxDistance The maximum distance.
     * @return The target location.
     */
    ServerLocation targetLocation(double maxDistance);

    /**
     * The location of the {@link #cause()}'s eye.
     *
     * <p>If the cause has no such eye location,
     * {@link Locatable#getServerLocation()} will be used instead.</p>
     *
     * @return The eye location.
     */
    ServerLocation eyeLocation();

    /**
     * The position of the {@link #cause()}'s eye.
     *
     * <p>If the cause has no such eye location,
     * {@link Locatable#getServerLocation()} and
     * {@link ServerLocation#getPosition()} will be used instead.</p>
     *
     * @return The eye position.
     */
    Vector3d eyePosition();

    /**
     * The direction of the {@link #cause()}'s head.
     *
     * <p>If the cause has no such head direction,
     * {@link Entity#getDirection()} will be used instead.</p>
     *
     * @return The head direction.
     */
    Vector3d headDirection();

    /**
     * Whether this cause is still valid to use (i.e. the source entity is
     * still living and loaded).
     *
     * @return True if valid, false otherwise.
     */
    boolean isValid();

    /**
     * A factory interface for creating {@link AbilityCause}s.
     */
    interface Factory {

        /**
         * @see AbilityCause#of(Ability, AbilityControl, Cause)
         */
        AbilityCause of(Ability ability, AbilityControl control, Cause cause);
    }
}