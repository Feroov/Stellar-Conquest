package com.feroov.frv.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BouncyBonk extends Item
{
    public BouncyBonk()  { super(new Properties().stacksTo(1).durability(175)); }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        if (attacker instanceof Player && target instanceof Mob)
        {
            Vec3 knockbackVector = attacker.getLookAngle().scale(-5.0);
            target.knockback(2.0, knockbackVector.x, knockbackVector.z);

            double launchVelocity = 0.5;
            target.setDeltaMovement(target.getDeltaMovement().add(0, launchVelocity, 0));
            stack.hurtAndBreak(1, attacker, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));

            if (attacker instanceof ServerPlayer)
            {
                ServerPlayer player = (ServerPlayer) attacker;
                ServerLevel world = player.serverLevel();

                double x = target.getX();
                double y = target.getY() + target.getEyeHeight();
                double z = target.getZ();

                ParticleOptions particleOptions = ParticleTypes.CLOUD;
                int particleCount = 10;
                double particleSpread = 0.45;

                for (int i = 0; i < particleCount; i++)
                {
                    double offsetX = attacker.getRandom().nextGaussian() * particleSpread;
                    double offsetY = attacker.getRandom().nextGaussian() * particleSpread;
                    double offsetZ = attacker.getRandom().nextGaussian() * particleSpread;

                    double particleX = x + offsetX;
                    double particleY = y + offsetY;
                    double particleZ = z + offsetZ;

                    world.sendParticles(particleOptions, particleX, particleY, particleZ, 0, 0, 0, 0, 1);
                }

                DamageSources damageSources = new DamageSources(world.registryAccess());
                float damageAmount = 3.0f;
                DamageSource damageSource = damageSources.playerAttack(player);
                target.hurt(damageSource, damageAmount);


                player.level().playSound(null, attacker.blockPosition(), SoundEvents.SLIME_JUMP, SoundSource.PLAYERS, 1.0f, 0.8f);
                player.level().playSound(null, attacker.blockPosition(), SoundEvents.SLIME_HURT, SoundSource.PLAYERS, 1.0f, 0.8f);
                player.level().playSound(null, attacker.blockPosition(), SoundEvents.SLIME_BLOCK_BREAK, SoundSource.PLAYERS, 1.0f, 0.8f);
            }
            return true;
        }
        return super.hurtEnemy(stack, target, attacker);
    }


    @Override
    public boolean isFoil(ItemStack stack)  { return false; }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        tooltip.add(Component.translatable("Slap enemies away.").withStyle(ChatFormatting.GRAY));
    }
}
