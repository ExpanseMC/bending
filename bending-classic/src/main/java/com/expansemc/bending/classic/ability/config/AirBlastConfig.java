package com.expansemc.bending.classic.ability.config;

import com.expansemc.bending.api.util.VectorUtil;
import com.expansemc.bending.classic.ability.AirAbilities;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public final class AirBlastConfig {

    public static final AirBlastConfig DEFAULT = new AirBlastConfig();

    @Setting private double knockbackOther = 1.6;
    @Setting private double knockbackSelf = 2.0;
    @Setting private double radius = 0.5;
    @Setting private double range = 20.0;
    @Setting private double selectRange = 10.0;
    @Setting private double speed = 25.0;
    @Setting private int numRayParticles = 4;

    private @Nullable ParticleEffect rayParticle = null;

    public final double knockbackOther() {
        return this.knockbackOther;
    }

    public final double knockbackSelf() {
        return this.knockbackSelf;
    }

    public final double radius() {
        return this.radius;
    }

    public final double range() {
        return this.range;
    }

    public final double selectRange() {
        return this.selectRange;
    }

    public final double selectRangeSquaredPadded() {
        return 2.25 * this.selectRange * this.selectRange;
    }

    public final double speed() {
        return this.speed;
    }

    public final ParticleEffect rayParticle() {
        if (this.rayParticle == null) {
            this.rayParticle = ParticleEffect.builder()
                    .type(AirAbilities.PARTICLE_TYPE)
                    .quantity(this.numRayParticles)
                    .offset(VectorUtil.VECTOR_0_275)
                    .build();
        }
        return this.rayParticle;
    }
}