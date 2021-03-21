package com.expansemc.bending.classic.ability.air;

import com.expansemc.bending.api.Bending;
import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityCategories;
import com.expansemc.bending.api.ability.AbilityControls;
import com.expansemc.bending.api.data.BendingKeys;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import java.util.List;
import java.util.Set;

public final class AirAgilityAbility {

    public static final Ability ABILITY = Ability.builder()
            .name(Component.text("AirAgility"))
            .category(AbilityCategories.AIR)
            .addControls(AbilityControls.PASSIVE.get())
            .build();

    private static final List<PotionEffect> EFFECTS = List.of(
            PotionEffect.builder()
                    .potionType(PotionEffectTypes.JUMP_BOOST)
                    .amplifier(3)
                    .duration(100)
                    .showParticles(false)
                    .build(),
            PotionEffect.builder()
                    .potionType(PotionEffectTypes.SPEED)
                    .amplifier(2)
                    .duration(100)
                    .showParticles(false)
                    .build()
    );

    // TODO: switch to ChangeDataHolderEvent.ValueChange when it works for SPRINTING again
    public static final Task TASK = Task.builder()
            .plugin(Bending.get().implementation())
            .interval(Ticks.single())
            .execute(() -> {
                for (final ServerPlayer player : Sponge.server().onlinePlayers()) {
                    if (!player.getOrElse(BendingKeys.PASSIVE_ABILITIES, Set.of()).contains(ABILITY)){
                        continue;
                    }

                    if (player.getOrElse(Keys.IS_SPRINTING, false)) {
                        player.offerAll(Keys.POTION_EFFECTS, EFFECTS);
                    }
                }
            })
            .build();
}