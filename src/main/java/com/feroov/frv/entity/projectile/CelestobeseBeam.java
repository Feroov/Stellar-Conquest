package com.feroov.frv.entity.projectile;

import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.monster.Celestobese;
import com.feroov.frv.entity.monster.Celestroid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;


public class CelestobeseBeam extends AbstractArrow implements GeoEntity
{

    private int ticksInAir;
    public SoundEvent hitSound = this.getDefaultHitGroundSoundEvent();
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public static final EntityDataAccessor<Integer> PARTICLE = SynchedEntityData.defineId(CelestobeseBeam.class, EntityDataSerializers.INT);

    public CelestobeseBeam(EntityType<? extends CelestobeseBeam> entityType, Level world)
    {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }

    public CelestobeseBeam(Level level, Celestobese celestobese, double d2, double d3, double d4)
    {
        super(EntitiesSTLCON.CELESTOBESE_BEAM.get(), celestobese, level);
    }

    /******************************************** Methods of Interest ************************************************************/
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult)
    {
        Entity targetEntity = entityHitResult.getEntity();
        Entity shooterEntity = this.getOwner();

        if (shooterEntity instanceof LivingEntity && targetEntity instanceof LivingEntity)
        {
            LivingEntity shooter = (LivingEntity) shooterEntity;
            LivingEntity target = (LivingEntity) targetEntity;

            DamageSource damageSource = this.damageSources().arrow(this, shooter);
            float projectileDamage = 9.0F;

            if (target.hurt(damageSource, projectileDamage))
            {
                if (!this.level().isClientSide && shooter instanceof Player)
                {
                    EnchantmentHelper.doPostHurtEffects(target, shooter);
                    EnchantmentHelper.doPostDamageEffects(shooter, target);
                    this.remove(RemovalReason.KILLED);
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult)
    {
        super.onHitBlock(blockHitResult);
        this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

        BlockPos hitPos = blockHitResult.getBlockPos();
        BlockState hitBlockState = this.level().getBlockState(hitPos);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        if (hitBlockState.isAir() && Blocks.FIRE.canSurvive(hitBlockState, this.level(), hitPos))
        {
            this.level().setBlockAndUpdate(hitPos, Blocks.FIRE.defaultBlockState());
        }
        else
        {
            for (Direction direction : Direction.values())
            {
                mutablePos.setWithOffset(hitPos, direction);
                hitBlockState = this.level().getBlockState(mutablePos);
                if (hitBlockState.isAir() && Blocks.FIRE.canSurvive(hitBlockState, this.level(), mutablePos))
                {
                    this.level().setBlockAndUpdate(mutablePos, Blocks.FIRE.defaultBlockState());
                    break;
                }
            }
        }

        if (!this.level().isClientSide())
            this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void tick()
    {
        super.tick();
        ++this.ticksInAir;
        if (this.ticksInAir >= 80) { this.remove(RemovalReason.DISCARDED); }

        if (this.level().isClientSide())
        {
            double offsetX = 0.1;
            double offsetY = 0.1;
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, true, this.getX() + offsetX, this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, true, this.getX(), this.getY() + offsetY, this.getZ(), 0, 0, 0);
        }
    }

    /***************************************************************************************************************************/


    @Override
    public boolean isNoGravity()  { return !this.isInWater(); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState)
    {
        tAnimationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    protected void defineSynchedData() { super.defineSynchedData(); this.entityData.define(PARTICLE, 0); }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this);}

    @Override
    protected void tickDespawn() { ++this.ticksInAir; if (this.tickCount >= 40) { this.remove(RemovalReason.KILLED); }}

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        super.shoot(x, y, z, velocity, inaccuracy);
        this.ticksInAir = 0;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putShort("life", (short) this.ticksInAir);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        this.ticksInAir = compound.getShort("life");
    }

    @Override
    protected ItemStack getPickupItem() { return null; }


    @Override
    public void setSoundEvent(SoundEvent soundIn) { this.hitSound = soundIn; }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent()  { return SoundEvents.AMETHYST_BLOCK_CHIME; }

    @Override
    public boolean displayFireAnimation() { return false; }
}
