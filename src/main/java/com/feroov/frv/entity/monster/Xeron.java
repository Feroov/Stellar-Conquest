package com.feroov.frv.entity.monster;


import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nonnull;


public class Xeron extends Animal implements GeoEntity
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    protected static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(Xeron.class, EntityDataSerializers.BOOLEAN);

    public Xeron(EntityType<? extends Animal> entityType, Level level) { super(entityType, level); }

    public static AttributeSupplier setAttributes()
    {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D).build();
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Mob.class, 25.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.73D));
        this.goalSelector.addGoal(7, new MoveTowardsRestrictionGoal(this, 0.73D));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEventsSTLCON.XERON_AMBIENT.get(), 1.0F, 1.0F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEventsSTLCON.XERON_HURT.get(), 1.0F, 1.0F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.SHULKER_DEATH, 1.0F, 2.0F);
        return null;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) { return 1.0F; }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState)
    {
        if (isAttacking())
        {
            animationState.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

        if (!(walkAnimation.speed() > -0.10F && walkAnimation.speed() < 0.10F) && !this.isAggressive())
        {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(isAggressive())
        {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    protected void defineSynchedData() { super.defineSynchedData(); this.entityData.define(ATTACKING, false); }

    public void setAttacking(boolean attack) {
        this.entityData.set(ATTACKING, attack);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public static class XeronMeleeAttack extends MeleeAttackGoal
    {
        private Xeron entity;
        private int animCounter = 0;
        private int animTickLength = 19;

        public XeronMeleeAttack(PathfinderMob pathfinderMob, double speedModifier, boolean followingTargetEvenIfNotSeen)
        {
            super(pathfinderMob, speedModifier, followingTargetEvenIfNotSeen);
            if(pathfinderMob instanceof Xeron xeron)
            {
                entity = xeron;
            }
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity p_25557_, double p_25558_)
        {
            if (p_25558_ <= this.getAttackReachSqr(p_25557_) && this.getTicksUntilNextAttack() <= 0)
            {
                if(entity != null)
                {
                    entity.setAttacking(true);
                    animCounter = 0;
                }
            }

            super.checkAndPerformAttack(p_25557_, p_25558_);
        }

        @Override
        public void tick()
        {
            super.tick();
            if(entity.isAttacking())
            {
                animCounter++;

                if(animCounter >= animTickLength)
                {
                    animCounter = 0;
                    entity.setAttacking(false);
                }
            }
        }

        @Override
        public void stop()
        {
            animCounter = 0;
            entity.setAttacking(false);
            super.stop();
        }
    }
}
