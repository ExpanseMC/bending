package com.expansemc.bending.classic.ability.air;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityCategories;
import com.expansemc.bending.api.ability.AbilityException;
import com.expansemc.bending.api.ability.execution.AbilityCause;
import com.expansemc.bending.api.ability.task.AbilityTask;
import com.expansemc.bending.api.ability.task.AbilityTaskExecutor;
import com.expansemc.bending.api.ability.task.AbilityTaskResult;
import com.expansemc.bending.api.ray.Raycast;
import com.expansemc.bending.classic.ability.AirAbilities;
import com.expansemc.bending.classic.ability.config.AirBlastConfig;
import com.expansemc.bending.classic.util.AbilityUtil;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.expansemc.bending.api.ability.AbilityControls.PRIMARY;
import static com.expansemc.bending.api.ability.AbilityControls.SNEAK;

public final class AirBlastAbility {

    public static final Ability ABILITY = Ability.builder()
            .name(Component.text("AirBlast"))
            .category(AbilityCategories.AIR)
            .executor(AbilityTask.immediate(() -> new Start(AirBlastConfig.DEFAULT)))
            .addControls(PRIMARY.get(), SNEAK.get())
            .build();

    //////////////////////////////////////////////////
    // Ability Start
    //////////////////////////////////////////////////

    private static final class Start implements AbilityTaskExecutor {

        private final AirBlastConfig config;

        public Start(final AirBlastConfig config) {
            this.config = config;
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) throws AbilityException {
            if (cause.control() == SNEAK.get()) {
                // Run the sneak task.
                final ServerLocation origin = cause.targetLocation(this.config.selectRange());
                final Vector3d direction = cause.headDirection();

                return AbilityTaskResult.next(sneak(this.config, origin, direction));
            } else if (cause.control() == PRIMARY.get()) {
                // Run the primary task.
                final ServerLocation origin = cause.eyeLocation();
                final Vector3d direction = cause.headDirection();

                if (!origin.getFluid().isEmpty()) {
                    // End immediately if in a liquid.
                    return AbilityTaskResult.end();
                } else {
                    return AbilityTaskResult.next(blast(this.config, origin, direction, false));
                }
            } else {
                throw new AbilityException(AbilityUtil.validControlsError(ABILITY));
            }
        }
    }

    //////////////////////////////////////////////////
    // Ability Sneaking
    //////////////////////////////////////////////////

    private static AbilityTask sneak(final AirBlastConfig config, final ServerLocation origin, final Vector3d direction) {
        return AbilityTask.repeatingUntil(
                PRIMARY.get(), blast(config, origin, direction, true),
                Ticks.single(), () -> new Sneak(config, origin)
        );
    }

    private static final class Sneak implements AbilityTaskExecutor {

        private final AirBlastConfig config;
        private final ServerLocation origin;

        public Sneak(final AirBlastConfig config, final ServerLocation origin) {
            this.config = config;
            this.origin = origin;
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) {
            if (this.origin.getPosition().distanceSquared(cause.eyePosition()) > this.config.selectRangeSquaredPadded()) {
                // Beyond selection range.
                return AbilityTaskResult.end();
            }

            // Pretty!
            this.origin.getWorld().spawnParticles(this.config.rayParticle(), this.origin.getPosition());

            return AbilityTaskResult.repeat();
        }
    }

    //////////////////////////////////////////////////
    // Ability Blast
    //////////////////////////////////////////////////

    private static AbilityTask blast(final AirBlastConfig config,
                                     final ServerLocation origin, final Vector3d direction,
                                     final boolean canPushSelf) {
        return AbilityTask.repeating(Ticks.single(), () -> new Blast(config, origin, direction, canPushSelf));
    }

    private static final class Blast implements AbilityTaskExecutor {

        private final AirBlastConfig config;
        private final boolean canPushSelf;
        private final Raycast raycast;

        private final Set<ServerLocation> affectedLocations = new HashSet<>();
        private final Set<UUID> affectedEntities = new HashSet<>();

        private Blast(final AirBlastConfig config,
                      final ServerLocation origin, final Vector3d direction,
                      final boolean canPushSelf) {
            this.config = config;
            this.canPushSelf = canPushSelf;
            this.raycast = new Raycast(origin, direction, this.config.range(), this.config.speed(), true);
        }

        @Override
        public AbilityTaskResult execute(final AbilityCause cause) {
            if (this.raycast.advance((raycast, current) -> this.progress(raycast, current, cause))) {
                // If the ray moved forward, continue advancing.
                return AbilityTaskResult.repeat();
            } else {
                // Otherwise, end the ability now.
                return AbilityTaskResult.end();
            }
        }

        private boolean progress(final Raycast raycast, final ServerLocation current, final AbilityCause cause) {
            raycast.affectLocations(this.affectedLocations, this.config.radius(), test ->
//                    AirRaycast.extinguishFlames(test) || AirRaycast.toggleOpenable(test) || AirRaycast.togglePowerable(test));
                    false); // TODO

            raycast.affectEntities(this.affectedEntities, this.config.radius(), test -> {
                // Ignore return value so we can push entities multiple times with the same ray.
                raycast.pushEntity(cause.cause(), test, this.canPushSelf, this.config.knockbackSelf(), this.config.knockbackOther());

                return true;
            });

            // Pretty!
            current.getWorld().spawnParticles(this.config.rayParticle(), current.getPosition());

            if (Math.random() < 0.20) {
                // Add some sound every now and then.
                cause.audience().playSound(AirAbilities.SOUND);
            }

            // End if the current block is solid or liquid.
            return !current.getOrElse(Keys.IS_SOLID, false) && current.getFluid().isEmpty();
        }
    }
}