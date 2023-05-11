package com.feroov.frv.world.dimension;

import com.feroov.frv.STLCON;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionsSTLCON
{
    public static final ResourceKey<Level> XENOSPHERE_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(STLCON.MOD_ID, "the_xenosphere"));

    public static final ResourceKey<DimensionType> CORRUPT_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, XENOSPHERE_KEY.registry());


    public static void register() {
        System.out.println("Registering ModDimensions for " + STLCON.MOD_ID);
    }
}
