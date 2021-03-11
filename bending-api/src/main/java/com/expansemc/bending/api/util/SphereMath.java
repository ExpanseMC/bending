package com.expansemc.bending.api.util;

import com.expansemc.bending.api.ray.Raycast;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

public final class SphereMath {

    public static List<Vector3d> directions(final int thetaMin, final int thetaMax,
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

    public static List<Raycast> raycasts(final ServerLocation origin, final List<Vector3d> directions,
                                     final double range, final double speed) {
        return raycasts(origin, directions, range, speed, Vector3d.ZERO, 0.0);
    }

    public static List<Raycast> raycasts(final ServerLocation origin, final List<Vector3d> directions,
                                         final double range, final double speed,
                                         final Vector3d targetDirection, final double maxAngle) {
        final List<Raycast> result = new ArrayList<>();
        for (final Vector3d direction : directions) {
            if (maxAngle > 0.0 && VectorUtil.angle(direction, targetDirection) > maxAngle) {
                continue;
            }

            final Raycast raycast = new Raycast(origin, direction, range, speed, true);
            result.add(raycast);
        }
        return result;
    }

    public static List<Raycast> raycasts(final ServerLocation origin, final Vector3d[] directions,
                                         final double range, final double speed) {
        return raycasts(origin, directions, range, speed, Vector3d.ZERO, 0.0);
    }

    public static List<Raycast> raycasts(final ServerLocation origin, final Vector3d[] directions,
                                         final double range, final double speed,
                                         final Vector3d targetDirection, final double maxAngle) {
        final List<Raycast> result = new ArrayList<>();
        for (final Vector3d direction : directions) {
            if (maxAngle > 0.0 && VectorUtil.angle(direction, targetDirection) > maxAngle) {
                final Raycast raycast = new Raycast(origin, direction, range, speed, true);
                result.add(raycast);
            }
        }
        return result;
    }

    private SphereMath() {
    }
}