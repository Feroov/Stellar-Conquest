package com.feroov.frv.block.custom.wood;

import com.feroov.frv.block.BlocksSTLCON;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;

public class XenosWoodType extends RotatedPillarBlock
{
    /**
     * Constructs a XenosWoodType block with the specified properties.
     *
     * @param properties The properties for the XenosWoodType block.
     */
    public XenosWoodType(Properties properties) { super(properties); }

    /**
     * Specifies that the XenosWoodType block is not flammable.
     *
     * @param state The current state of the block.
     * @param level The BlockGetter to use for retrieving neighboring blocks' information.
     * @param pos The position of the block in the world.
     * @param direction The direction in which the fire is spreading.
     * @return False, indicating the block is not flammable.
     */
    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) { return false; }

    /**
     * Specifies the flammability level of the XenosWoodType block.
     *
     * @param state The current state of the block.
     * @param level The BlockGetter to use for retrieving neighboring blocks' information.
     * @param pos The position of the block in the world.
     * @param direction The direction in which the fire is spreading.
     * @return 0, indicating the block is not flammable.
     */
    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) { return 0; }

    /**
     * Specifies the fire spread speed of the XenosWoodType block.
     *
     * @param state The current state of the block.
     * @param level The BlockGetter to use for retrieving neighboring blocks' information.
     * @param pos The position of the block in the world.
     * @param direction The direction in which the fire is spreading.
     * @return 0, indicating the fire does not spread on this block.
     */
    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) { return 0; }

    /**
     * Retrieves the modified block state when using a specific tool on the XenosWoodType block.
     * If an AxeItem is used, it returns the corresponding stripped xenos wood block state.
     *
     * @param state The current state of the block.
     * @param context The UseOnContext containing information about the tool usage.
     * @param toolAction The ToolAction representing the tool's interaction type.
     * @param simulate True if the operation is being simulated, false if it is applied.
     * @return The modified block state after using the tool on the block, or the original state if no modification.
     */
    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate)
    {
        if(context.getItemInHand().getItem() instanceof AxeItem)
        {
            if(state.is(BlocksSTLCON.XENOS_LOG.get()))
            {
                return BlocksSTLCON.STRIPPED_XENOS_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }

            if(state.is(BlocksSTLCON.XENOS_WOOD.get()))
            {
                return BlocksSTLCON.STRIPPED_XENOS_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }
}