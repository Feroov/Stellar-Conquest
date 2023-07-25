package com.feroov.frv.entity.projectile;

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
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class MothershipBeam extends AbstractHurtingProjectile implements GeoEntity
{

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final int power = 2;
    private int ticksInAir;

    public MothershipBeam(EntityType<? extends AbstractHurtingProjectile> entityType, Level level)
    {
        super(entityType, level);
    }

    public MothershipBeam(Level level, Mothership mothership, double d2, double d3, double d4) {
        super(EntitiesSTLCON.MOTHERSHIP_BEAM.get(), mothership, d2, d3, d4, level);
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
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult)
    {
        super.onHitEntity(entityHitResult);
        if (!this.level().isClientSide)
        {
            Entity entity = entityHitResult.getEntity();
            Entity entity1 = this.getOwner();
            if (entity instanceof Stardusk)  { return; }
            if (entity instanceof Mekkron) { return; }
            if (entity instanceof Celestroid) { return; }
            if (entity instanceof Celestobese) { return; }
            if (entity instanceof CelestroidShip) { return; }
            if (entity instanceof Mothership) { return; }

            boolean flag;

            if (entity1 instanceof LivingEntity livingentity)
            {
                flag = entity.hurt(this.damageSources().thrown(this, livingentity), 15.0F);
                if (flag)  { if (entity.isAlive()) { this.doEnchantDamageEffects(livingentity, entity); } }
            }
            this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

        }

        boolean flag = ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner());
        this.level().explode(null, this.getX(), this.getY(), this.getZ(), this.power, flag, Level.ExplosionInteraction.NONE);
        this.discard();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult)
    {
        super.onHitBlock(blockHitResult);

        this.level().addParticle(ParticleTypes.FLASH, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        this.level().addParticle(ParticleTypes.SQUID_INK, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

        boolean flag = ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner());
        this.level().explode(null, this.getX(), this.getY(), this.getZ(), (float) this.power, flag, Level.ExplosionInteraction.NONE);
        this.discard();
    }

    @Override
    public boolean isOnFire() { return false; }

    @Override
    public void tick()
    {
        super.tick();
        ++this.ticksInAir;
        if (this.ticksInAir >= 140) { this.remove(RemovalReason.DISCARDED); }
    }

    @Override
    protected @NotNull ParticleOptions getTrailParticle()  { return ParticleTypes.SONIC_BOOM; }
}
