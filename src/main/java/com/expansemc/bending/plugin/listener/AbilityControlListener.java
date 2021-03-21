package com.expansemc.bending.plugin.listener;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityControls;
import com.expansemc.bending.api.ability.execution.AbilityCause;
import com.expansemc.bending.api.bender.Bender;
import com.expansemc.bending.api.data.BendingKeys;
import com.expansemc.bending.api.event.BendingEventContextKeys;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class AbilityControlListener {

    public static final EventListener<ChangeDataHolderEvent.ValueChange> ON_SNEAK = event -> {
        final ServerPlayer player = event.cause().first(ServerPlayer.class).get();

        final @Nullable Boolean isSneaking = getData(Keys.IS_SNEAKING, event.endResult().successfulData());
        if (!Objects.requireNonNullElse(isSneaking, false)) {
            // Only handle sneaking.
            return;
        }

        currentAbility(player).ifPresent(ability -> {
            player.sendActionBar(Component.text("SNEAK"));

            final AbilityCause cause = AbilityCause.of(ability, AbilityControls.SNEAK.get(), event.cause());
            final Bender bender = Bender.player(player);

            bender.execute(cause);
        });
    };

    @Listener
    public void onFall(final DamageEntityEvent event,
                       @First final DamageSource source,
                       @Getter("entity") final ServerPlayer player) {
        if (source.type() != DamageTypes.FALL.get()) {
            return;
        }

        try (final CauseStackManager.StackFrame frame = Sponge.server().causeStackManager().pushCauseFrame()) {
            frame.addContext(BendingEventContextKeys.FALL_DISTANCE, player.fallDistance().get());

            currentAbility(player).ifPresent(ability -> {
                player.sendActionBar(Component.text("FALL"));

                final AbilityCause cause = AbilityCause.of(ability, AbilityControls.FALL.get(), frame.currentCause());
                final Bender bender = Bender.player(player);

                bender.execute(cause);
            });
        }
    }

    @Listener
    public void onInteractItemPrimary(final InteractItemEvent.Primary event,
                          @First final ServerPlayer player) {
        currentAbility(player).ifPresent(ability -> {
            player.sendActionBar(Component.text("PRIMARY-item"));

            final AbilityCause cause = AbilityCause.of(ability, AbilityControls.PRIMARY.get(), event.cause());
            final Bender bender = Bender.player(player);

            bender.execute(cause);
        });
    }

    @Listener
    public void onInteractEntityPrimary(final InteractEntityEvent.Primary event,
                                        @First final ServerPlayer player) {
        currentAbility(player).ifPresent(ability -> {
            player.sendActionBar(Component.text("PRIMARY-entity"));

            final AbilityCause cause = AbilityCause.of(ability, AbilityControls.PRIMARY.get(), event.cause());
            final Bender bender = Bender.player(player);

            bender.execute(cause);
        });
    }

    @Listener
    public void onInteractItemSecondary(final InteractItemEvent.Secondary event,
                            @First final ServerPlayer player) {
        currentAbility(player).ifPresent(ability -> {
            player.sendActionBar(Component.text("SECONDARY"));

            final AbilityCause cause = AbilityCause.of(ability, AbilityControls.SECONDARY.get(), event.cause());
            final Bender bender = Bender.player(player);

            bender.execute(cause);
        });
    }

    @Listener
    public void onSneak(final ChangeDataHolderEvent.ValueChange event,
                        @Getter("targetHolder") final ServerPlayer player) {
        final @Nullable Boolean isSneaking = getData(Keys.IS_SNEAKING, event.endResult().successfulData());
        if (!Objects.requireNonNullElse(isSneaking, false)) {
            // Only handle sneaking.
            return;
        }

        currentAbility(player).ifPresent(ability -> {
            player.sendActionBar(Component.text("SNEAK"));

            final AbilityCause cause = AbilityCause.of(ability, AbilityControls.SNEAK.get(), event.cause());
            final Bender bender = Bender.player(player);

            bender.execute(cause);
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> @Nullable T getData(final Key<Value<T>> key, final List<Value.Immutable<?>> values) {
        for (final Value.Immutable<?> value : values) {
            if (value.key() == key) {
                return (T) value.get();
            }
        }
        return null;
    }

    private static Optional<Ability> currentAbility(final ServerPlayer player) {
        final int selectedSlotIndex = player.inventory().hotbar().selectedSlotIndex();
        final Ability ability = player.getOrElse(BendingKeys.ABILITY_HOTBAR, Map.of())
                .get(selectedSlotIndex);
        return Optional.ofNullable(ability);
    }
}