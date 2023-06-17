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
    protected PlantBlocksSTLCON(BlockBehaviour.Properties properties)  { super(properties); }

    @Override
    public PlantType getPlantType(BlockGetter getter, BlockPos pos) { return PlantType.PLAINS; }

    @Override
    public boolean isValidBonemealTarget(LevelReader reader, BlockPos pos, BlockState state, boolean client) { return false; }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) { return false; }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource randomSource, BlockPos pos, BlockState state) { }

    @Override
    public int getFlammability(BlockState state, BlockGetter getter, BlockPos pos, Direction face) { return 100; }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter getter, BlockPos pos, Direction face) { return 60; }
}
