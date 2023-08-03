package com.feroov.frv.entity.projectile;

import com.feroov.frv.entity.AnimationConstants;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;


public class StarduskBeam extends AbstractArrow implements GeoEntity
{

    private int ticksInAir;
    public SoundEvent hitSound = this.getDefaultHitGroundSoundEvent();
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public static final EntityDataAccessor<Integer> PARTICLE = SynchedEntityData.defineId(StarduskBeam.class, EntityDataSerializers.INT);

    /**
     * Constructs a StarduskBeam projectile with the specified entity type and world.
     *
     * @param entityType The entity type of the projectile.
     * @param world      The world where the projectile exists.
     */
    public StarduskBeam(EntityType<? extends StarduskBeam> entityType, Level world)
    {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }

    /**
     * Constructs a StarduskBeam projectile with the specified world and owner.
     *
     * @param world The world where the projectile exists.
     * @param owner The LivingEntity owner of the projectile.
     */
    public StarduskBeam(Level world, LivingEntity owner)
    {
        super(EntitiesSTLCON.STARDUSK_BEAM.get(), owner, world);
    }

    /**
     * Handles the behavior when the StarduskBeam hits an entity.
     *
     * @param entityHitResult The EntityHitResult representing the entity that was hit.
     */
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

            if (entity instanceof LivingEntity livingentity)
            {
                if (!this.level().isClientSide && entity1 instanceof LivingEntity)
                {
                    EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity);
                    this.remove(RemovalReason.KILLED);
                }
                this.doPostHurtEffects(livingentity);
                if (livingentity != entity1 && livingentity instanceof Player
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

    /**
     * Called when the projectile hits something.
     * If the hit is on the server side, it triggers an explosion and discards the projectile.
     *
     * @param hitResult The result of the hit, containing information about the hit location.
     */
    @Override
    protected void onHit(@NotNull HitResult hitResult)
    {
        super.onHit(hitResult);
        if (!this.level().isClientSide)
        {
            boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner());
            double explosionPower = 0.5;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) explosionPower, flag, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }

    /**
     * Handles the behavior when the Stardusk hits a block.
     *
     * @param blockHitResult The BlockHitResult representing the block that was hit.
     */
    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult)
    {
        super.onHitBlock(blockHitResult);
        this.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
        this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

        if (!this.level().isClientSide())
            this.remove(RemovalReason.DISCARDED);
    }

    /**
     * Called every tick to update the entity's behavior.
     * This method handles the removal of the entity after a certain number of ticks.
     */
    @Override
    public void tick()
    {
        super.tick();
        ++this.ticksInAir;

        // Remove the projectile after a certain number of ticks.
        if (this.ticksInAir >= 80) { this.remove(RemovalReason.DISCARDED); }

        if (this.level().isClientSide())
        {
            // Add particles on the client side to represent the projectile's motion.
            this.level().addParticle(ParticleTypes.SONIC_BOOM, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    // Disable gravity for the projectile when it is not in water.
    @Override
    public boolean isNoGravity()  { return !this.isInWater(); }

    /**
     * Registers the animation controllers for this StarduskBeam entity.
     *
     * @param controllers The AnimatableManager.ControllerRegistrar used for registering the animation controllers.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "livingController", 0, event -> event.setAndContinue(AnimationConstants.IDLE)));
    }

    // Return the cached AnimatableInstanceCache for this projectile.
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    // Define the synched data for the projectile.
    @Override
    protected void defineSynchedData() { super.defineSynchedData(); this.entityData.define(PARTICLE, 0); }

    // Return the packet for adding the projectile entity to the client.
    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this);}

    // Increment ticksInAir and remove the projectile if it exceeds a certain value.
    @Override
    protected void tickDespawn() { ++this.ticksInAir; if (this.tickCount >= 40) { this.remove(RemovalReason.KILLED); }}

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        super.shoot(x, y, z, velocity, inaccuracy);
        this.ticksInAir = 0;
    }

    // Set the projectile's motion and reset ticksInAir when it is shot.
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
