package com.feroov.frv.world.placement;

import com.feroov.frv.STLCON;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class PlacementRegistry
{
    public static final PlacementModifierType<Surface> SURFACE_PLACEMENT = register(new ResourceLocation(STLCON.MOD_ID, "surface_placement"), Surface.CODEC);
    public static final PlacementModifierType<Underground> UNDERGROUND = register(new ResourceLocation(STLCON.MOD_ID, "underground"), Underground.CODEC);

    public static void init() { }

    private static <P extends PlacementModifier> PlacementModifierType<P> register(ResourceLocation name, Codec<P> codec) {
        return Registry.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, name, () -> codec);
    }
}