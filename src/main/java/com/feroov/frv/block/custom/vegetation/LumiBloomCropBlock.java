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
    // The property that represents the age of the Lumi Bloom crop block.
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 6);

    /**
     * Constructs a LumiBloomCropBlock with the specified properties.
     *
     * @param properties The properties for the LumiBloomCropBlock.
     */
    public LumiBloomCropBlock(Properties properties) { super(properties); }

    /**
     * Retrieves the base seed item for planting Lumi Bloom crops.
     *
     * @return The item representing Lumi Bloom seeds.
     */
    @Override
    protected ItemLike getBaseSeedId() { return ItemsSTLCON.LUMIBLOOM_SEEDS.get(); }

    /**
     * Retrieves the IntegerProperty representing the age of the Lumi Bloom crop block.
     * The age determines the growth stage of the crop.
     *
     * @return The IntegerProperty for the crop's age.
     */
    @Override
    public IntegerProperty getAgeProperty() { return AGE; }

    /**
     * Retrieves the maximum age value for the Lumi Bloom crop block.
     * When the crop reaches this age, it is fully grown and can be harvested.
     *
     * @return The maximum age value for the crop.
     */
    @Override
    public int getMaxAge() { return 6; }

    /**
     * Defines the block state for the Lumi Bloom crop block.
     * Adds the age property to the block state to represent different growth stages.
     *
     * @param builder The StateDefinition.Builder used to build the block state.
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(AGE); }
}