package com.feroov.frv.block.custom.vegetation;

import com.feroov.frv.block.PlantBlocksSTLCON;
import com.feroov.frv.events.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Blushthorn extends PlantBlocksSTLCON
{
    // The voxel shape for the Blushthorn block.
    private static final VoxelShape BLUSH_SHAPE = Block.box(3, 0, 3, 13, 12, 13);

    /**
     * Constructs a Blushthorn block with the specified properties.
     *
     * @param properties The properties for the Blushthorn block.
     */
    public Blushthorn(Properties properties) { super(properties); }

    /**
     * Retrieves the custom voxel shape for the Blushthorn block.
     *
     * @param state The current state of the block.
     * @param getter The BlockGetter to use for retrieving neighboring blocks' information.
     * @param pos The position of the block in the world.
     * @param context The CollisionContext to use for shape calculations.
     * @return The voxel shape of the Blushthorn block.
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        return BLUSH_SHAPE;
    }

    /**
     * Called on every game tick to animate the Blushthorn block.
     * Plays heart particles and a chime sound at random intervals.
     *
     * @param blockstate The current state of the block.
     * @param level The Level where the block is located.
     * @param blockPos The position of the block in the world.
     * @param randomSource The RandomSource to generate random numbers.
     */
    @Override
    public void animateTick(BlockState blockstate, Level level, BlockPos blockPos, RandomSource randomSource)
    {
        if (randomSource.nextInt(30) == 0)
        {
            // Spawn heart particles in a circle around the block.
            for(int i = 0; i < 360; i++)
            {
                if(i % 20 == 0)
                {
                    level.addParticle(ModParticles.HEART_PARTICLES.get(),
                            blockPos.getX() + 0.5f, blockPos.getY() -0.5f, blockPos.getZ() + 0.5f, Math.cos(i) * 0.15d, 0.15d, Math.sin(i) * 0.15d);
                }
            }
            // Play a chime sound when the particles are spawned.
            level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F,1.0f, false);
        }
    }
}
