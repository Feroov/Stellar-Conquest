package com.feroov.frv.block;

import com.feroov.frv.entity.BossSpawnerBlockEntity;
import com.feroov.frv.entity.BossVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public class BossSpawnerBlock extends BaseEntityBlock
{
    // The voxel shape of the boss spawner block.
    private static final VoxelShape SHAPE = Block.box(-4, -4, -4, 10, 10, 10);

    // The variant of the boss associated with this spawner block.
    private final BossVariant boss;

    /**
     * Constructs a BossSpawnerBlock with the specified properties and boss variant.
     *
     * @param props The properties for the BossSpawnerBlock.
     * @param variant The boss variant associated with this spawner block.
     */
    public BossSpawnerBlock(BlockBehaviour.Properties props, BossVariant variant)
    {
        super(props);
        this.boss = variant;
    }

    /**
     * Gets the render shape for this block.
     *
     * @param state The current state of the block.
     * @return The RenderShape.MODEL indicating this block uses the model for rendering.
     */
    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    /**
     * Creates a new BlockEntity instance for this block at the specified position.
     *
     * @param pos The position of the block in the world.
     * @param state The current state of the block.
     * @return The new BossSpawnerBlockEntity instance associated with this block.
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return boss.getType().create(pos, state); }

    /**
     * Gets the VoxelShape of this block's collision box.
     *
     * @param state The current state of the block.
     * @param getter The BlockGetter to use for retrieving neighboring blocks' information.
     * @param pos The position of the block in the world.
     * @param context The CollisionContext to use for collision calculations.
     * @return The VoxelShape representing this block's collision box.
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) { return SHAPE; }

    /**
     * Gets the BlockEntityTicker for this block, used for updating block entities in the world.
     *
     * @param level The Level where the block entity is placed.
     * @param state The current state of the block.
     * @param type The BlockEntityType of the block entity.
     * @param <T> The type of BlockEntity associated with this block.
     * @return The BlockEntityTicker instance for the boss spawner block entity.
     */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, boss.getType(), BossSpawnerBlockEntity::tick);
    }

    /**
     * Determines whether the given entity can destroy this block.
     *
     * @param state The current state of the block.
     * @param getter The BlockGetter to use for retrieving neighboring blocks' information.
     * @param pos The position of the block in the world.
     * @param entity The entity trying to destroy the block.
     * @return True if the entity can destroy the block, false otherwise.
     */
    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter getter, BlockPos pos, Entity entity)
    {
        return state.getDestroySpeed(getter, pos) >= 0f;
    }
}
