package com.feroov.frv.events;

import com.feroov.frv.STLCON;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, STLCON.MOD_ID);

    public static final RegistryObject<SimpleParticleType> HEART_PARTICLES = PARTICLE_TYPES.register("heart_particles", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> XENOSPHERE_PORTAL_PARTICLES = PARTICLE_TYPES.register("xenosphere_portal_particles", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> RAYGUN_PARTICLES = PARTICLE_TYPES.register("raygun_particles", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BANNED_PARTICLES = PARTICLE_TYPES.register("banned_particles", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) { PARTICLE_TYPES.register(eventBus); }
}
