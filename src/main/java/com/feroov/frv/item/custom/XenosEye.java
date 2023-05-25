package com.feroov.frv.item.custom;

import com.feroov.frv.STLCON;
import com.feroov.frv.block.STLCONPortalBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XenosEye extends Item
{
    private final Set<Block> possibleBlocks = new HashSet<Block>()
    {
        private static final long serialVersionUID = 1L;
        {
            add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(STLCON.MOD_ID, "xenoflux")));
        }};

    public XenosEye(Properties properties) { super(properties); }


    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        ItemStack itemstack = player.getItemInHand(hand);
        Level worldIn = context.getLevel();
        RandomSource random = worldIn.random;
        worldIn.playSound(player, pos, SoundEvents.EVOKER_CAST_SPELL, SoundSource.BLOCKS, 1.0F, worldIn.getRandom().nextFloat() * 0.4F + 0.8F);
        worldIn.addParticle(ParticleTypes.SQUID_INK,
                pos.getX() + 0.5f, pos.getY() + 1.5f, pos.getZ() + 0.5f,0d, 0d,0d);

        if (!player.mayUseItemAt(pos, facing, itemstack)) { return InteractionResult.SUCCESS; }
        if (facing != Direction.UP) { return InteractionResult.SUCCESS; }

        if (!worldIn.isClientSide)
        {
            STLCONPortalBlocks portalBlock;
            for (Direction direction : Direction.Plane.VERTICAL)
            {
                BlockPos framePos = context.getClickedPos().relative(direction);
                if (worldIn.getBlockState(framePos.below()) == ForgeRegistries.BLOCKS.getValue(new ResourceLocation(STLCON.MOD_ID, "xenoflux")).defaultBlockState()) {
                    portalBlock = (STLCONPortalBlocks) ForgeRegistries.BLOCKS.getValue(new ResourceLocation(STLCON.MOD_ID, "xenosphere_portal"));
                    assert portalBlock != null;
                    portalBlock.createPortal(context.getLevel(), framePos);
                    itemstack.shrink(1);
                    return InteractionResult.CONSUME;
                }
                else
                {
                    if (possibleBlocks.contains(worldIn.getBlockState(context.getClickedPos()).getBlock()))
                    {
                        worldIn.setBlock(context.getClickedPos().above(), Blocks.FIRE.defaultBlockState(), 0);
                    }
                    itemstack.shrink(1);
                    player.setItemInHand(hand, itemstack);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("Shows the world of Xenosphere").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
    }
}
