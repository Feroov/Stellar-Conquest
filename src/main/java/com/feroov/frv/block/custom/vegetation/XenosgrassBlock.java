package com.feroov.frv.block.custom.vegetation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import org.jetbrains.annotations.Nullable;
import java.util.*;

public class XenosgrassBlock extends Block implements BonemealableBlock
{
    public XenosgrassBlock(Properties properties) { super(properties); }


    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter getter, BlockPos pos, Direction direction, IPlantable plantable)
    {
        if (direction != Direction.UP)
            return false;
        PlantType plantType = plantable.getPlantType(getter, pos.relative(direction));
        return plantType == PlantType.PLAINS || plantType == PlantType.CAVE || plantable instanceof LumiBloomCropBlock;
    }


    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean b)
    {
        return levelReader.getBlockState(blockPos.above()).isAir();
    }



    @Override
    public boolean isBonemealSuccess(Level level, RandomSource rand, BlockPos pos, BlockState state) { return true; }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos p_221272_, BlockState blockState)
    {
        BlockPos blockpos = p_221272_.above();
        BlockState blockstate = Blocks.GRASS.defaultBlockState();
        Optional<Holder.Reference<PlacedFeature>> optional = serverLevel.registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .getHolder(VegetationPlacements.GRASS_BONEMEAL);
        label49:
        for(int i = 0; i < 128; ++i)
        {
            BlockPos blockpos1 = blockpos;

            for(int j = 0; j < i / 16; ++j)
            {
                blockpos1 = blockpos1.offset(randomSource.nextInt(3) - 1, (randomSource.nextInt(3) - 1) *
                        randomSource.nextInt(3) / 2, randomSource.nextInt(3) - 1);
                if (!serverLevel.getBlockState(blockpos1.below()).is(this) || serverLevel.getBlockState(blockpos1)
                        .isCollisionShapeFullBlock(serverLevel, blockpos1))
                {
                    continue label49;
                }
            }

            BlockState blockstate1 = serverLevel.getBlockState(blockpos1);
            if (blockstate1.is(blockstate.getBlock()) && randomSource.nextInt(10) == 0)
            {
                ((BonemealableBlock)blockstate.getBlock()).performBonemeal(serverLevel, randomSource, blockpos1, blockstate1);
            }

            if (blockstate1.isAir())
            {
                Holder<PlacedFeature> holder;
                if (randomSource.nextInt(8) == 0)
                {
                    List<ConfiguredFeature<?, ?>> list = serverLevel.getBiome(blockpos1).value().getGenerationSettings().getFlowerFeatures();
                    if (list.isEmpty())
                    {
                        continue;
                    }

                    holder = ((RandomPatchConfiguration)list.get(0).config()).feature();
                }
                else { if (!optional.isPresent()) { continue; }
                    holder = optional.get();
                }
                holder.value().place(serverLevel, serverLevel.getChunkSource().getGenerator(), randomSource, blockpos1);
            }
        }
    }
}
