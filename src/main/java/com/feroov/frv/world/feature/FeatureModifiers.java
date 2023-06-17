package com.feroov.frv.world.feature;

import com.feroov.frv.STLCON;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class FeatureModifiers
{
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS = DeferredRegister.create(ForgeRegistries.FOLIAGE_PLACER_TYPES, STLCON.MOD_ID);

    public static final RegistryObject<FoliagePlacerType<LeafSpheroidFoliagePlacer>> FOLIAGE_SPHEROID =
            FOLIAGE_PLACERS.register("spheroid_foliage_placer", () -> new FoliagePlacerType<>(LeafSpheroidFoliagePlacer.CODEC));
}
