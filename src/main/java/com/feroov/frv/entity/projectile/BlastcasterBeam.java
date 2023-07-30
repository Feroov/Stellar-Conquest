package com.feroov.frv.entity.projectile;

import com.feroov.frv.entity.AnimationConstants;
import com.feroov.frv.entity.EntitiesSTLCON;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;


public class BlastcasterBeam extends AbstractArrow implements GeoEntity
{
    // Tracks the number of ticks the beam has been in the air.
    private int ticksInAir;

    // The sound to play when the beam hits something.
    public SoundEvent hitSound = this.getDefaultHitGroundSoundEvent();

    // Cache for animation-related instances.
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // Data parameter used to sync an integer value between server and client.
    public static final EntityDataAccessor<Integer> PARTICLE = SynchedEntityData.defineId(BlastcasterBeam.class, EntityDataSerializers.INT);

    /**
     * Constructor for the BlastcasterBeam entity.
     * @param entityType The entity type of the BlastcasterBeam.
     * @param world The level in which the entity exists.
     */
    public BlastcasterBeam(EntityType<? extends BlastcasterBeam> entityType, Level world)
    {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }

    /**
     * Constructor for the BlastcasterBeam entity.
     * @param world The level in which the entity exists.
     * @param owner The entity that shot the beam.
     */
    public BlastcasterBeam(Level world, LivingEntity owner)
    {
        super(EntitiesSTLCON.BLAST_CASTER.get(), owner, world);
    }

