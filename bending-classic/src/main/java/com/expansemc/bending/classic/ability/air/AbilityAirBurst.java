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
import com.expansemc.bending.api.util.SphereMath;
import com.expansemc.bending.api.util.VectorUtil;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
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

public final class AbilityAirBurst {

    public static final Ability ABILITY = Ability.builder()
            .name(Component.text("AirBurst"))
            .category(AbilityCategories.AIR)
            .executor(AbilityTask.immediate(Start::new))
            .addControls(FALL.get(), PRIMARY.get(), SNEAK.get())
            .build();

    private static final ParticleEffect PARTICLE_EFFECT = ParticleEffect.builder()
            .type(ParticleTypes.CLOUD)
            .quantity(2)
            .offset(VectorUtil.VECTOR_0_275)
            .build();

    private static final long CHARGE_TIME = 1750;
    private static final double BLAST_RADIUS = 1.0;
    private static final double KNOCKBACK = 2.8;
    private static final double RANGE = 20.0;
    private static final double SPEED = 25.0;
    private static final double FALL_THRESHOLD = 10.0;
    private static final int ANGLE_THETA = 10;
    private static final int ANGLE_PHI = 10;
    private static final int MAX_CONE_DEGREES = 30;
    private static final double MAX_CONE_RADIANS = Math.toRadians(MAX_CONE_DEGREES);

    private static final List<Vector3d> FALL_DIRECTIONS = SphereMath.directions(75, 105, ANGLE_THETA, ANGLE_PHI);
    private static final List<Vector3d> SPHERE_DIRECTIONS = SphereMath.directions(0, 180, ANGLE_THETA, ANGLE_PHI);

    private static final class Start implements AbilityTaskExecutor {

        private static final Component VALID_CONTROLS = Component.text("Valid ability controls: ")
                .append(Component.join(Component.text(", "), FALL.get().name(), PRIMARY.get().name(), SNEAK.get().name()));

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) throws AbilityException {
            if (cause.control() == FALL.get()) {
                if (cause.cause().getContext().require(BendingEventContextKeys.FALL_DISTANCE) > FALL_THRESHOLD) {
                    final AbilityTask task = AbilityTask.repeating(Ticks.single(), () -> new Burst(cause.location(), FALL_DIRECTIONS));
                    return AbilityTaskResult.next(task);
                } else {
                    return AbilityTaskResult.end();
                }
            } else if (cause.control() == PRIMARY.get()) {
                final AbilityTask task = AbilityTask.repeating(Ticks.single(), () -> new Burst(cause.location(), SPHERE_DIRECTIONS, cause.headDirection(), MAX_CONE_RADIANS));
                return AbilityTaskResult.next(task);
            } else if (cause.control() == SNEAK.get()) {
                final AbilityTask task = AbilityTask.repeating(Ticks.single(), Charging::new);
                return AbilityTaskResult.next(task);
            } else {
                throw new AbilityException(VALID_CONTROLS);
            }
        }
    }

    private static final class Charging implements AbilityTaskExecutor {

        private static final Duration CHARGE_DURATION = Duration.ofMillis(CHARGE_TIME);

        private static final ParticleEffect CHARGED_PARTICLE = ParticleEffect.builder()
                .type(ParticleTypes.CLOUD)
                .quantity(10)
                .offset(VectorUtil.VECTOR_0_275)
                .build();

        private static final ParticleEffect CHARGING_PARTICLE = ParticleEffect.builder()
                .type(ParticleTypes.SMOKE)
                .quantity(4)
                .offset(VectorUtil.VECTOR_0_275)
                .build();

        private boolean charged = false;
        private final Instant startTime = Instant.now();

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) throws AbilityException {
            if (!this.charged && Duration.between(this.startTime, Instant.now()).compareTo(CHARGE_DURATION) >= 0) {
                // Now fully charged.
                this.charged = true;
            }

            final ServerPlayer player = cause.cause().first(ServerPlayer.class).get();
            if (!player.getOrElse(Keys.IS_SNEAKING, false)) {
                if (this.charged) {
                    // Activate.
                    final AbilityTask task = AbilityTask.repeating(Ticks.single(), () -> new Burst(cause.location(), SPHERE_DIRECTIONS));
                    return AbilityTaskResult.next(task);
                } else {
                    // Not charged, do nothing.
                    return AbilityTaskResult.end();
                }
            }

            final ServerLocation eyeLocation = cause.eyeLocation();
            if (this.charged) {
                eyeLocation.getWorld().spawnParticles(CHARGED_PARTICLE, eyeLocation.getPosition());
            } else {
                eyeLocation.getWorld().spawnParticles(CHARGING_PARTICLE, eyeLocation.getPosition());
            }

            return AbilityTaskResult.repeat();
        }
    }

    private static final class Burst implements AbilityTaskExecutor {

        private final List<Raycast> raycasts;

        private final Set<ServerLocation> affectedLocations = new HashSet<>();
        private final Set<UUID> affectedEntities = new HashSet<>();

        public Burst(final ServerLocation origin, final List<Vector3d> directions) {
            this(origin, directions, Vector3d.ZERO, 0.0);
        }

        public Burst(final ServerLocation origin, final List<Vector3d> directions,
                     final Vector3d targetDirection, final double maxAngle) {
            this.raycasts = SphereMath.raycasts(origin, directions, RANGE, SPEED, targetDirection, maxAngle);
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) throws AbilityException {
            if (Raycast.advanceAll(this.raycasts, ((raycast, location) -> this.progress(raycast, location, cause)))) {
                return AbilityTaskResult.repeat();
            }
            return AbilityTaskResult.end();
        }

        private boolean progress(final Raycast raycast, final ServerLocation current, final AbilityCause cause) {
            if (current.getOrElse(Keys.IS_SOLID, false) || !current.getFluid().isEmpty()) {
                // End if the current block is solid or liquid.
                return false;
            }

            raycast.affectLocations(this.affectedLocations, BLAST_RADIUS, test ->
//                    AirRaycast.extinguishFlames(test) || AirRaycast.toggleOpenable(test) || AirRaycast.togglePowerable(test));
                    false); // TODO

            raycast.affectEntities(this.affectedEntities, BLAST_RADIUS, test -> {
                // Ignore return value so we can push entities multiple times with the same ray.
                raycast.pushEntity(cause.cause(), test, false, 0.0, KNOCKBACK);

                return true;
            });

            // Pretty!
            current.getWorld().spawnParticles(PARTICLE_EFFECT, current.getPosition());

            if (Math.random() < 0.05) {
                // Add some sound every now and then.
                cause.audience().playSound(Sound.sound(SoundTypes.ENTITY_CREEPER_HURT, Sound.Source.PLAYER, 0.5f, 1.0f));
            }

            return true;
        }
    }
}