package com.expansemc.bending.api.bender;

import com.expansemc.bending.api.ability.execution.AbilityCause;
import com.expansemc.bending.api.ability.execution.AbilityContext;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Optional;

public interface Bender {

    static Bender player(final ServerPlayer player) {
        return Sponge.getGame().getFactoryProvider().provide(Factory.class).player(player);
    }

    static Bender entity(final Entity entity) {
        return Sponge.getGame().getFactoryProvider().provide(Factory.class).entity(entity);
    }

    Optional<AbilityContext> execute(AbilityCause cause);

    /**
     * A factory interface for creating {@link Bender}s.
     */
    interface Factory {

        Bender player(ServerPlayer player);

        Bender entity(Entity entity);
    }
}