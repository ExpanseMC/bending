package com.expansemc.bending.api.util;

import com.expansemc.bending.api.ray.Raycast;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.matrix.Matrix3d;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities revolving around {@link Vector3d}s and {@link Vector3i}s.
 */
public final class VectorUtil {

    public static final Vector3d VECTOR_0_275 = new Vector3d(0.275, 0.275, 0.275);

    /**
     * Calculate the angle (in radians) between two vectors.
     *
     * @param from First vector.
     * @param to   Second vector.
     * @return The angle in radians.
     */
    public static double angle(final Vector3d from, final Vector3d to) {
        return Math.acos(from.dot(to) / (from.length() * to.length()));
    }

    public static List<Vector3d> sphereDirections(final int thetaMin, final int thetaMax,
                                                  final int angleTheta, final int anglePhi) {
        final List<Vector3d> result = new ArrayList<>();

        for (int theta = thetaMin; theta <= thetaMax; theta += angleTheta) {
            final double thetaRad = Math.toRadians(theta);
            final double sinTheta = Math.sin(thetaRad);
            final double cosTheta = Math.cos(thetaRad);

            final int deltaPhi = (int) (anglePhi / sinTheta);

            for (int phi = 0; phi < 360; phi += deltaPhi) {
                final double phiRad = Math.toRadians(phi);
                final double sinPhi = Math.sin(phiRad);
                final double cosPhi = Math.cos(phiRad);

                final Vector3d direction = new Vector3d(
                        cosPhi * sinTheta,
                        sinPhi * sinTheta,
                        cosTheta
                );
                result.add(direction);
            }
        }

        return result;
    }

    public static List<Raycast> sphereRaycasts(final ServerLocation origin, final List<Vector3d> directions,
                                               final double range, final double speed) {
        return sphereRaycasts(origin, directions, range, speed, Vector3d.ZERO, 0.0);
    }

    public static List<Raycast> sphereRaycasts(final ServerLocation origin, final List<Vector3d> directions,
                                               final double range, final double speed,
                                               final Vector3d targetDirection, final double maxAngleRadians) {
        final List<Raycast> result = new ArrayList<>();
        for (final Vector3d direction : directions) {
            if (maxAngleRadians > 0.0 && VectorUtil.angle(direction, targetDirection) > maxAngleRadians) {
                continue;
            }

            result.add(new Raycast(origin, direction, range, speed, true));
        }
        return result;
    }

    public static List<Matrix3d> arcMatrices(final int arcDegrees, final int arcIncrementDegrees) {
        final List<Matrix3d> result = new ArrayList<>();
        for (int angle = -arcDegrees; angle < arcDegrees; angle += arcIncrementDegrees) {
            final double angleRad = Math.toRadians(angle);

            final double sinAngle = Math.sin(angleRad);
            final double cosAngle = Math.cos(angleRad);

            final Matrix3d transformation = new Matrix3d(
                    cosAngle, 0.0, -sinAngle,
                    0.0, 1.0, 0.0,
                    sinAngle, 0.0, cosAngle
            );
            result.add(transformation);
        }
        return result;
    }

    public static List<Raycast> arcRaycasts(final ServerLocation origin, final Vector3d direction,
                                            final List<Matrix3d> transforms,
                                            final double range, final double speed) {
        final List<Raycast> result = new ArrayList<>();
        for (final Matrix3d transformation : transforms) {
            result.add(new Raycast(origin, transformation.transform(direction), range, speed, true));
        }
        return result;
    }

    private VectorUtil() {
    }
}