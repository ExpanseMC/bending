package com.expansemc.bending.plugin.bender;

import com.expansemc.bending.api.bender.Bender;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import java.util.Objects;
import java.util.UUID;

public final class BenderFactory implements Bender.Factory {

    public static final BenderFactory INSTANCE = new BenderFactory();

    private final Cache<UUID, BenderImpl> playerCache = Caffeine.newBuilder().build();

    private BenderFactory() {
    }

    @Override
    public Bender player(final ServerPlayer player) {
        //noinspection ConstantConditions
        return Objects.requireNonNull(this.playerCache.get(player.uniqueId(), BenderImpl::new));
    }

    @Override
    public Bender entity(final Entity entity) {
        if (entity instanceof ServerPlayer) {
            return this.player((ServerPlayer) entity);
        }

        throw new UnsupportedOperationException("TODO");
    }

    @Listener
    public void onDisconnect(final ServerSideConnectionEvent.Disconnect event) {
        final @Nullable BenderImpl bender = this.playerCache.getIfPresent(event.player().uniqueId());
        this.playerCache.invalidate(event.player().uniqueId());

        if (bender != null) {
            bender.cancelAll();
        }
    }
}