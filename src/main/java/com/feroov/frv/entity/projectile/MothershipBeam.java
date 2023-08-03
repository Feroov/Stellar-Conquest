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
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;

public class MothershipBeam extends AbstractHurtingProjectile implements GeoEntity
{

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final int power = 2;
    private int ticksInAir;

    /**
     * Constructs a MothershipBeam projectile with the specified entity type and level.
     *
     * @param entityType The entity type of the projectile.
     * @param level      The level where the projectile exists.
     */
    public MothershipBeam(EntityType<? extends AbstractHurtingProjectile> entityType, Level level)
    {
        super(entityType, level);
    }

    /**
     * Constructs a MothershipBeam projectile with the specified level, mothership owner, and position.
     *
     * @param level      The level where the projectile exists.
     * @param mothership The Mothership entity that owns this projectile.
     * @param d2         The x-coordinate of the projectile's position.
     * @param d3         The y-coordinate of the projectile's position.
     * @param d4         The z-coordinate of the projectile's position.
     */
    public MothershipBeam(Level level, Mothership mothership, double d2, double d3, double d4)
    {
        super(EntitiesSTLCON.MOTHERSHIP_BEAM.get(), mothership, d2, d3, d4, level);
    }

    /**
     * Registers the animation controllers for this MothershipBeam entity.
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
     * Handles the behavior when the MothershipBeam hits an entity.
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

            // Ignore hitting specific entities
            if (entity instanceof Stardusk || entity instanceof Mekkron || entity instanceof Celestroid ||
                    entity instanceof Celestobese || entity instanceof CelestroidShip || entity instanceof Mothership)
            {
                return;
            }

            boolean flag;

            if (entity1 instanceof LivingEntity livingentity)
            {
                // Hurt the entity with damage from this projectile.
                flag = entity.hurt(this.damageSources().thrown(this, livingentity), 15.0F);

                // Apply enchantment damage effects to the entity if it's still alive.
                if (flag)  { if (entity.isAlive()) { this.doEnchantDamageEffects(livingentity, entity); } }
            }

            // Add particles when the projectile hits an entity.
            this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

        // Create an explosion at the projectile's position with a specific power.
        boolean flag = ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner());
        this.level().explode(null, this.getX(), this.getY(), this.getZ(), this.power, flag, Level.ExplosionInteraction.NONE);
        this.discard();
    }

    /**
     * Handles the behavior when the MothershipBeam hits a block.
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

        // Create an explosion at the projectile's position with a specific power.
        boolean flag = ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner());
        this.level().explode(null, this.getX(), this.getY(), this.getZ(), (float) this.power, flag, Level.ExplosionInteraction.NONE);
        this.discard();
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
        if (this.ticksInAir >= 140) { this.remove(RemovalReason.DISCARDED); }
    }

    // This projectile is not on fire.
    @Override
    public boolean isOnFire() { return false; }

    @Override
    protected @NotNull ParticleOptions getTrailParticle()  { return ParticleTypes.SONIC_BOOM; }
}
