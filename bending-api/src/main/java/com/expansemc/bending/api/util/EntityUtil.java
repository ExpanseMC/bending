package com.expansemc.bending.api.util;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

public final class EntityUtil {

    public static List<Entity> entitiesWithin(final ServerLocation origin, final double radius) {
        return entitiesWithin(origin.world(), origin.position(), radius);
    }

    // TODO: remove when EntityVolume#getNearbyEntities is fixed
    public static List<Entity> entitiesWithin(final ServerWorld world, final Vector3d center, final double radius) {
        final List<Entity> result = new ArrayList<>();
        for (final Entity entity : world.entities()) {
            entity.boundingBox().ifPresent(aabb -> {
                if (intersectsSphere(aabb, center, radius)) {
                    result.add(entity);
                }
            });
        }
        return result;
    }

    private static boolean intersectsSphere(final AABB box, final Vector3d center, final double radius) {
        double radiusSquared = radius * radius;
        if (center.getX() < box.min().getX()) {
            final double diff = center.getX() - box.min().getX();
            radiusSquared -= diff * diff;
        } else if (center.getX() > box.max().getX()) {
            final double diff = center.getX() - box.max().getX();
            radiusSquared -= diff * diff;
        }
        if (center.getY() < box.min().getY()) {
            final double diff = center.getY() - box.min().getY();
            radiusSquared -= diff * diff;
        } else if (center.getY() > box.max().getY()) {
            final double diff = center.getY() - box.max().getY();
            radiusSquared -= diff * diff;
        }
        if (center.getZ() < box.min().getZ()) {
            final double diff = center.getZ() - box.min().getZ();
            radiusSquared -= diff * diff;
        } else if (center.getZ() > box.max().getZ()) {
            final double diff = center.getZ() - box.max().getZ();
            radiusSquared -= diff * diff;
        }
        return radiusSquared > 0;
    }

    private EntityUtil() {
    }
}