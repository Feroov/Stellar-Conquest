package com.feroov.frv.block.custom.vegetation;

import com.feroov.frv.block.PlantBlocksSTLCON;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Xenosgrass extends PlantBlocksSTLCON
{
    // The voxel shape for the Xenosgrass block.
    private static final VoxelShape XENOSGRASS_SHAPE = Block.box(3, 0, 3, 13, 14, 13);

    /**
     * Constructs a Xenosgrass block with the specified properties.
     *
     * @param properties The properties for the Xenosgrass block.
     */
    public Xenosgrass(Properties properties) { super(properties); }

    /**
     * Retrieves the custom voxel shape for the Xenosgrass block.
     *
     * @param state The current state of the block.
     * @param getter The BlockGetter to use for retrieving neighboring blocks' information.
     * @param pos The position of the block in the world.
     * @param context The CollisionContext to use for shape calculations.
     * @return The voxel shape of the Xenosgrass block.
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        return XENOSGRASS_SHAPE;
    }
}
