package com.expansemc.bending.classic.ability;

import net.kyori.adventure.sound.Sound;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;

public final class AirAbilities {

    public static final Sound SOUND = Sound.sound(SoundTypes.ENTITY_WITCH_THROW, Sound.Source.PLAYER, 1.0f, 0.0f);

    public static final ParticleType PARTICLE_TYPE = ParticleTypes.CLOUD.get();

    private AirAbilities() {
    }
}