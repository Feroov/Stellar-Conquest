package com.feroov.frv.world.dimension;

import com.feroov.frv.STLCON;
import com.feroov.frv.world.dimension.sky.XenosphereSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = STLCON.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DimensionsSTLCON
{
    public static final ResourceKey<Level> XENOSPHERE_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(STLCON.MOD_ID, "the_xenosphere"));

    public static final ResourceKey<DimensionType> XENOSPHERE_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, XENOSPHERE_KEY.registry());


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(new ResourceLocation(STLCON.MOD_ID, "xenosphere_sky"), new XenosphereSky());
    }

    public static void register() { System.out.println("Registering DimensionsSTLCON for " + STLCON.MOD_ID); }
}
