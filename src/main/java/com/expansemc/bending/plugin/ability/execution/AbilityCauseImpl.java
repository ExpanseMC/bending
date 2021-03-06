package com.expansemc.bending.plugin.ability.execution;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityControl;
import com.expansemc.bending.api.ability.execution.AbilityCause;
import com.expansemc.bending.api.util.LocationUtil;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;

public final class AbilityCauseImpl implements AbilityCause {

    private final Ability ability;
    private final AbilityControl control;
    private final Cause cause;

    public AbilityCauseImpl(final Ability ability, final AbilityControl control, final Cause cause) {
        this.ability = ability;
        this.control = control;
        this.cause = cause;
    }

    @Override
    public Ability ability() {
        return this.ability;
    }

    @Override
    public AbilityControl control() {
        return this.control;
    }

    @Override
    public Cause cause() {
        return this.cause;
    }

    @Override
    public DataHolder.Mutable dataHolder() {
        final Optional<ServerPlayer> playerOpt = this.cause.first(ServerPlayer.class);
        if (playerOpt.isPresent()) {
            return playerOpt.get();
        }

        return this.cause.first(DataHolder.Mutable.class)
                .orElseThrow(() -> new RuntimeException("Cause has no data holder!"));
    }

    @Override
    public Subject subject() {
        return this.cause.first(Subject.class)
                .orElseGet(() -> Sponge.game().systemSubject());
    }

    @Override
    public Audience audience() {
        return this.cause.first(Audience.class)
                .orElseGet(() -> Sponge.game().systemSubject());
    }

    @Override
    public ServerLocation location() {
        return this.cause.first(Locatable.class)
                .map(Locatable::serverLocation)
                .orElseThrow(() -> new RuntimeException("Cause is not locatable!"));
    }

    @Override
    public ServerLocation targetLocation(final double maxDistance) {
        return LocationUtil.targetLocation(this.eyeLocation(), this.headDirection(), maxDistance);
    }

    @Override
    public ServerLocation eyeLocation() {
        return this.cause.first(Living.class)
                .map(living -> living.serverLocation().withPosition(living.eyePosition().get()))
                .or(() -> this.cause.first(Locatable.class)
                        .map(Locatable::serverLocation))
                .orElseThrow(() -> new RuntimeException("Cause is not locatable!"));
    }

    @Override
    public Vector3d eyePosition() {
        return this.cause.first(Living.class)
                .map(living -> living.eyePosition().get())
                .or(() -> this.cause.first(Locatable.class)
                        .map(loc -> loc.serverLocation().position()))
                .orElseThrow(() -> new RuntimeException("Cause is not locatable!"));
    }

    @Override
    public Vector3d headDirection() {
        return this.cause.first(Living.class)
                .map(Living::headDirection)
                .or(() -> this.cause.first(Entity.class)
                        .map(Entity::direction))
                .orElseThrow(() -> new RuntimeException("Cause has no direction!"));
    }

    @Override
    public boolean isValid() {
        return this.cause.first(Entity.class)
                .map(entity -> !entity.isRemoved() && entity.isLoaded())
                .orElse(true);
    }
}