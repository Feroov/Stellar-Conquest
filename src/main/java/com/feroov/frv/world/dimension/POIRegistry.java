package com.feroov.frv.world.dimension;

import com.feroov.frv.STLCON;
import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class POIRegistry
{
    public static final DeferredRegister<PoiType> POI = DeferredRegister.create(ForgeRegistries.POI_TYPES, STLCON.MOD_ID);

    public static final RegistryObject<PoiType> XENOSPHERE_PORTAL = POI.register("xenosphere_portal", () ->
            new PoiType(ImmutableSet.copyOf(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(STLCON.MOD_ID, "xenosphere_portal"))
                    .getStateDefinition().getPossibleStates()), 0, 1));
}
