package com.feroov.frv.item.custom;

import com.feroov.frv.entity.projectile.RaygunBeam;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;


public class CosmicRayGun extends Item
{

    public CosmicRayGun()
    {
        super(new Properties().stacksTo(1).durability(75));

    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player shooter, InteractionHand hand) {
        shooter.startUsingItem(hand);

        return InteractionResultHolder.consume(shooter.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int ticksRemaining)
    {
        if (shooter instanceof Player)
        {
            Player playerentity = (Player) shooter;
            if (stack.getDamageValue() < stack.getMaxDamage() - 1)
            {
                playerentity.getCooldowns().addCooldown(this, 35);

                if (!level.isClientSide)
                {
                    RaygunBeam raygunBeam = createArrow(level, stack, playerentity);
                    raygunBeam.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot(),
                            0.0F, 2.5F, 1.0F);

                    raygunBeam.isNoGravity();

                    stack.hurtAndBreak(1, shooter, p -> p.broadcastBreakEvent(shooter.getUsedItemHand()));
                    level.addFreshEntity(raygunBeam);

                    level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                            SoundEventsSTLCON.RATGUN_SHOOT.get(), SoundSource.PLAYERS, 2.0F,
                            1.0F / (level.random.nextFloat() * 0.4F + 10.2F) + 0.25F);
                }
            }
        }
    }

    @Override
    public ItemStack getDefaultInstance()
    {
        ItemStack defaultInstance = new ItemStack(this);
        EnchantmentHelper.setEnchantments(Collections.singletonMap(Enchantments.PIERCING, 1), defaultInstance);
        return defaultInstance;
    }

    public RaygunBeam createArrow(Level worldIn, ItemStack stack, LivingEntity shooter)
    {
        RaygunBeam raygunBeam = new RaygunBeam(worldIn, shooter);
        return raygunBeam;
    }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }

    @Override
    public int getUseDuration(ItemStack stack) { return 72000; }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        tooltip.add(Component.translatable("Fires a beam of cosmic radiation.")
                .withStyle(ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("Deals high damage")
                .withStyle(ChatFormatting.RED));
    }
}
