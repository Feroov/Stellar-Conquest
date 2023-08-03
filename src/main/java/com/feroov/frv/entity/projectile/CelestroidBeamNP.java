package com.feroov.frv.entity.projectile;

import com.feroov.frv.entity.AnimationConstants;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.monster.Celestobese;
import com.feroov.frv.entity.monster.Celestroid;
import com.feroov.frv.entity.monster.Mekkron;
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

public class CelestroidBeamNP extends AbstractHurtingProjectile implements GeoEntity
{
    private int ticksInAir;

    // AnimatableInstanceCache used to cache the animation state for this projectile.
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    /**
     * Constructs a CelestroidBeamNP projectile with the specified entity type and level.
     *
     * @param entityType The entity type of the projectile.
     * @param level      The level where the projectile exists.
     */
    public CelestroidBeamNP(EntityType<? extends AbstractHurtingProjectile> entityType, Level level)
    {
        super(entityType, level);
    }

    /**
     * Constructs a CelestroidBeamNP projectile with the specified level, celestroid owner, and position.
     *
     * @param level      The level where the projectile exists.
     * @param celestroid The Celestroid entity that owns this projectile.
     * @param d2         The x-coordinate of the projectile's position.
     * @param d3         The y-coordinate of the projectile's position.
     * @param d4         The z-coordinate of the projectile's position.
     */
    public CelestroidBeamNP(Level level, Celestroid celestroid, double d2, double d3, double d4)
    {
        super(EntitiesSTLCON.CELESTROID_BEAM_NP.get(), celestroid, d2, d3, d4, level);
    }

    /**
     * Registers the animation controllers for this CelestroidBeamNP entity.
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

    /**
     * Handles the behavior when the CelestroidBeamNP hits an entity.
     *
     * @param entityHitResult The EntityHitResult representing the entity that was hit.
     */
    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult)
    {
        super.onHitEntity(entityHitResult);

        Entity entity = entityHitResult.getEntity();
        Entity entity1 = this.getOwner();

        // Ignore hitting specific entities
        if (entity instanceof Mekkron || entity instanceof Celestroid || entity instanceof Celestobese)
        {
            return;
        }
        if (!this.level().isClientSide())
        {
            boolean flag;

            if (entity1 instanceof LivingEntity livingentity)
            {
                // Hurt the entity with damage from this projectile.
                flag = entity.hurt(this.damageSources().thrown(this, livingentity), 3.5F);
                if (flag)
                {
                    // Apply enchantment damage effects to the entity if it's still alive.
                    if (entity.isAlive()) { this.doEnchantDamageEffects(livingentity, entity); }
                }
            }
        }
        // Remove the projectile after hitting an entity.
        if (!this.level().isClientSide()) this.remove(RemovalReason.DISCARDED);
    }

    /**
     * Handles the behavior when the CelestroidBeamNP hits a block.
     *
     * @param blockHitResult The BlockHitResult representing the block that was hit.
     */
    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult)
    {
        super.onHitBlock(blockHitResult);
        // Add particles when the projectile hits a block.
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
            double offsetX = 0.1;
            double offsetY = 0.1;
            double offsetZ = 0.1;

            double motionX = this.getDeltaMovement().x;
            double motionY = this.getDeltaMovement().y;
            double motionZ = this.getDeltaMovement().z;

            double posX = this.getX() - motionX * offsetX;
            double posY = this.getY() - motionY * offsetY;
            double posZ = this.getZ() - motionZ * offsetZ;


            this.level().addParticle(ParticleTypes.SCULK_CHARGE_POP, true, posX, posY, posZ, 0, 0, 0);
        }
    }

    // This projectile is not on fire.
    @Override
    public boolean isOnFire() { return false; }

    @Override
    protected @NotNull ParticleOptions getTrailParticle()  { return ParticleTypes.UNDERWATER; }
}
