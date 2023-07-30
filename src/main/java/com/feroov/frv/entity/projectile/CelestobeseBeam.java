package com.feroov.frv.entity.projectile;

import com.feroov.frv.entity.AnimationConstants;
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
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;


public class CelestobeseBeam extends AbstractArrow implements GeoEntity
{
    // Tracks the number of ticks the beam has been in the air.
    private int ticksInAir;

    // The sound to play when the beam hits something.
    public SoundEvent hitSound = this.getDefaultHitGroundSoundEvent();

    // Cache for animation-related instances.
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // Data parameter used to sync an integer value between server and client.
    public static final EntityDataAccessor<Integer> PARTICLE = SynchedEntityData.defineId(CelestobeseBeam.class, EntityDataSerializers.INT);

    /**
     * Constructor for the CelestobeseBeam entity.
     * @param entityType The entity type of the CelestobeseBeam.
     * @param world The level in which the entity exists.
     */
    public CelestobeseBeam(EntityType<? extends CelestobeseBeam> entityType, Level world)
    {
        super(entityType, world);
        this.pickup = Pickup.DISALLOWED;
    }

    /**
     * Constructor for the CelestobeseBeam entity.
     * @param level The level in which the entity exists.
     * @param celestobese The entity that shot the beam.
     */
    public CelestobeseBeam(Level level, Celestobese celestobese)
    {
        super(EntitiesSTLCON.CELESTOBESE_BEAM.get(), celestobese, level);
    }

    /**
     * Called when the beam hits an entity. Applies damage and sets the target on fire.
     */
    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult)
    {
        super.onHitEntity(entityHitResult);

        Entity entity = entityHitResult.getEntity();
        Entity entity1 = this.getOwner();

        // Prevents the CelestobeseBeam from harming certain entities
        if (entity instanceof Mekkron || entity instanceof Celestroid || entity instanceof Celestobese || entity instanceof CelestobeseBeam)
            return;

        if (!this.level().isClientSide())
        {
            boolean flag;

            // Inflicts damage on the hit entity if the owner of the CelestobeseBeam is a LivingEntity
            if (entity1 instanceof LivingEntity livingentity)
            {
                flag = entity.hurt(this.damageSources().thrown(this, livingentity), 9.0F);
                if (flag)
                {
                    if (entity.isAlive()) { this.doEnchantDamageEffects(livingentity, entity); }
                }
            }
        }

        // Removes the CelestobeseBeam entity after hitting an entity
        if (!this.level().isClientSide()) this.remove(RemovalReason.DISCARDED);
    }

    /**
     * Handles the behavior when the CelestobeseBeam hits a block.
     *
     * @param blockHitResult The BlockHitResult representing the block that was hit.
     */
    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult)
    {
        super.onHitBlock(blockHitResult);
        this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

        BlockPos hitPos = blockHitResult.getBlockPos();
        BlockState hitBlockState = this.level().getBlockState(hitPos);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        // Spawns fire particles and creates fire blocks on impact with certain blocks
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

        // Removes the CelestobeseBeam entity after hitting a block
        if (!this.level().isClientSide())
            this.remove(RemovalReason.DISCARDED);
    }

    /**
     * Called every tick to update the entity's behavior.
     * This method handles the particle effects and the removal of the entity after a certain number of ticks.
     */
    @Override
    public void tick()
    {
        super.tick();
        ++this.ticksInAir;

        // Removes the CelestobeseBeam entity after reaching the maximum number of ticks
        if (this.ticksInAir >= 100) {if (!this.level().isClientSide()) {this.remove(RemovalReason.DISCARDED);}}

        // Spawns HAPPY_VILLAGER particles in a loop if running on the client-side
        if (this.level().isClientSide())
        {
            double offsetX = 0.1;
            double offsetY = 0.1;
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, true, this.getX() + offsetX, this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, true, this.getX(), this.getY() + offsetY, this.getZ(), 0, 0, 0);
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
     * Registers the animation controllers for this CelestobeseBeam entity.
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
     * Handles the despawn of the CelestobeseBeam entity by increasing the ticksInAir counter.
     * The entity is removed when the tick count exceeds a certain value.
     */
    @Override
    protected void tickDespawn() { ++this.ticksInAir; if (this.tickCount >= 40) { this.remove(RemovalReason.KILLED); }}

    /**
     * Shoots the CelestobeseBeam entity with the given parameters, resetting the ticksInAir counter.
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
     * Defines the synchronized entity data for the CelestobeseBeam entity.
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
     * Retrieves the item stack used for pickup. In this case, it returns null since the CelestobeseBeam
     * entity is not meant to be picked up by players.
     *
     * @return The ItemStack used for pickup (null in this case).
     */
    @Override
    protected @NotNull ItemStack getPickupItem() { return null; }

    /**
     * Sets the sound event for the hit effect of the CelestobeseBeam entity.
     *
     * @param soundIn The SoundEvent to be set as the hit sound.
     */
    @Override
    public void setSoundEvent(@NotNull SoundEvent soundIn) { this.hitSound = soundIn; }

    /**
     * Retrieves the default SoundEvent to be played when the CelestobeseBeam hits the ground.
     *
     * @return The default SoundEvent for hitting the ground.
     */
    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent()  { return SoundEvents.AMETHYST_BLOCK_CHIME; }

    /**
     * Specifies whether the CelestobeseBeam entity should display the fire animation.
     *
     * @return True if the fire animation should be displayed, false otherwise.
     */
    @Override
    public boolean displayFireAnimation() { return false; }
}
