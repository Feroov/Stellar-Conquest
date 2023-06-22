package com.feroov.frv.events;


import com.feroov.frv.STLCON;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.monster.Celestroid;
import com.feroov.frv.entity.monster.Mothership;
import com.feroov.frv.entity.monster.Xenaptor;
import com.feroov.frv.entity.monster.renderer.XenaptorRenderer;
import com.feroov.frv.entity.passive.Wispxen;
import com.feroov.frv.entity.passive.Xeron;
import com.feroov.frv.entity.neutral.XeronGuard;
import com.feroov.frv.entity.monster.renderer.CelestroidRenderer;
import com.feroov.frv.entity.monster.renderer.MothershipRenderer;
import com.feroov.frv.entity.neutral.renderer.XeronGuardRenderer;
import com.feroov.frv.entity.passive.Zephxen;
import com.feroov.frv.entity.passive.renderer.WispxenRenderer;
import com.feroov.frv.entity.passive.renderer.XeronRenderer;
import com.feroov.frv.entity.passive.renderer.ZephxenRenderer;
import com.feroov.frv.entity.projectile.renderer.CelestroidBeamRenderer;
import com.feroov.frv.entity.projectile.renderer.MothershipBeamRenderer;
import com.feroov.frv.entity.projectile.renderer.RaygunBeamRenderer;
import com.feroov.frv.particles.HeartParticles;
import com.feroov.frv.particles.XenospherePortalParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = STLCON.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEvents
{

    public ModEvents(){}

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event)
    {
        event.put(EntitiesSTLCON.CELESTROID.get(), Celestroid.setAttributes());
        event.put(EntitiesSTLCON.MOTHERSHIP.get(), Mothership.setAttributes());
        event.put(EntitiesSTLCON.XERON.get(), Xeron.setAttributes());
        event.put(EntitiesSTLCON.XERON_GUARD.get(), XeronGuard.setAttributes());
        event.put(EntitiesSTLCON.WISPXEN.get(), Wispxen.setAttributes());
        event.put(EntitiesSTLCON.ZEPHXEN.get(), Zephxen.setAttributes());
        event.put(EntitiesSTLCON.XENAPTOR.get(), Xenaptor.setAttributes());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(EntitiesSTLCON.CELESTROID.get(), CelestroidRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.MOTHERSHIP.get(), MothershipRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.XERON.get(), XeronRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.XERON_GUARD.get(), XeronGuardRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.WISPXEN.get(), WispxenRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.ZEPHXEN.get(), ZephxenRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.XENAPTOR.get(), XenaptorRenderer::new);

        event.registerEntityRenderer(EntitiesSTLCON.RAYGUN_BEAM.get(), RaygunBeamRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.CELESTROID_BEAM.get(), CelestroidBeamRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.MOTHERSHIP_BEAM.get(), MothershipBeamRenderer::new);
    }

    @SubscribeEvent
    public static void entitySpawnRestriction(SpawnPlacementRegisterEvent event)
    {
        event.register(EntitiesSTLCON.XERON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE,
                Mob::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);

        event.register(EntitiesSTLCON.WISPXEN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE,
                Mob::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);

        event.register(EntitiesSTLCON.ZEPHXEN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);

        event.register(EntitiesSTLCON.XENAPTOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE,
                Mob::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event)
    {
        Minecraft.getInstance().particleEngine.register(ModParticles.HEART_PARTICLES.get(), HeartParticles.Provider::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.XENOSPHERE_PORTAL_PARTICLES.get(), XenospherePortalParticles.Provider::new);
    }
}
