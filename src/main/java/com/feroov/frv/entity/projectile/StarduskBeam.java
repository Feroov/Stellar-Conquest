package com.feroov.frv.entity.projectile;

import com.feroov.frv.entity.EntitiesSTLCON;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;


public class StarduskBeam extends AbstractArrow implements GeoEntity
{

    private int ticksInAir;
    public SoundEvent hitSound = this.getDefaultHitGroundSoundEvent();
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public static final EntityDataAccessor<Integer> PARTICLE = SynchedEntityData.defineId(StarduskBeam.class, EntityDataSerializers.INT);
    private double explosionPower = 0.5;

    public StarduskBeam(EntityType<? extends StarduskBeam> entityType, Level world)
    {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }

    public StarduskBeam(Level world, LivingEntity owner)
    {
        super(EntitiesSTLCON.STARDUSK_BEAM.get(), owner, world);
    }


    /******************************************** Methods of Interest ************************************************************/
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult)
    {
        Entity entity = entityHitResult.getEntity();
        if (entityHitResult.getType() != HitResult.Type.ENTITY || !entityHitResult.getEntity().is(entity))
        {
            if (!this.level().isClientSide) { this.remove(RemovalReason.KILLED); }
        }

        Entity entity1 = this.getOwner();
        DamageSource damagesource;
        if (entity1 == null) { damagesource = this.damageSources().arrow(this, this); }
        else
        {
            damagesource = this.damageSources().arrow(this, entity1);
            if (entity1 instanceof LivingEntity) { ((LivingEntity)entity1).setLastHurtMob(entity); }
        }


        float projectiledamage = 10.0F;
        if (entity.hurt(damagesource, projectiledamage))
        {
            this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

            if (entity instanceof LivingEntity)
            {
                LivingEntity livingentity = (LivingEntity) entity;
                if (!this.level().isClientSide && entity1 instanceof LivingEntity)
                {
                    EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity);
                    this.remove(RemovalReason.KILLED);
                }
                this.doPostHurtEffects(livingentity);
                if (entity1 != null && livingentity != entity1 && livingentity instanceof Player
                        && entity1 instanceof ServerPlayer && !this.isSilent())
                {
                    ((ServerPlayer) entity1).connection
                            .send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 14.0F));
                }
                this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }


        } else { if (!this.level().isClientSide) { this.remove(RemovalReason.KILLED); } }
    }


    @Override
    protected void onHit(HitResult hitResult)
    {
        super.onHit(hitResult);
        if (!this.level().isClientSide)
        {
            boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner());
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, flag, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult)
    {
        super.onHitBlock(blockHitResult);
        this.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
        this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

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
            this.level().addParticle(ParticleTypes.SONIC_BOOM, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

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