    /**
     * Called when the beam hits an entity. Applies damage and sets the target on fire.
     */
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult)
    {
        // Get the entities involved in the collision.
        Entity targetEntity = entityHitResult.getEntity();
        Entity shooterEntity = this.getOwner();

        // Check if both entities are LivingEntities.
        if (shooterEntity instanceof LivingEntity shooter && targetEntity instanceof LivingEntity target)
        {
            // Create a DamageSource representing the arrow shot by the shooter.
            DamageSource damageSource = this.damageSources().arrow(this, shooter);

            // Set the projectile damage amount.
            float projectileDamage = 25.0F;

            // Inflict damage on the target and set it on fire for 5 seconds if the hit is successful.
            if (target.hurt(damageSource, projectileDamage))
            {
                target.setSecondsOnFire(5);

                // Perform additional effects on the target and shooter if they are living entities.
                if (!this.level().isClientSide && shooter instanceof Player)
                {
                    EnchantmentHelper.doPostHurtEffects(target, shooter);
                    EnchantmentHelper.doPostDamageEffects(shooter, target);
                    this.remove(RemovalReason.KILLED);
                }
            }
        }
    }

    /**
     * Called when the beam hits a block. Spawns particles and places fire blocks if the hit block is air.
     */
    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult)
    {
        super.onHitBlock(blockHitResult);

        // Spawn flash particles at the hit position.
        this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

        // Get the position and state of the hit block.
        BlockPos hitPos = blockHitResult.getBlockPos();
        BlockState hitBlockState = this.level().getBlockState(hitPos);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        // If the hit block is air and fire can be placed on it, place a fire block at the hit position.
        if (hitBlockState.isAir() && Blocks.FIRE.canSurvive(hitBlockState, this.level(), hitPos))
        {
            this.level().setBlockAndUpdate(hitPos, Blocks.FIRE.defaultBlockState());
        }
        else // Otherwise, try to place a fire block in one of the neighboring positions.
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

        // Remove the beam entity if it is not on the client-side.
        if (!this.level().isClientSide())
            this.remove(RemovalReason.DISCARDED);
    }

    /**
     * Called on every tick to update the entity.
     */
    @Override
    public void tick()
    {
        super.tick();
        ++this.ticksInAir;

        // Check if the arrow has been in the air for too long and remove it if necessary.
        if (this.ticksInAir >= 80) { this.remove(RemovalReason.DISCARDED); }

        // Only execute the following code on the client side (when rendering the game).
        if (this.level().isClientSide())
        {
            // Define the offsets for positioning the HAPPY_VILLAGER particles.
            double offsetX = 0.1;
            double offsetY = 0.1;
            double offsetZ = 0.1;

            // Spawn HAPPY_VILLAGER particles in a loop, with different offsets for each iteration.
            for (int i = 0; i < 7; i++)
            {
                // Calculate the adjusted position for the particle based on the current iteration.
                double x = this.getX() + (i == 1 || i == 2 ? -offsetX : (i == 4 || i == 5 ? offsetX : 0));
                double y = this.getY() + (i == 5 ? offsetY : (i == 6 ? -offsetY : 0));
                double z = this.getZ() + (i == 3 || i == 4 ? -offsetZ : (i == 6 || i == 1 ? offsetZ : 0));

                // Spawn the HAPPY_VILLAGER particle at the adjusted position.
                this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, true, x, y, z, 0, 0, 0);
            }
        }
    }

    /**
     * Checks if the arrow has no gravity. It returns true if the arrow is not in water, indicating no gravity effect.
     *
     * @return True if the arrow is not affected by gravity (e.g., it is underwater), otherwise false.
     */
    @Override
    public boolean isNoGravity()  { return !this.isInWater(); }

    /**
     * Registers the animation controllers for this BlastcasterBeam entity.
     *
     * @param controllers The AnimatableManager.ControllerRegistrar used for registering the animation controllers.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "livingController", 0, event -> event.setAndContinue(AnimationConstants.IDLE)));
    }

    /**
     * Retrieves the AnimatableInstanceCache associated with this entity.
     *
     * @return The AnimatableInstanceCache used for caching animation data.
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    /**
     * Retrieves the packet used to add the entity to the client-side world.
     *
     * @return The packet containing entity information for spawning on the client-side.
     */
    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this);}

    /**
     * Handles the despawn of the BlastcasterBeam entity by increasing the ticksInAir counter.
     * The entity is removed when the tick count exceeds a certain value.
     */
    @Override
    protected void tickDespawn() { ++this.ticksInAir; if (this.tickCount >= 40) { this.remove(RemovalReason.KILLED); }}

    /**
     * Shoots the BlastcasterBeam entity with the given parameters, resetting the ticksInAir counter.
     *
     * @param x         The x-coordinate of the shooting direction.
     * @param y         The y-coordinate of the shooting direction.
     * @param z         The z-coordinate of the shooting direction.
     * @param velocity  The velocity of the shot.
     * @param inaccuracy The inaccuracy of the shot.
     */
    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        super.shoot(x, y, z, velocity, inaccuracy);
        this.ticksInAir = 0;
    }

    /**
     * Defines the synchronized entity data for the BlastcasterBeam entity.
     * This method is automatically called by the superclass.
     */
    @Override
    protected void defineSynchedData() { super.defineSynchedData(); this.entityData.define(PARTICLE, 0); }

    /**
     * Saves additional data of the entity to a CompoundTag.
     *
     * @param compound The CompoundTag used to store additional data.
     */
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putShort("life", (short) this.ticksInAir);
    }

    /**
     * Reads additional data of the entity from a CompoundTag.
     *
     * @param compound The CompoundTag used to read additional data from.
     */
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        this.ticksInAir = compound.getShort("life");
    }

    /**
     * Retrieves the item stack used for pickup. In this case, it returns null since the BlastcasterBeam
     * entity is not meant to be picked up by players.
     *
     * @return The ItemStack used for pickup (null in this case).
     */
    @Override
    protected @NotNull ItemStack getPickupItem() { return null; }

    /**
     * Sets the sound event for the hit effect of the BlastcasterBeam entity.
     *
     * @param soundIn The SoundEvent to be set as the hit sound.
     */
    @Override
    public void setSoundEvent(@NotNull SoundEvent soundIn) { this.hitSound = soundIn; }

    /**
     * Retrieves the default SoundEvent to be played when the BlastcasterBeam hits the ground.
     *
     * @return The default SoundEvent for hitting the ground.
     */
    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent()  { return SoundEvents.AMETHYST_BLOCK_CHIME; }

    /**
     * Specifies whether the BlastcasterBeam entity should display the fire animation.
     *
     * @return True if the fire animation should be displayed, false otherwise.
     */
    @Override
    public boolean displayFireAnimation() { return false; }
}
