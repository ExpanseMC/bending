package com.expansemc.bending.api.util;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utilities revolving around {@link ServerLocation}s.
 */
public final class LocationUtil {

    /**
     * Generates a list of {@link ServerLocation}s that collectively represent
     * a sphere from the provided origin and radius.
     *
     * @param origin The center of the sphere.
     * @param radius The radius of the sphere.
     * @return The list of locations.
     */
    public static List<ServerLocation> sphere(final ServerLocation origin, final double radius) {
        final List<ServerLocation> result = new ArrayList<>();

        final int originX = origin.blockX();
        final int originY = origin.blockY();
        final int originZ = origin.blockZ();

        final int r = (int) (radius * 4);
        final double radiusSquared = radius * radius;

        for (int x = originX - r; x < originX + r; x++) {
            for (int y = originY - r; y < originY + r; y++) {
                for (int z = originZ - r; z < originZ + r; z++) {
                    final ServerLocation location = origin.withPosition(new Vector3d(x, y, z));

                    if (location.position().distanceSquared(origin.position()) <= radiusSquared) {
                        result.add(location);
                    }
                }
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static ServerLocation targetLocation(final ServerLocation origin, final Vector3d direction, final double range) {
        return targetLocation(origin, direction, range, (type) -> type.isAnyOf(BlockTypes.AIR, BlockTypes.CAVE_AIR, BlockTypes.VOID_AIR));
    }

    public static ServerLocation targetLocation(final ServerLocation origin, final Vector3d direction,
                                                final double range, final Predicate<BlockType> passthrough) {
        return targetLocation(origin, direction, range, true, passthrough);
    }

    public static ServerLocation targetLocation(final ServerLocation origin, final Vector3d direction,
                                                final double range, final boolean checkCorners,
                                                final Predicate<BlockType> passthrough) {
        final Vector3d increment = direction.normalize().mul(0.2);
        ServerLocation location = origin;

        for (double i = 0.0; i < range; i += 0.2) {
            final ServerLocation current = location.add(increment);

            if (checkCorners && hasCorners(current, increment)) {
                break;
            }
            if (!passthrough.test(current.blockType())) {
                break;
            }

            location = current;
        }

        return location;
    }

    /**
     * Whether the provided {@link ServerLocation location} has a corner in the
     * provided {@link Vector3d direction}.
     *
     * @param location  The location.
     * @param direction The direction.
     * @return True if at a corner, false otherwise.
     */
    public static boolean hasCorners(final ServerLocation location, final Vector3d direction) {
        final boolean isSolidX = location.relativeToBlock(axisDirectionOnX(direction.getX())).getOrElse(Keys.IS_SOLID, false);
        final boolean isSolidY = location.relativeToBlock(axisDirectionOnY(direction.getY())).getOrElse(Keys.IS_SOLID, false);
        final boolean isSolidZ = location.relativeToBlock(axisDirectionOnZ(direction.getZ())).getOrElse(Keys.IS_SOLID, false);

        final boolean xz = isSolidX && isSolidZ;
        final boolean xy = isSolidX && isSolidY;
        final boolean yz = isSolidY && isSolidZ;

        return xz || xy || yz;
    }

    private static Direction axisDirectionOnX(final double length) {
        if (length > 0) {
            return Direction.EAST;
        } else if (length < 0) {
            return Direction.WEST;
        } else {
            return Direction.NONE;
        }
    }

    private static Direction axisDirectionOnY(final double length) {
        if (length > 0) {
            return Direction.UP;
        } else if (length < 0) {
            return Direction.DOWN;
        } else {
            return Direction.NONE;
        }
    }

    private static Direction axisDirectionOnZ(final double length) {
        if (length > 0) {
            return Direction.SOUTH;
        } else if (length < 0) {
            return Direction.NORTH;
        } else {
            return Direction.NONE;
        }
    }

    private LocationUtil() {
    }
}