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
        return entitiesWithin(origin.getWorld(), origin.getPosition(), radius);
    }

    // TODO: remove when EntityVolume#getNearbyEntities is fixed
    public static List<Entity> entitiesWithin(final ServerWorld world, final Vector3d center, final double radius) {
        final List<Entity> result = new ArrayList<>();
        for (final Entity entity : world.getEntities()) {
            entity.getBoundingBox().ifPresent(aabb -> {
                if (intersectsSphere(aabb, center, radius)) {
                    result.add(entity);
                }
            });
        }
        return result;
    }

    private static boolean intersectsSphere(final AABB box, final Vector3d center, final double radius) {
        double radiusSquared = radius * radius;
        if (center.getX() < box.getMin().getX()) {
            final double diff = center.getX() - box.getMin().getX();
            radiusSquared -= diff * diff;
        } else if (center.getX() > box.getMax().getX()) {
            final double diff = center.getX() - box.getMax().getX();
            radiusSquared -= diff * diff;
        }
        if (center.getY() < box.getMin().getY()) {
            final double diff = center.getY() - box.getMin().getY();
            radiusSquared -= diff * diff;
        } else if (center.getY() > box.getMax().getY()) {
            final double diff = center.getY() - box.getMax().getY();
            radiusSquared -= diff * diff;
        }
        if (center.getZ() < box.getMin().getZ()) {
            final double diff = center.getZ() - box.getMin().getZ();
            radiusSquared -= diff * diff;
        } else if (center.getZ() > box.getMax().getZ()) {
            final double diff = center.getZ() - box.getMax().getZ();
            radiusSquared -= diff * diff;
        }
        return radiusSquared > 0;
    }

    private EntityUtil() {
    }
}