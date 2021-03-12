package com.expansemc.bending.classic.ability.air;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityCategories;
import com.expansemc.bending.api.ability.AbilityException;
import com.expansemc.bending.api.ability.execution.AbilityCause;
import com.expansemc.bending.api.ability.task.AbilityTask;
import com.expansemc.bending.api.ability.task.AbilityTaskExecutor;
import com.expansemc.bending.api.ability.task.AbilityTaskResult;
import com.expansemc.bending.api.event.BendingEventContextKeys;
import com.expansemc.bending.api.ray.Raycast;
import com.expansemc.bending.api.util.VectorUtil;
import com.expansemc.bending.classic.ability.AirAbilities;
import com.expansemc.bending.classic.ability.config.AirBurstConfig;
import com.expansemc.bending.classic.util.AbilityUtil;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.expansemc.bending.api.ability.AbilityControls.*;

public final class AirBurstAbility {

    public static final Ability ABILITY = Ability.builder()
            .name(Component.text("AirBurst"))
            .category(AbilityCategories.AIR)
            .executor(AbilityTask.immediate(() -> new Start(AirBurstConfig.DEFAULT)))
            .addControls(FALL.get(), PRIMARY.get(), SNEAK.get())
            .build();

    //////////////////////////////////////////////////
    // Ability Start
    //////////////////////////////////////////////////

    private static final class Start implements AbilityTaskExecutor {

        private final AirBurstConfig config;

        private Start(final AirBurstConfig config) {
            this.config = config;
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) throws AbilityException {
            final ServerLocation eyeLocation = cause.eyeLocation();
            if (eyeLocation.getOrElse(Keys.IS_SOLID, false) || !eyeLocation.getFluid().isEmpty()) {
                return AbilityTaskResult.end();
            }

            if (cause.control() == FALL.get()) {
                if (cause.cause().getContext().require(BendingEventContextKeys.FALL_DISTANCE) > this.config.fallThreshold()) {
                    return AbilityTaskResult.next(burstFall(this.config, cause.location()));
                } else {
                    return AbilityTaskResult.end();
                }
            } else if (cause.control() == PRIMARY.get()) {
                return AbilityTaskResult.next(burstCone(this.config, cause.eyeLocation(), cause.headDirection()));
            } else if (cause.control() == SNEAK.get()) {
                return AbilityTaskResult.next(charging(this.config));
            } else {
                throw new AbilityException(AbilityUtil.validControlsError(ABILITY));
            }
        }
    }

    //////////////////////////////////////////////////
    // Ability Charging
    //////////////////////////////////////////////////

    private static AbilityTask charging(final AirBurstConfig config) {
        return AbilityTask.repeating(Ticks.single(), () -> new Charging(config));
    }

    private static final class Charging implements AbilityTaskExecutor {

        private final AirBurstConfig config;
        private final Instant startTime;

        private boolean fullyCharged = false;

        private Charging(final AirBurstConfig config) {
            this.config = config;
            this.startTime = Instant.now();
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) {
            if (!this.fullyCharged && Duration.between(this.startTime, Instant.now()).compareTo(this.config.chargeDuration()) >= 0) {
                // Now fully charged.
                this.fullyCharged = true;
            }

            if (!cause.dataHolder().getOrElse(Keys.IS_SNEAKING, false)) {
                if (this.fullyCharged) {
                    // Activate.
                    return AbilityTaskResult.next(burstSphere(this.config, cause.eyeLocation()));
                } else {
                    // Not charged, do nothing.
                    return AbilityTaskResult.end();
                }
            }

            final ServerLocation eyeLocation = cause.eyeLocation();
            if (this.fullyCharged) {
                eyeLocation.getWorld().spawnParticles(this.config.chargedParticle(), eyeLocation.getPosition());
            } else {
                eyeLocation.getWorld().spawnParticles(this.config.chargingParticle(), eyeLocation.getPosition());
            }

            return AbilityTaskResult.repeat();
        }
    }

    //////////////////////////////////////////////////
    // Ability Burst
    //////////////////////////////////////////////////

    private static AbilityTask burstFall(final AirBurstConfig config, final ServerLocation origin) {
        return AbilityTask.repeating(Ticks.single(), () -> new Burst(config, config.fallDirections(), origin));
    }

    private static AbilityTask burstSphere(final AirBurstConfig config, final ServerLocation origin) {
        return AbilityTask.repeating(Ticks.single(), () -> new Burst(config, config.sphereDirections(), origin));
    }

    private static AbilityTask burstCone(final AirBurstConfig config, final ServerLocation origin, final Vector3d direction) {
        return AbilityTask.repeating(Ticks.single(), () -> new Burst(
                config, origin, config.sphereDirections(),
                direction, config.maxConeRadians()
        ));
    }

    private static final class Burst implements AbilityTaskExecutor {

        private final AirBurstConfig config;
        private final List<Raycast> raycasts;

        private final Set<ServerLocation> affectedLocations;
        private final Set<UUID> affectedEntities;

        public Burst(final AirBurstConfig config, final List<Vector3d> directions, final ServerLocation origin) {
            this(config, origin, directions, Vector3d.ZERO, 0.0);
        }

        public Burst(final AirBurstConfig config, final ServerLocation origin, final List<Vector3d> directions,
                     final Vector3d targetDirection, final double maxAngle) {
            this.config = config;
            this.raycasts = VectorUtil.sphereRaycasts(origin, directions, this.config.range(), this.config.speed(), targetDirection, maxAngle);

            this.affectedLocations = new HashSet<>();
            this.affectedEntities = new HashSet<>();
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) {
            if (Raycast.advanceAll(this.raycasts, (raycast, location) -> this.progress(raycast, location, cause))) {
                return AbilityTaskResult.repeat();
            }
            return AbilityTaskResult.end();
        }

        private boolean progress(final Raycast raycast, final ServerLocation current, final AbilityCause cause) {
            if (current.getOrElse(Keys.IS_SOLID, false) || !current.getFluid().isEmpty()) {
                // End if the current block is solid or liquid.
                return false;
            }

            raycast.affectLocations(this.affectedLocations, this.config.blastRadius(), test ->
//                    AirRaycast.extinguishFlames(test) || AirRaycast.toggleOpenable(test) || AirRaycast.togglePowerable(test));
                    false); // TODO

            raycast.affectEntities(this.affectedEntities, this.config.blastRadius(), test -> {
                // Ignore return value so we can push entities multiple times with the same ray.
                raycast.pushEntity(cause.cause(), test, false, 0.0, this.config.knockback());

                return true;
            });

            // Pretty!
            current.getWorld().spawnParticles(this.config.rayParticle(), current.getPosition());

            if (Math.random() < 0.05) {
                // Add some sound every now and then.
                cause.audience().playSound(AirAbilities.SOUND);
            }

            return true;
        }
    }
}