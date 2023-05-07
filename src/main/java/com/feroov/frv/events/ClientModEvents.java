package com.feroov.frv.events;


import com.feroov.frv.STLCON;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.monster.renderer.CelestroidRenderer;
import com.feroov.frv.entity.projectile.renderer.CelestroidBeamRenderer;
import com.feroov.frv.entity.projectile.renderer.RaygunBeamRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
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

        event.registerEntityRenderer(EntitiesSTLCON.RAYGUN_BEAM.get(), RaygunBeamRenderer::new);
        event.registerEntityRenderer(EntitiesSTLCON.CELESTROID_BEAM.get(), CelestroidBeamRenderer::new);
    }
}
