package com.expansemc.bending.api.util;

import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

/**
 * Utilities revolving around {@link Vector3d}s and {@link Vector3i}s.
 */
public final class VectorUtil {

    public static final Vector3d VECTOR_0_275 = new Vector3d(0.275, 0.275, 0.275);

    /**
     * Calculate the angle (in radians) between two vectors.
     *
     * @param from First vector.
     * @param to Second vector.
     * @return The angle in radians.
     */
    public static double angle(final Vector3d from, final Vector3d to) {
        return Math.acos(from.dot(to) / (from.length() * to.length()));
    }

    private VectorUtil() {
    }
}