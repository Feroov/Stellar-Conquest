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

import java.util.*;

public class XenosgrassBlock extends Block implements BonemealableBlock
{
    /**
     * Constructs a XenosgrassBlock with the specified properties.
     *
     * @param properties The properties for the XenosgrassBlock.
     */
    public XenosgrassBlock(Properties properties) { super(properties); }

    /**
     * Checks if the block can sustain a specific type of plant on it.
     * Xenosgrass can sustain plants on its top face (direction UP) if the plant type is PLAINS, CAVE,
     * or if the plantable is an instance of LumiBloomCropBlock.
     *
     * @param state The current state of the block.
     * @param getter The BlockGetter to use for retrieving neighboring blocks' information.
     * @param pos The position of the block in the world.
     * @param direction The direction in which the plant is being placed.
     * @param plantable The IPlantable instance representing the plant being placed.
     * @return True if the block can sustain the plant, false otherwise.
     */
    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter getter, BlockPos pos, Direction direction, IPlantable plantable)
    {
        if (direction != Direction.UP)
            return false;
        PlantType plantType = plantable.getPlantType(getter, pos.relative(direction));
        return plantType == PlantType.PLAINS || plantType == PlantType.CAVE || plantable instanceof LumiBloomCropBlock;
    }

    /**
     * Checks if the Xenosgrass block is a valid target for bonemeal.
     * The block is a valid bonemeal target if the block above it is air.
     *
     * @param levelReader The LevelReader representing the world.
     * @param blockPos The position of the Xenosgrass block.
     * @param blockState The current state of the Xenosgrass block.
     * @param isClient The flag indicating if the check is performed on the client side.
     * @return True if the block is a valid target for bonemeal, false otherwise.
     */
    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean b)
    {
        return levelReader.getBlockState(blockPos.above()).isAir();
    }

    /**
     * Determines if bonemeal application is successful on the Xenosgrass block.
     * Bonemeal success is always true for Xenosgrass.
     *
     * @param level The Level in which the bonemeal is being applied.
     * @param rand The RandomSource to generate random numbers.
     * @param pos The position of the Xenosgrass block.
     * @param state The current state of the Xenosgrass block.
     * @return True, indicating bonemeal application success.
     */
    @Override
    public boolean isBonemealSuccess(Level level, RandomSource rand, BlockPos pos, BlockState state) { return true; }

    /**
     * Performs bonemeal effect on the Xenosgrass block.
     * When bonemeal is applied, it has a chance to create new flowers or plants in the vicinity.
     *
     * @param serverLevel The ServerLevel where the bonemeal effect is being applied.
     * @param randomSource The RandomSource to generate random numbers.
     * @param pos The position of the Xenosgrass block.
     * @param blockState The current state of the Xenosgrass block.
     */
    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos pos, BlockState blockState)
    {
        BlockPos blockpos = pos.above();
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
