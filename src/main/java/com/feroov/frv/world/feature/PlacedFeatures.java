package com.feroov.frv.world.feature;

import com.feroov.frv.STLCON;
import com.feroov.frv.block.BlocksSTLCON;
import com.feroov.frv.world.placement.OrePlacementSTLCON;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public class PlacedFeatures
{
    public static final ResourceKey<PlacedFeature> XENOS_PLACED_KEY = createKey("xenos_placed");
    public static final ResourceKey<PlacedFeature> XENITE_PLACED_KEY = createKey("xenite_placed");
    public static final ResourceKey<PlacedFeature> ASTRALITE_PLACED_KEY = createKey("astralite_placed");
    public static final ResourceKey<PlacedFeature> USKIUM_PLACED_KEY = createKey("uskium_placed");



    public static void bootstrap(BootstapContext<PlacedFeature> context)
    {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, XENOS_PLACED_KEY, configuredFeatures.getOrThrow(ConfiguredFeaturesSTLCON.XENOS_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(55, 0.1f, 55), BlocksSTLCON.XENOS_SAPLING.get()));

        register(context, XENITE_PLACED_KEY, configuredFeatures.getOrThrow(ConfiguredFeaturesSTLCON.XENITE_ORE_KEY),
                OrePlacementSTLCON.commonOrePlacement(9, // veins per chunk
                        HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-64), VerticalAnchor.absolute(80))));

        register(context, ASTRALITE_PLACED_KEY, configuredFeatures.getOrThrow(ConfiguredFeaturesSTLCON.ASTRALITE_ORE_KEY),
                OrePlacementSTLCON.commonOrePlacement(4, // veins per chunk
                        HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-64), VerticalAnchor.absolute(-43))));

        register(context, USKIUM_PLACED_KEY, configuredFeatures.getOrThrow(ConfiguredFeaturesSTLCON.USKIUM_ORE_KEY),
                OrePlacementSTLCON.commonOrePlacement(4, // veins per chunk
                        HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-64), VerticalAnchor.absolute(110))));
    }

    private static ResourceKey<PlacedFeature> createKey(String name)
    {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(STLCON.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers)
    {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
