package com.feroov.frv.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.PlantType;

public abstract class PlantBlocksSTLCON extends BushBlock implements BonemealableBlock
{
    /**
     * Constructs a new PlantBlocksSTLCON with the specified properties.
     *
     * @param properties The properties for the plant block.
     */
    protected PlantBlocksSTLCON(BlockBehaviour.Properties properties)  { super(properties); }

    /**
     * Gets the PlantType of this plant block.
     * The default implementation returns PlantType.PLAINS.
     *
     * @param getter The BlockGetter to use for retrieving neighboring blocks' information.
     * @param pos The position of the block in the world.
     * @return The PlantType of this plant block.
     */
    @Override
    public PlantType getPlantType(BlockGetter getter, BlockPos pos) { return PlantType.PLAINS; }

    /**
     * Determines if this plant block is a valid target for bonemeal.
     * The default implementation always returns false.
     *
     * @param reader The LevelReader to use for block state retrieval.
     * @param pos The position of the block in the world.
     * @param state The current state of the block.
     * @param client Whether the call is on the client side.
     * @return Always false since the default implementation does not allow bonemealing.
     */
    @Override
    public boolean isValidBonemealTarget(LevelReader reader, BlockPos pos, BlockState state, boolean client) { return false; }

    /**
     * Determines if bonemeal application is successful on this plant block.
     * The default implementation always returns false.
     *
     * @param level The Level where the block is placed.
     * @param random The RandomSource for random calculations.
     * @param pos The position of the block in the world.
     * @param state The current state of the block.
     * @return Always false since the default implementation does not allow bonemeal success.
     */
    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) { return false; }

    /**
     * Performs the bonemeal action on this plant block.
     * The default implementation does nothing.
     *
     * @param level The ServerLevel where the block is placed.
     * @param randomSource The RandomSource for random calculations.
     * @param pos The position of the block in the world.
     * @param state The current state of the block.
     */
    @Override
    public void performBonemeal(ServerLevel level, RandomSource randomSource, BlockPos pos, BlockState state) { }

    /**
     * Gets the flammability of this plant block.
     * The default implementation returns 100, indicating that the block can catch fire easily.
     *
     * @param state The current state of the block.
     * @param getter The BlockGetter to use for retrieving neighboring blocks' information.
     * @param pos The position of the block in the world.
     * @param face The direction of the fire source.
     * @return The flammability value of this plant block (default: 100).
     */
    @Override
    public int getFlammability(BlockState state, BlockGetter getter, BlockPos pos, Direction face) { return 100; }

    /**
     * Gets the fire spread speed of this plant block.
     * The default implementation returns 60, indicating a relatively fast fire spread speed.
     *
     * @param state The current state of the block.
     * @param getter The BlockGetter to use for retrieving neighboring blocks' information.
     * @param pos The position of the block in the world.
     * @param face The direction of the fire source.
     * @return The fire spread speed value of this plant block (default: 60).
     */
    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter getter, BlockPos pos, Direction face) { return 60; }
}
