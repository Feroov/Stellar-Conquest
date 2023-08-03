package com.feroov.frv.entity.projectile;

import com.feroov.frv.entity.AnimationConstants;
import com.feroov.frv.entity.EntitiesSTLCON;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;


public class RaygunBeam extends AbstractArrow implements GeoEntity
{

    private int ticksInAir;
    public SoundEvent hitSound = this.getDefaultHitGroundSoundEvent();
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public static final EntityDataAccessor<Integer> PARTICLE = SynchedEntityData.defineId(RaygunBeam.class, EntityDataSerializers.INT);

    /**
     * Constructs a RaygunBeam projectile with the specified entity type and world.
     *
     * @param entityType The entity type of the projectile.
     * @param world      The world where the projectile exists.
     */
    public RaygunBeam(EntityType<? extends RaygunBeam> entityType, Level world)
    {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }

    /**
     * Constructs a RaygunBeam projectile with the specified world and owner.
     *
     * @param world The world where the projectile exists.
     * @param owner The LivingEntity owner of the projectile.
     */
    public RaygunBeam(Level world, LivingEntity owner)
    {
        super(EntitiesSTLCON.RAYGUN_BEAM.get(), owner, world);
    }

    /**
     * Handles the behavior when the RaygunBeam hits an entity.
     *
     * @param entityHitResult The EntityHitResult representing the entity that was hit.
     */
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult)
    {
        Entity targetEntity = entityHitResult.getEntity();
        Entity shooterEntity = this.getOwner();

        if (shooterEntity instanceof LivingEntity shooter && targetEntity instanceof LivingEntity target)
        {

            DamageSource damageSource = this.damageSources().arrow(this, shooter);
            float projectileDamage = 30.0F;

            if (target.hurt(damageSource, projectileDamage))
            {
                // Add particles when the projectile hits an entity and deals damage.
                this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

                if (!this.level().isClientSide && shooter instanceof Player)
                {
                    // Apply enchantment effects and remove the projectile after dealing damage.
                    EnchantmentHelper.doPostHurtEffects(target, shooter);
                    EnchantmentHelper.doPostDamageEffects(shooter, target);
                    this.remove(RemovalReason.KILLED);
                }
            }
        }
    }

    /**
     * Handles the behavior when the RaygunBeam hits a block.
     *
     * @param blockHitResult The BlockHitResult representing the block that was hit.
     */
    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult)
    {
        super.onHitBlock(blockHitResult);

        // Play a sound and add particles when the projectile hits a block.
        this.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
        this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

        // Remove the projectile after hitting a block.
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
     * Registers the animation controllers for this RaygunBeam entity.
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

    // Set the projectile's motion and reset ticksInAir when it is shot.
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
    public @NotNull ItemStack getPickupItem() { return null; }

    @Override
    public void setSoundEvent(@NotNull SoundEvent soundIn) { this.hitSound = soundIn; }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent()  { return SoundEvents.AMETHYST_BLOCK_CHIME; }

    @Override
    public boolean displayFireAnimation() { return false; }
}
