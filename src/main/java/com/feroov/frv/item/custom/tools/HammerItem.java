package com.feroov.frv.item.custom.tools;

import com.feroov.frv.util.TagsSTLCON;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class HammerItem extends DiggerItem implements Vanishable
{

    public HammerItem(Tier tier, float attackDamage, float attackSpeed, Properties properties)
    {
        super(attackDamage, attackSpeed, tier, TagsSTLCON.Blocks.MINEABLE_WITH_PICK_AND_SHOVEL, properties);
    }

    public static List<BlockPos> getBlocksToBeDestroyed(int range, BlockPos initalBlockPos, ServerPlayer player)
    {
        List<BlockPos> positions = new ArrayList<>();

        BlockHitResult traceResult = player.level().clip(new ClipContext(player.getEyePosition(1f),
                (player.getEyePosition(1f).add(player.getViewVector(1f).scale(6f))),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if(traceResult.getType() == HitResult.Type.MISS)
        {
            return positions;
        }

        if(traceResult.getDirection() == Direction.DOWN || traceResult.getDirection() == Direction.UP)
        {
            for(int x = -range; x <= range; x++)
            {
                for(int y = -range; y <= range; y++)
                {
                    positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY(), initalBlockPos.getZ() + y));
                }
            }
        }

        if(traceResult.getDirection() == Direction.NORTH || traceResult.getDirection() == Direction.SOUTH)
        {
            for(int x = -range; x <= range; x++)
            {
                for(int y = -range; y <= range; y++)
                {
                    positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY() + y, initalBlockPos.getZ()));
                }
            }
        }

        if(traceResult.getDirection() == Direction.EAST || traceResult.getDirection() == Direction.WEST)
        {
            for(int x = -range; x <= range; x++)
            {
                for(int y = -range; y <= range; y++)
                {
                    positions.add(new BlockPos(initalBlockPos.getX(), initalBlockPos.getY() + y, initalBlockPos.getZ() + x));
                }
            }
        }
        return positions;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        tooltip.add(Component.translatable("A Powerful tool that").withStyle(ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("enables you to harness").withStyle(ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("the forces of excavation").withStyle(ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("within a 3x3 area").withStyle(ChatFormatting.ITALIC));
    }
}
