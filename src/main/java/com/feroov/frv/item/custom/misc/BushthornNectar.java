package com.feroov.frv.item.custom.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class BushthornNectar extends Item
{
    public BushthornNectar(Item.Properties properties) { super(properties); }

    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity)
    {
        super.finishUsingItem(itemStack, level, livingEntity);
        if (livingEntity instanceof ServerPlayer serverplayer)
        {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, itemStack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide) { livingEntity.removeEffect(MobEffects.POISON); }

        if (itemStack.isEmpty()) { return new ItemStack(Items.GLASS_BOTTLE); }
        else
        {
            if (livingEntity instanceof Player && !((Player)livingEntity).getAbilities().instabuild)
            {
                ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
                Player player = (Player)livingEntity;
                if (!player.getInventory().add(itemstack))
                {
                    player.drop(itemstack, false);
                }
            }
            return itemStack;
        }
    }

    public int getUseDuration(ItemStack itemStack) { return 20; }

    public UseAnim getUseAnimation(ItemStack itemStack) { return UseAnim.DRINK; }

    public SoundEvent getDrinkingSound() { return SoundEvents.HONEY_DRINK; }

    public SoundEvent getEatingSound() { return SoundEvents.HONEY_DRINK; }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) { return ItemUtils.startUsingInstantly(level, player, interactionHand); }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        tooltip.add(Component.translatable("Grants vitality and heals").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("with floral essence.").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GREEN));
    }
}