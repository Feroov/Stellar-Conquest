package com.feroov.frv.events;


import com.feroov.frv.STLCON;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.monster.renderer.CelestroidRenderer;
import com.feroov.frv.entity.monster.renderer.MothershipRenderer;
import com.feroov.frv.entity.projectile.renderer.CelestroidBeamRenderer;
import com.feroov.frv.entity.projectile.renderer.MothershipBeamRenderer;
import com.feroov.frv.entity.projectile.renderer.RaygunBeamRenderer;
import com.feroov.frv.particles.HeartParticles;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = STLCON.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents
{
    public ClientModEvents(){}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(EntitiesSTLCON.CELESTROID.get(), CelestroidRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.MOTHERSHIP.get(), MothershipRenderer::new);

        event.registerEntityRenderer(EntitiesSTLCON.RAYGUN_BEAM.get(), RaygunBeamRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.CELESTROID_BEAM.get(), CelestroidBeamRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.MOTHERSHIP_BEAM.get(), MothershipBeamRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event)
    {
        Minecraft.getInstance().particleEngine.register(ModParticles.HEART_PARTICLES.get(), HeartParticles.Provider::new);
    }
}
