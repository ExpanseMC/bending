package com.expansemc.bending.classic.ability.config;

import com.expansemc.bending.api.util.VectorUtil;
import com.expansemc.bending.classic.ability.AirAbilities;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.math.vector.Vector3d;

import java.time.Duration;
import java.util.List;

@ConfigSerializable
public final class AirBurstConfig {

    public static final AirBurstConfig DEFAULT = new AirBurstConfig();

    @Setting private long chargeTime = 1750;
    @Setting private double blastRadius = 1.0;
    @Setting private double knockback = 2.8;
    @Setting private double range = 20.0;
    @Setting private double speed = 25.0;
    @Setting private double fallThreshold = 10.0;
    @Setting private int angleTheta = 10;
    @Setting private int anglePhi = 10;
    @Setting private int maxConeDegrees = 30;
    @Setting private int numChargingParticles = 4;
    @Setting private int numParticlesPerRay = 2;

    private @Nullable List<Vector3d> fallDirections = null;
    private @Nullable List<Vector3d> sphereDirections = null;

    private @Nullable ParticleEffect chargedParticle = null;
    private @Nullable ParticleEffect chargingParticle = null;
    private @Nullable ParticleEffect rayParticle = null;

    public final long chargeTime() {
        return this.chargeTime;
    }

    public final Duration chargeDuration() {
        return Duration.ofMillis(this.chargeTime());
    }

    public final double blastRadius() {
        return this.blastRadius;
    }

    public final double knockback() {
        return this.knockback;
    }

    public final double range() {
        return this.range;
    }

    public final double speed() {
        return this.speed;
    }

    public final double fallThreshold() {
        return this.fallThreshold;
    }

    public final int angleTheta() {
        return this.angleTheta;
    }

    public final int anglePhi() {
        return this.anglePhi;
    }

    public final int maxConeDegrees() {
        return this.maxConeDegrees;
    }

    public final double maxConeRadians() {
        return Math.toRadians(this.maxConeDegrees());
    }

    public final List<Vector3d> fallDirections() {
        if (this.fallDirections == null) {
            this.fallDirections = VectorUtil.sphereDirections(75, 105, this.angleTheta(), this.anglePhi());
        }
        return this.fallDirections;
    }

    public final List<Vector3d> sphereDirections() {
        if (this.sphereDirections == null) {
            this.sphereDirections = VectorUtil.sphereDirections(0, 180, this.angleTheta(), this.anglePhi());
        }
        return this.sphereDirections;
    }

    public final ParticleEffect chargedParticle() {
        if (this.chargedParticle == null) {
            this.chargedParticle = ParticleEffect.builder()
                    .type(ParticleTypes.SMOKE)
                    .quantity(this.numChargingParticles)
                    .offset(VectorUtil.VECTOR_0_275)
                    .build();
        }
        return this.chargedParticle;
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
}