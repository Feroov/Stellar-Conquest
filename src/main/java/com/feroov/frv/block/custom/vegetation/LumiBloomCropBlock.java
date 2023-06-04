package com.feroov.frv.block.custom.vegetation;


import com.feroov.frv.item.ItemsSTLCON;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class LumiBloomCropBlock extends CropBlock
{
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 6);

    public LumiBloomCropBlock(Properties properties) { super(properties); }

    @Override
    protected ItemLike getBaseSeedId() { return ItemsSTLCON.LUMIBLOOM_SEEDS.get(); }

    @Override
    public IntegerProperty getAgeProperty() { return AGE; }

    @Override
    public int getMaxAge() { return 6; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(AGE); }
}