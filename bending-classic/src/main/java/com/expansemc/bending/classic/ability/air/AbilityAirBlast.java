package com.expansemc.bending.classic.ability.air;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityCategories;
import com.expansemc.bending.api.ability.AbilityControls;
import com.expansemc.bending.api.ability.AbilityException;
import com.expansemc.bending.api.ability.execution.AbilityCause;
import com.expansemc.bending.api.ability.task.AbilityTask;
import com.expansemc.bending.api.ability.task.AbilityTaskExecutor;
import com.expansemc.bending.api.ability.task.AbilityTaskResult;
import com.expansemc.bending.api.ray.Raycast;
import com.expansemc.bending.api.util.VectorUtil;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class AbilityAirBlast {

    public static final Ability ABILITY = Ability.builder()
            .name(Component.text("AirBlast"))
            .category(AbilityCategories.AIR)
            .executor(AbilityTask.immediate(Start::new))
            .addControls(AbilityControls.PRIMARY.get(), AbilityControls.SNEAK.get())
            .build();

    private static final ParticleEffect PARTICLE_EFFECT = ParticleEffect.builder()
            .type(ParticleTypes.CLOUD)
            .quantity(4)
            .offset(VectorUtil.VECTOR_0_275)
            .build();

    // TODO: Runtime configuration values
    private static final double KNOCKBACK_OTHER = 1.6;
    private static final double KNOCKBACK_SELF = 2.0;
    private static final double RADIUS = 1.0;
    private static final double RANGE = 25.0;
    private static final double SELECT_RANGE = 10.0;
    private static final double SPEED = 21.0;

    /**
     * Handles the initial logic of when the player activates AirBlast.
     */
    private static final class Start implements AbilityTaskExecutor {

        private static final Component VALID_CONTROLS = Component.text("Valid ability controls: ")
                .append(AbilityControls.SNEAK.get().name())
                .append(Component.text(", "))
                .append(AbilityControls.PRIMARY.get().name());

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) throws AbilityException {
            if (cause.control() == AbilityControls.SNEAK.get()) {
                // Run the sneak task.
                final ServerLocation origin = cause.targetLocation(SELECT_RANGE);
                final Vector3d direction = cause.headDirection();

                return AbilityTaskResult.next(Sneak.task(origin, direction));
            } else if (cause.control() == AbilityControls.PRIMARY.get()) {
                // Run the primary task.
                final ServerLocation origin = cause.eyeLocation();
                final Vector3d direction = cause.headDirection();

                if (!origin.getFluid().isEmpty()) {
                    // End immediately if in a liquid.
                    return AbilityTaskResult.end();
                } else {
                    return AbilityTaskResult.next(Primary.task(origin, direction, false));
                }
            } else {
                throw new AbilityException(VALID_CONTROLS);
            }
        }
    }

    /**
     * Handles when the player sneaks to activate AirBlast.
     */
    private static final class Sneak implements AbilityTaskExecutor {

        private static AbilityTask task(final ServerLocation origin, final Vector3d direction) {
            return AbilityTask.repeatingUntil(
                    AbilityControls.PRIMARY.get(),
                    Primary.task(origin, direction, true),
                    Ticks.single(),
                    () -> new Sneak(origin)
            );
        }

        private final ServerLocation origin;

        public Sneak(final ServerLocation origin) {
            this.origin = origin;
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) {
            if (this.origin.getPosition().distanceSquared(cause.eyePosition()) > SELECT_RANGE * SELECT_RANGE) {
                // Beyond selection range.
                return AbilityTaskResult.end();
            }

            // Pretty!
            this.origin.getWorld().spawnParticles(PARTICLE_EFFECT, this.origin.getPosition());

            return AbilityTaskResult.repeat();
        }
    }

    /**
     * Handles when the player primary/left clicks to activate AirBlast.
     */
    private static final class Primary implements AbilityTaskExecutor {

        private static AbilityTask task(final ServerLocation origin, final Vector3d direction, final boolean canPushSelf) {
            return AbilityTask.repeating(Ticks.single(), () -> new Primary(origin, direction, canPushSelf));
        }

        private final boolean canPushSelf;
        private final Raycast raycast;

        private final Set<ServerLocation> affectedLocations = new HashSet<>();
        private final Set<UUID> affectedEntities = new HashSet<>();

        private Primary(final ServerLocation origin, final Vector3d direction, final boolean canPushSelf) {
            this.canPushSelf = canPushSelf;
            this.raycast = new Raycast(origin, direction, RANGE, SPEED, true);
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) {
            if (this.raycast.advance(current -> this.progress(cause, current))) {
                // If the ray moved forward, continue advancing.
                return AbilityTaskResult.repeat();
            } else {
                // Otherwise, end the ability now.
                return AbilityTaskResult.end();
            }
        }

        private boolean progress(final AbilityCause cause, final ServerLocation current) {
            this.raycast.affectLocations(this.affectedLocations, RADIUS, test -> {
                // TODO
                return true;
            });
            this.raycast.affectEntities(this.affectedEntities, RADIUS, test -> {
                // Ignore return value so we can push entities multiple times with the same ray.
                this.raycast.pushEntity(cause.cause(), test, this.canPushSelf, KNOCKBACK_SELF, KNOCKBACK_OTHER);

                return true;
            });

            // Pretty!
            current.getWorld().spawnParticles(PARTICLE_EFFECT, current.getPosition());

            if (Math.random() < 0.20) {
                // Add some sound every now and then.
                cause.audience().playSound(Sound.sound(SoundTypes.ENTITY_CREEPER_HURT, Sound.Source.PLAYER, 1.0f, 1.0f));
            }

            // End if the current block is solid or liquid.
            return !current.getOrElse(Keys.IS_SOLID, false) && current.getFluid().isEmpty();
        }
    }
}