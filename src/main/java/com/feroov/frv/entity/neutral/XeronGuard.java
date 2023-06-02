package com.feroov.frv.entity.neutral;


import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
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
import java.util.UUID;


public class XeronGuard extends Animal implements GeoEntity, NeutralMob
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    protected static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(XeronGuard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(XeronGuard.class, EntityDataSerializers.INT);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;

    public XeronGuard(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
        this.xpReward = 20;
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
    }

    public static AttributeSupplier setAttributes()
    {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.5D).build();
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(1, new XeronMeleeAttack(this, 0.95D, true));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Mob.class, 25.0F));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this)).setAlertOthers());
        this.goalSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, this::isAngryAt));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.73D));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.73D));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEventsSTLCON.XERON_AMBIENT.get(), 1.0F, 0.7F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEventsSTLCON.XERON_HURT.get(), 1.0F, 0.7F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.SHULKER_DEATH, 1.0F, 1.6F);
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
            animationState.getController().setAnimation(RawAnimation.begin().then("defensive", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
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
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    public void setAttacking(boolean attack) {
        this.entityData.set(ATTACKING, attack);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_30418_) {
        super.addAdditionalSaveData(p_30418_);
        this.addPersistentAngerSaveData(p_30418_);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_30402_) {
        super.readAdditionalSaveData(p_30402_);
        this.readPersistentAngerSaveData(this.level, p_30402_);
    }

    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level, true);
        }

    }
    @Override
    public int getRemainingPersistentAngerTime()
    {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int remainingPersistentAngerTime)
    {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, remainingPersistentAngerTime);
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    public static class XeronMeleeAttack extends MeleeAttackGoal
    {
        private XeronGuard entity;
        private int animCounter = 0;
        private int animTickLength = 19;

        public XeronMeleeAttack(PathfinderMob pathfinderMob, double speedModifier, boolean followingTargetEvenIfNotSeen)
        {
            super(pathfinderMob, speedModifier, followingTargetEvenIfNotSeen);
            if(pathfinderMob instanceof XeronGuard xeron)
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
