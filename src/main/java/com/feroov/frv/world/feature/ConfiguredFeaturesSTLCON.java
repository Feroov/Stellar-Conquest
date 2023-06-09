package com.feroov.frv.world.feature;

import com.feroov.frv.STLCON;
import com.feroov.frv.block.BlocksSTLCON;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;


public class ConfiguredFeaturesSTLCON
{
    public static final ResourceKey<ConfiguredFeature<?, ?>> XENOS_KEY = registerKey("xenos_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> XENITE_ORE_KEY = registerKey("xenite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ASTRALITE_ORE_KEY = registerKey("astralite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> USKIUM_ORE_KEY = registerKey("uskium_ore");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context)
    {
        RuleTest xenostoneReplaceables = new BlockMatchTest(BlocksSTLCON.XENOSTONE.get());

        // Trees
        register(context, XENOS_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(BlocksSTLCON.XENOS_LOG.get()),
                new StraightTrunkPlacer(4, 3, 0),
                BlockStateProvider.simple(BlocksSTLCON.XENOS_LEAVES.get()),
                new LeafSpheroidFoliagePlacer(1.5f, 2.25f, ConstantInt.of(0), 1, 1, 0.5f, 0),
                new TwoLayersFeatureSize(2, 0, 2)).build());

        // Ores
        register(context, USKIUM_ORE_KEY, Feature.ORE, new OreConfiguration(xenostoneReplaceables,
                BlocksSTLCON.USKIUM_ORE.get().defaultBlockState(), 13));

        register(context, XENITE_ORE_KEY, Feature.ORE, new OreConfiguration(xenostoneReplaceables,
                BlocksSTLCON.XENITE_ORE.get().defaultBlockState(), 9));

        register(context, ASTRALITE_ORE_KEY, Feature.ORE, new OreConfiguration(xenostoneReplaceables,
                BlocksSTLCON.ASTRALITE_ORE.get().defaultBlockState(), 4));
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) { return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(STLCON.MOD_ID, name)); }
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) { context.register(key, new ConfiguredFeature<>(feature, configuration));}
}
