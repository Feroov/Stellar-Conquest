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
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public class ConfiguredFeaturesSTLCON
{
    public static final ResourceKey<ConfiguredFeature<?, ?>> XENOS_KEY = registerKey("xenos_tree");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {

        register(context, XENOS_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(BlocksSTLCON.XENOS_LOG.get()),
                new StraightTrunkPlacer(4, 3, 0),
                BlockStateProvider.simple(BlocksSTLCON.XENOS_LEAVES.get()),
                new LeafSpheroidFoliagePlacer(1.5f, 2.25f, ConstantInt.of(0), 1, 1, 0.5f, 0),
                new TwoLayersFeatureSize(2, 2, 2)).build());
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) { return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(STLCON.MOD_ID, name)); }
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) { context.register(key, new ConfiguredFeature<>(feature, configuration));}
}
