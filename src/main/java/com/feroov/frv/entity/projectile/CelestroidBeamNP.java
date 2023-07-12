package com.feroov.frv.entity.projectile;

import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.monster.Celestroid;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class CelestroidBeamNP extends AbstractHurtingProjectile implements GeoEntity
{
    private int ticksInAir;
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public CelestroidBeamNP(EntityType<? extends AbstractHurtingProjectile> entityType, Level level)
    {
        super(entityType, level);
    }

    public CelestroidBeamNP(Level level, Celestroid celestroid, double d2, double d3, double d4)
    {
        super(EntitiesSTLCON.CELESTROID_BEAM_NP.get(), celestroid, d2, d3, d4, level);
    }

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
    protected void onHitEntity(EntityHitResult entityHitResult)
    {
        super.onHitEntity(entityHitResult);
        if (!this.level().isClientSide)
        {
            Entity entity = entityHitResult.getEntity();
            Entity entity1 = this.getOwner();
            boolean flag;

            if (entity1 instanceof LivingEntity)
            {
                LivingEntity livingentity = (LivingEntity)entity1;
                flag = entity.hurt(this.damageSources().thrown(this, livingentity), 3.5F);
                if (flag)
                {
                    if (entity.isAlive())
                    {
                        this.doEnchantDamageEffects(livingentity, entity);
                    }
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

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult)
    {
        super.onHitBlock(blockHitResult);
        this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

        if (!this.level().isClientSide())
            this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public boolean isOnFire() { return false; }

    @Override
    protected ParticleOptions getTrailParticle()  { return ParticleTypes.UNDERWATER; }
}
