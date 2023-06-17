package com.feroov.frv.world.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.function.BiFunction;

public final class FeaturePlacers
{
    public static final BiFunction<LevelSimulatedReader, BlockPos, Boolean> VALID_TREE_POS = TreeFeature::validTreePos;

    public static void placeLeaf(LevelSimulatedReader world, FoliagePlacer.FoliageSetter setter, BiFunction<LevelSimulatedReader, BlockPos, Boolean> predicate, BlockPos pos, BlockStateProvider config, RandomSource random)
    {
        if (predicate.apply(world, pos)) setter.set(pos, config.getState(random, pos));
    }

    public static void placeSpheroid(LevelSimulatedReader world, FoliagePlacer.FoliageSetter setter, BiFunction<LevelSimulatedReader, BlockPos, Boolean> predicate, RandomSource random, BlockPos centerPos, float xzRadius, float yRadius, float verticalBias, BlockStateProvider config)
    {
        float xzRadiusSquared = xzRadius * xzRadius;
        float yRadiusSquared = yRadius * yRadius;
        float superRadiusSquared = xzRadiusSquared * yRadiusSquared;
        FeaturePlacers.placeLeaf(world, setter, predicate, centerPos, config, random);

        for (int y = 0; y <= yRadius; y++)
        {
            if (y > yRadius) continue;

            FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( 0,  y, 0), config, random);
            FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( 0, -y, 0), config, random);
        }

        for (int x = 0; x <= xzRadius; x++)
        {
            for (int z = 1; z <= xzRadius; z++)
            {
                if (x * x + z * z > xzRadiusSquared) continue;

                FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  x, 0,  z), config, random);
                FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -x, 0, -z), config, random);
                FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -z, 0,  x), config, random);
                FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  z, 0, -x), config, random);

                for (int y = 1; y <= yRadius; y++)
                {
                    float xzSquare = ((x * x + z * z) * yRadiusSquared);

                    if (xzSquare + (((y - verticalBias) * (y - verticalBias)) * xzRadiusSquared) <= superRadiusSquared)
                    {
                        FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  x,  y,  z), config, random);
                        FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -x,  y, -z), config, random);
                        FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -z,  y,  x), config, random);
                        FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  z,  y, -x), config, random);
                    }

                    if (xzSquare + (((y + verticalBias) * (y + verticalBias)) * xzRadiusSquared) <= superRadiusSquared)
                    {
                        FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  x, -y,  z), config, random);
                        FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -x, -y, -z), config, random);
                        FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset( -z, -y,  x), config, random);
                        FeaturePlacers.placeLeaf(world, setter, predicate, centerPos.offset(  z, -y, -x), config, random);
                    }
                }
            }
        }
    }

    private static void setIfEmpty(LevelAccessor world, BlockPos pos, BlockState state)
    {
        if (world.isEmptyBlock(pos))
        {
            world.setBlock(pos, state,3);
        }
    }

    public static BlockState transferAllStateKeys(BlockState stateIn, Block blockOut)
    {
        return transferAllStateKeys(stateIn, blockOut.defaultBlockState());
    }

    public static BlockState transferAllStateKeys(BlockState stateIn, BlockState stateOut)
    {
        for (Property<?> property : stateOut.getProperties())
        {
            stateOut = transferStateKey(stateIn, stateOut, property);
        }
        return stateOut;
    }

    public static <T extends Comparable<T>> BlockState transferStateKey(BlockState stateIn, BlockState stateOut, Property<T> property)
    {
        if(!stateIn.hasProperty(property) || !stateOut.hasProperty(property)) return stateOut;
        return stateOut.setValue(property, stateIn.getValue(property));
    }
}
