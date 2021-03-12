package com.expansemc.bending.api.ray;

import com.expansemc.bending.api.util.EntityUtil;
import com.expansemc.bending.api.util.LocationUtil;
import com.expansemc.bending.api.util.VectorUtil;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * An incremental (usually particle) ray casting class.
 *
 * <p>This class differs from {@link org.spongepowered.api.util.blockray.RayTrace}
 * in that its primary usage revolves around step-by-step execution, rather
 * than execution at the end of the ray.</p>
 */
public final class Raycast {

    public static boolean advanceAll(final Iterable<Raycast> raycasts, final BiPredicate<Raycast, ServerLocation> predicate) {
        final Iterator<Raycast> iter = raycasts.iterator();
        boolean anySucceeded = false;
        while (iter.hasNext()) {
            final Raycast raycast = iter.next();

            if (raycast.advance(predicate)) {
                anySucceeded = true;
            } else {
                iter.remove();
            }
        }
        return anySucceeded;
    }

    private final ServerLocation origin;
    private final Vector3d direction;
    private final double range;
    private final double speed;
    private final boolean checkCorners;

    private final double speedFactor;
    private final double rangeSquared;
    private final Vector3d step;

    private ServerLocation currentLocation;

    public Raycast(final ServerLocation origin, final Vector3d direction, final double range, final double speed, final boolean checkCorners) {
        this.origin = origin;
        this.direction = direction.normalize();
        this.range = range;
        this.speed = speed;
        this.checkCorners = checkCorners;

        this.speedFactor = this.speed * (50 / 1000.0);
        this.rangeSquared = this.range * this.range;
        this.step = this.direction.mul(this.speedFactor);

        this.currentLocation = this.origin;
    }

    /**
     * Advances the ray based on the provided predicate.
     *
     * @param predicate
     * @return True if the ray moved, false otherwise.
     */
    public final boolean advance(final Predicate<ServerLocation> predicate) {
        return this.advance(((r, l) -> predicate.test(l)));
    }

    /**
     * Advances the ray based on the provided predicate.
     *
     * @param predicate
     * @return True if the ray moved, false otherwise.
     */
    public final boolean advance(final BiPredicate<Raycast, ServerLocation> predicate) {
        if (this.range > 0 && this.currentLocation.getPosition().distanceSquared(this.origin.getPosition()) > this.rangeSquared) {
            // The ray is beyond its allowed range.
            return false;
        }

        if (this.checkCorners && LocationUtil.hasCorners(this.currentLocation, this.direction)) {
            // Found a corner, stop advancing.
            return false;
        }

        final boolean result = predicate.test(this, this.currentLocation);
        if (!result) {
            return false;
        }

        this.currentLocation = this.currentLocation.add(this.step);
        return true;
    }

    public final void affectLocations(final Collection<ServerLocation> affected, final double radius, final Predicate<ServerLocation> predicate) {
        for (final ServerLocation location : LocationUtil.sphere(this.currentLocation, radius)) {
            if (affected.contains(location)) {
                // This location has already been affected.
                continue;
            }

            if (predicate.test(location)) {
                // The location was affected.
                affected.add(location);
            }
        }
    }

    public final void affectEntities(final Collection<UUID> affected, final double radius, final Predicate<Entity> predicate) {
        for (final Entity entity : EntityUtil.entitiesWithin(this.currentLocation, radius)) {
            if (affected.contains(entity.getUniqueId())) {
                // This entity has already been affected.
                continue;
            }

            if (predicate.test(entity)) {
                // The entity was affected.
                affected.add(entity.getUniqueId());
            }
        }
    }

    public final boolean pushEntity(final Cause source, final Entity target, final boolean canPushSelf,
                                    final double knockbackSelf, final double knockbackOther) {
        final boolean isSelf = source.contains(target);

        double knockback = knockbackOther;
        if (isSelf) {
            if (!canPushSelf) {
                // Can't push ourselves.
                return false;
            }
            knockback = knockbackSelf;
        }

        knockback *= 1 - target.getPosition().distance(this.origin.getPosition()) / (2 * this.range);

        if (target.getServerLocation().add(0.0, -0.5, 0.0).getOrElse(Keys.IS_SOLID, false)) {
            knockback *= 0.85;
        }

        Vector3d result = this.direction.mul(knockback);

        final Vector3d targetVelocity = target.velocity().get();
        if (Math.abs(targetVelocity.dot(result)) > knockback && VectorUtil.angle(targetVelocity, result) > Math.PI / 3) {
            result = result.normalize().add(targetVelocity).mul(knockback);
        }

        target.offer(Keys.VELOCITY, result);
        return true;
    }

    public final boolean damageEntity(final Cause source, final Entity test, final double damage) {
        return this.damageEntity(source, test, damage, DamageSources.GENERIC);
    }

    public final boolean damageEntity(final Cause source, final Entity test, final double damage, final DamageSource damageSource) {
        if (!source.contains(test) && damage > 0) {
            return test.damage(damage, damageSource);
        }
        return false;
    }

    public final ServerLocation origin() {
        return this.origin;
    }

    public final Vector3d direction() {
        return this.direction;
    }

    public final double range() {
        return this.range;
    }

    public final double speed() {
        return this.speed;
    }

    public final boolean checkCorners() {
        return this.checkCorners;
    }

    public final double speedFactor() {
        return this.speedFactor;
    }

    public final double rangeSquared() {
        return this.rangeSquared;
    }

    public final Vector3d step() {
        return this.step;
    }

    public final ServerLocation currentLocation() {
        return this.currentLocation;
    }
}