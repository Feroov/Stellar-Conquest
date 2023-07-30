package com.feroov.frv.entity.projectile;

import com.feroov.frv.entity.AnimationConstants;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.misc.Stardusk;
import com.feroov.frv.entity.monster.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;

public class CelestroidBeam extends AbstractHurtingProjectile implements GeoEntity
{
    // Field to track ticks
    private int ticksInAir;

    // Animation cache for this entity
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    /**
     * Constructor for creating a CelestroidBeam entity.
     *
     * @param entityType The EntityType of the entity.
     * @param level      The Level in which the entity exists.
     */
    public CelestroidBeam(EntityType<? extends AbstractHurtingProjectile> entityType, Level level)
    {
        super(entityType, level);
    }

    /**
     * Constructor for creating a CelestroidBeam entity shot by a CelestroidShip entity.
     *
     * @param level          The Level in which the entity exists.
     * @param celestroidShip The CelestroidShip entity that shot the CelestroidBeam.
     * @param d2             The x-coordinate of the initial position.
     * @param d3             The y-coordinate of the initial position.
     * @param d4             The z-coordinate of the initial position.
     */
    public CelestroidBeam(Level level, CelestroidShip celestroidShip, double d2, double d3, double d4)
    {
        super(EntitiesSTLCON.CELESTROID_BEAM.get(), celestroidShip, d2, d3, d4, level);
    }

    /**
     * Registers the animation controllers for this CelestroidBeam entity.
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
     * Handles the behavior when the CelestroidBeam hits an entity.
     *
     * @param entityHitResult The EntityHitResult representing the entity that was hit.
     */
    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult)
    {
        super.onHitEntity(entityHitResult);

        if (!this.level().isClientSide)
        {
            Entity entity = entityHitResult.getEntity();
            Entity entity1 = this.getOwner();

            // Prevents the CelestroidBeam from harming certain entities
            if (entity instanceof Stardusk || entity instanceof Mekkron || entity instanceof Celestroid ||
                    entity instanceof Celestobese || entity instanceof CelestroidShip || entity instanceof Mothership)
                return;

            boolean flag;
            if (entity1 instanceof LivingEntity livingentity)
            {
                // Inflicts damage on the hit entity if the owner of the CelestroidBeam is a LivingEntity
                flag = entity.hurt(this.damageSources().thrown(this, livingentity), 8.0F);
                if (flag)
                {
                    if (entity.isAlive())
                    {
                        this.doEnchantDamageEffects(livingentity, entity);
                    }
                }
            }
            // Spawns FLASH and SQUID_INK particles when the CelestroidBeam hits an entity
            this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

        // Removes the CelestroidBeam entity after hitting an entity
        if (!this.level().isClientSide())
            this.remove(RemovalReason.DISCARDED);
    }

    /**
     * Handles the behavior when the CelestroidBeam hits a block.
     *
     * @param blockHitResult The BlockHitResult representing the block that was hit.
     */
    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult)
    {
        super.onHitBlock(blockHitResult);

        // Spawns FLASH and SQUID_INK particles when the CelestroidBeam hits a block
        this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

        // Removes the CelestroidBeam entity after hitting a block
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

        // Removes the CelestroidBeam entity after reaching the maximum number of ticks
        if (this.ticksInAir >= 140) { this.remove(RemovalReason.DISCARDED); }
    }

    /**
     * Checks if the arrow is on fire.
     *
     * @return Always returns false, as CelestroidBeam is not on fire.
     */
    @Override
    public boolean isOnFire() { return false; }

    /**
     * Retrieves the ParticleOptions used for the trail of the CelestroidBeam entity.
     *
     * @return The ParticleOptions for the trail.
     */
    @Override
    protected @NotNull ParticleOptions getTrailParticle() { return ParticleTypes.UNDERWATER; }
}
