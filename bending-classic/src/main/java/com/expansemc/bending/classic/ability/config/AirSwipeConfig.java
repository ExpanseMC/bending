package com.expansemc.bending.classic.ability.config;

import com.expansemc.bending.api.util.VectorUtil;
import com.expansemc.bending.classic.ability.AirAbilities;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.math.matrix.Matrix3d;

import java.time.Duration;
import java.util.List;

@ConfigSerializable
public final class AirSwipeConfig {

    public static final AirSwipeConfig DEFAULT = new AirSwipeConfig();

    @Setting private long chargeTime = 2500;
    @Setting private double maxChargeFactor = 3.0;
    @Setting private int arcDegrees = 16;
    @Setting private int arcIncrementDegrees = 4;
    @Setting private double damage = 2.0;
    @Setting private double knockback = 0.5;
    @Setting private double radius = 2.0;
    @Setting private double range = 14.0;
    @Setting private double speed = 25.0;
    @Setting private int numParticlesPerRay = 3;
    @Setting private int numChargingParticles = 4;

    private @Nullable List<Matrix3d> transforms = null;

    private @Nullable ParticleEffect rayParticle = null;
    private @Nullable ParticleEffect chargingParticle = null;

    public final long chargeTime() {
        return this.chargeTime;
    }

    public final Duration chargeDuration() {
        return Duration.ofMillis(this.chargeTime);
    }

    public final double maxChargeFactor() {
        return this.maxChargeFactor;
    }

    public final int arcDegrees() {
        return this.arcDegrees;
    }

    public final int arcIncrementDegrees() {
        return this.arcIncrementDegrees;
    }

    public final double damage() {
        return this.damage;
    }

    public final double knockback() {
        return this.knockback;
    }

    public final double radius() {
        return this.radius;
    }

    public final double range() {
        return this.range;
    }

    public final double speed() {
        return this.speed;
    }

    public final int numParticlesPerRay() {
        return this.numParticlesPerRay;
    }

    public final int numChargingParticles() {
        return this.numChargingParticles;
    }

    public final List<Matrix3d> transforms() {
        if (this.transforms == null) {
            this.transforms = VectorUtil.arcMatrices(this.arcDegrees, this.arcIncrementDegrees);
        }
        return this.transforms;
    }

    public final ParticleEffect rayParticle() {
        if (this.rayParticle == null) {
            this.rayParticle = ParticleEffect.builder()
                    .type(AirAbilities.PARTICLE_TYPE)
                    .quantity(this.numParticlesPerRay)
                    .offset(VectorUtil.VECTOR_0_275)
                    .build();
        }
        return this.rayParticle;
    }

    public final ParticleEffect chargingParticle() {
        if (this.chargingParticle == null) {
            this.chargingParticle = ParticleEffect.builder()
                    .type(AirAbilities.PARTICLE_TYPE)
                    .quantity(this.numChargingParticles)
                    .offset(VectorUtil.VECTOR_0_275)
                    .build();
        }
        return this.chargingParticle;
    }
}