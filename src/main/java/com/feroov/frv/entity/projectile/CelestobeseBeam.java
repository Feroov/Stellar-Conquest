package com.feroov.frv.entity.projectile;

import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.monster.Celestobese;
import com.feroov.frv.entity.monster.Celestroid;
import com.feroov.frv.entity.monster.Mekkron;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
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

    public CelestobeseBeam(Level level, Celestobese celestobese)
    {
        super(EntitiesSTLCON.CELESTOBESE_BEAM.get(), celestobese, level);
    }

    /******************************************** Methods of Interest ************************************************************/
    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult)
    {
        super.onHitEntity(entityHitResult);

        Entity entity = entityHitResult.getEntity();
        Entity entity1 = this.getOwner();

        if (entity instanceof Mekkron) { return; }
        if (entity instanceof Celestroid) { return; }
        if (entity instanceof Celestobese) { return; }
        if (entity instanceof CelestobeseBeam) { return; }

        if (!this.level().isClientSide())
        {
            boolean flag;

            if (entity1 instanceof LivingEntity livingentity)
            {
                flag = entity.hurt(this.damageSources().thrown(this, livingentity), 9.0F);
                if (flag)
                {
                    if (entity.isAlive()) { this.doEnchantDamageEffects(livingentity, entity); }
                }
            }
        }
        if (!this.level().isClientSide()) this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult)
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

        if (this.ticksInAir >= 100) {if (!this.level().isClientSide()) {this.remove(RemovalReason.DISCARDED);}}

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
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this);}

    @Override
    protected void tickDespawn() { ++this.ticksInAir; if (this.tickCount >= 40) { this.remove(RemovalReason.KILLED); }}

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        super.shoot(x, y, z, velocity, inaccuracy);
        this.ticksInAir = 0;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putShort("life", (short) this.ticksInAir);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        this.ticksInAir = compound.getShort("life");
    }

    @Override
    protected @NotNull ItemStack getPickupItem() { return null; }


    @Override
    public void setSoundEvent(@NotNull SoundEvent soundIn) { this.hitSound = soundIn; }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent()  { return SoundEvents.AMETHYST_BLOCK_CHIME; }

    @Override
    public boolean displayFireAnimation() { return false; }
}
