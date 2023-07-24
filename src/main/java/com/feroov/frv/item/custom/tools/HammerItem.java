package com.feroov.frv.item.custom.tools;

import com.feroov.frv.util.TagsSTLCON;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        if (attacker instanceof Player && target instanceof Mob)
        {
            Vec3 knockbackVector = attacker.getLookAngle().scale(-5.0);
            target.knockback(0.8, knockbackVector.x, knockbackVector.z);

            double launchVelocity = 0.3;

            target.setDeltaMovement(target.getDeltaMovement().add(0, launchVelocity, 0));
            stack.hurtAndBreak(1, attacker, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));

            if (attacker instanceof ServerPlayer)
            {
                ServerPlayer player = (ServerPlayer) attacker;
                ServerLevel world = player.serverLevel();
                performSweepAttack(attacker);

                double x = target.getX();
                double y = target.getY() + target.getEyeHeight();
                double z = target.getZ();

                ParticleOptions particleOptions = ParticleTypes.CRIT;
                int particleCount = 20;
                double particleSpread = 0.65;

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

                player.level().playSound(null, attacker.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 0.8f);
            }
            return true;
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    private void performSweepAttack(LivingEntity attacker)
    {
        double attackRadius = 3.5;
        ServerPlayer player = (ServerPlayer) attacker;
        ServerLevel world = player.serverLevel();

        List<LivingEntity> entities = attacker.level().getEntitiesOfClass(
                LivingEntity.class,
                attacker.getBoundingBox().inflate(attackRadius),
                entity -> entity != attacker && entity.isAlive() && entity instanceof Mob
        );

        for (LivingEntity entity : entities)
        {
            DamageSources damageSources = new DamageSources(world.registryAccess());
            float damageAmount = 7.0f;
            DamageSource damageSource = damageSources.playerAttack(player);
            entity.hurt(damageSource, damageAmount);

            double x = entity.getX();
            double y = entity.getY() + entity.getEyeHeight();
            double z = entity.getZ();

            ParticleOptions particleOptions = ParticleTypes.CRIT;
            int particleCount = 20;
            double particleSpread = 0.65;

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
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment == Enchantments.UNBREAKING ||
                enchantment == Enchantments.MENDING ||
                enchantment == Enchantments.BLOCK_FORTUNE;
    }


    public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player)
    {
        return !player.isCreative();
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
