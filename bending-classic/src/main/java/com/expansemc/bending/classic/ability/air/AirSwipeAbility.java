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
import com.expansemc.bending.classic.ability.AirAbilities;
import com.expansemc.bending.classic.ability.config.AirSwipeConfig;
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

import static com.expansemc.bending.api.ability.AbilityControls.PRIMARY;
import static com.expansemc.bending.api.ability.AbilityControls.SNEAK;

public final class AirSwipeAbility {

    public static final Ability ABILITY = Ability.builder()
            .name(Component.text("AirSwipe"))
            .category(AbilityCategories.AIR)
            .executor(AbilityTask.immediate(() -> new Start(AirSwipeConfig.DEFAULT)))
            .addControls(PRIMARY.get(), AbilityControls.SNEAK.get())
            .build();

    //////////////////////////////////////////////////
    // Ability Start
    //////////////////////////////////////////////////

    private static final class Start implements AbilityTaskExecutor {

        private final AirSwipeConfig config;

        public Start(final AirSwipeConfig config) {
            this.config = config;
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) throws AbilityException {
            if (cause.control() == PRIMARY.get()) {
                // Run swipe task.
                final AbilityTask task = swipe(
                        this.config,
                        this.config.damage(), this.config.knockback(),
                        cause.eyeLocation(), cause.headDirection()
                );
                return AbilityTaskResult.next(task);
            } else if (cause.control() == SNEAK.get()) {
                // Run charging task.
                return AbilityTaskResult.next(charging(this.config));
            } else {
                throw new AbilityException(AbilityUtil.validControlsError(ABILITY));
            }
        }
    }

    //////////////////////////////////////////////////
    // Ability Charging
    //////////////////////////////////////////////////

    private static AbilityTask charging(final AirSwipeConfig config) {
        return AbilityTask.repeating(Ticks.single(), () -> new Charging(config));
    }

    private static final class Charging implements AbilityTaskExecutor {

        private final AirSwipeConfig config;
        private final Instant startTime;

        private boolean fullyCharged = false;

        public Charging(final AirSwipeConfig config) {
            this.config = config;
            this.startTime = Instant.now();
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) throws AbilityException {
            final Duration elapsed = Duration.between(this.startTime, Instant.now());

            if (elapsed.compareTo(this.config.chargeDuration()) >= 0) {
                // If enough time has passed, we are fully charged.
                this.fullyCharged = true;
            }

            if (!cause.dataHolder().getOrElse(Keys.IS_SNEAKING, false)) {
                double chargeFactor = this.config.maxChargeFactor();
                if (!this.fullyCharged) {
                    // Decrease the effect of the swipe if not fully charged.
                    chargeFactor = this.config.maxChargeFactor() * elapsed.toMillis() / this.config.chargeTime();
                }

                // Start swiping.
                return AbilityTaskResult.next(swipe(
                        this.config,
                        this.config.damage() * chargeFactor, this.config.knockback() * chargeFactor,
                        cause.location(), cause.headDirection())
                );
            }

            if (this.fullyCharged) {
                // Let the player know they have fully charged the ability by showing particles.
                final ServerLocation location = cause.eyeLocation();
                location.getWorld().spawnParticles(this.config.chargingParticle(), location.getPosition());
            }

            return AbilityTaskResult.repeat();
        }
    }

    //////////////////////////////////////////////////
    // Ability Swipe
    //////////////////////////////////////////////////

    private static AbilityTask swipe(final AirSwipeConfig config,
                                     final double damage, final double knockback,
                                     final ServerLocation origin, final Vector3d direction) {
        return AbilityTask.repeating(Ticks.single(), () -> new Swipe(config, damage, knockback, origin, direction));
    }

    private static final class Swipe implements AbilityTaskExecutor {

        private final AirSwipeConfig config;
        private final double damage;
        private final double knockback;

        private final List<Raycast> raycasts;
        private final Set<UUID> affectedEntities;

        public Swipe(final AirSwipeConfig config, final double damage, final double knockback,
                     final ServerLocation origin, final Vector3d direction) {
            this.config = config;
            this.damage = damage;
            this.knockback = knockback;

            this.raycasts = VectorUtil.arcRaycasts(origin, direction, this.config.transforms(), this.config.range(), this.config.speed());
            this.affectedEntities = new HashSet<>();
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) throws AbilityException {
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

            raycast.affectEntities(this.affectedEntities, this.config.radius(), test -> {
                // Ignore return value so we can push entities multiple times with the same ray.
                raycast.pushEntity(cause.cause(), test, false, 0.0, this.knockback);

                return raycast.damageEntity(cause.cause(), test, this.damage);
            });

            // Pretty!
            current.getWorld().spawnParticles(this.config.rayParticle(), current.getPosition());

            if (Math.random() < 0.15) {
                // Add some sound every now and then.
                cause.audience().playSound(AirAbilities.SOUND);
            }

            return true;
        }
    }
}