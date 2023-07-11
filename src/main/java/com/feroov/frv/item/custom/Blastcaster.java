package com.feroov.frv.item.custom;

import com.feroov.frv.entity.projectile.BlastcasterBeam;
import com.feroov.frv.entity.projectile.RaygunBeam;
import com.feroov.frv.events.ModParticles;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Blastcaster extends Item
{
    private static final int CHARGE_TIME = 20;
    private static final int COOLDOWN_TIME = 45;
    private static final Random random = new Random();

    public Blastcaster()  { super(new Properties().stacksTo(1).durability(75)); }

    private float getRandomPitch()  { return 0.7F + random.nextFloat() * 4.6F; }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)  { return UseAnim.BOW; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player shooter, InteractionHand hand)
    {
        if (!level.isClientSide && shooter.getUseItemRemainingTicks() <= 0)
        {
            SoundEvent[] soundEvents = {SoundEventsSTLCON.BLASTCASTER_CHARGE.get()};
            SoundEvent soundEvent = soundEvents[random.nextInt(soundEvents.length)];
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                    soundEvent, SoundSource.PLAYERS, 1.0F, getRandomPitch());
        }
        shooter.startUsingItem(hand);
        return InteractionResultHolder.consume(shooter.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int ticksRemaining)
    {
        if (shooter instanceof Player)
        {
            Player player = (Player) shooter;
            int chargeTicks = getUseDuration(stack) - ticksRemaining;

            if (chargeTicks >= CHARGE_TIME && stack.getDamageValue() < stack.getMaxDamage())
            {
                player.getCooldowns().addCooldown(this, COOLDOWN_TIME);

                if (!level.isClientSide)
                {
                    int beamCount = 6;
                    List<BlastcasterBeam> beams = createArrows(level, stack, player, beamCount);
                    for (BlastcasterBeam beam : beams)
                    {
                        float spread = 20.0F;
                        float randomYaw = random.nextFloat() * spread - spread / 2;
                        float randomPitch = random.nextFloat() * spread - spread / 2;

                        beam.shootFromRotation(player, player.getXRot() + randomPitch, player.getYRot() + randomYaw,
                                0.0F, 2.5F, 1.0F);

                        beam.isNoGravity();
                        level.addFreshEntity(beam);
                    }

                    stack.hurtAndBreak(1, shooter, p -> p.broadcastBreakEvent(shooter.getUsedItemHand()));
                    level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                            SoundEventsSTLCON.BLASTCASTER_SHOOT.get(), SoundSource.PLAYERS, 4.0F, 0.2F);

                    if (level instanceof ServerLevel serverLevel)
                    {
                        Vec3 lookVector = shooter.getViewVector(1.0F);
                        double offsetX = lookVector.x;
                        double offsetY = lookVector.y;
                        double offsetZ = lookVector.z;

                        double x = shooter.getX() + offsetX;
                        double y = shooter.getY() + offsetY + 1.0;
                        double z = shooter.getZ() + offsetZ;

                        ParticleOptions particleOptions = ParticleTypes.FLASH;
                        int particleCount = 2;
                        double speedX = offsetX * 1.1;
                        double speedY = offsetY * 1.1;
                        double speedZ = offsetZ * 1.1;
                        double particleRadius = 0.5D;

                        serverLevel.sendParticles(particleOptions, x, y, z, particleCount, speedX, speedY, speedZ, particleRadius);
                    }
                }
            }
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseTicks)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            Vec3 lookVector = shooter.getViewVector(1.0F);
            double offsetX = lookVector.x;
            double offsetY = lookVector.y;
            double offsetZ = lookVector.z;

            double x = shooter.getX() + offsetX;
            double y = shooter.getY() + offsetY + 1.0;
            double z = shooter.getZ() + offsetZ;

            ParticleOptions particleOptions = ParticleTypes.TOTEM_OF_UNDYING;
            int particleCount = 2;
            double speedX = offsetX * 1.1;
            double speedY = offsetY * 1.1;
            double speedZ = offsetZ * 1.1;
            double particleRadius = 0.5D;

            serverLevel.sendParticles(particleOptions, x, y, z, particleCount, speedX, speedY, speedZ, particleRadius);
        }
    }


    public List<BlastcasterBeam> createArrows(Level worldIn, ItemStack stack, LivingEntity shooter, int count)
    {
        List<BlastcasterBeam> beams = new ArrayList<>();
        for (int i = 0; i < count; i++) { BlastcasterBeam beam = new BlastcasterBeam(worldIn, shooter); beams.add(beam); }
        return beams;
    }


    @Override
    public boolean isFoil(ItemStack stack)  { return false; }

    @Override
    public int getUseDuration(ItemStack stack)  { return 72000; }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        tooltip.add(Component.translatable("☢ Fires a scatter of intense").withStyle(ChatFormatting.ITALIC));
        tooltip.add(Component.translatable(" cosmic radiation bursts. ☢").withStyle(ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("Deals vicious damage").withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable(""));
        tooltip.add(Component.translatable("Hold right-click to charge!").withStyle(ChatFormatting.GRAY));
    }
}