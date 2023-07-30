package com.feroov.frv.entity.monster;

import com.feroov.frv.entity.AnimationConstants;
import com.feroov.frv.entity.neutral.XeronGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;

import javax.annotation.Nonnull;


public class Xenaptor extends Spider implements GeoEntity
{
    // AnimatableInstanceCache for managing animations.
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // EntityDataAccessor for the attacking state.
    protected static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(Skeleroid.class, EntityDataSerializers.BOOLEAN);
    /**
     * Constructs a new Xenaptor entity with the specified entity type and level.
     *
     * @param entityType The entity type of the Xenaptor.
     * @param level The level in which the Xenaptor will exist.
     */
    public Xenaptor(EntityType<? extends Spider> entityType, Level level)
    {
        super(entityType, level);
        this.xpReward = 5;
    }

    /**
     * Sets the attributes for the Xenaptor mob.
     * Configures various attributes such as max health, movement speed, follow range, and attack damage.
     *
     * @return The AttributeSupplier containing the configured attributes for the Xenaptor.
     */
    public static AttributeSupplier setAttributes()
    {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.5D).build();
    }

    /**
     * Registers the AI goals (behaviors) for the Xenaptor entity.
     * Adds various AI tasks such as floating in water, attacking, targeting players, and random wandering.
     */
    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new XenaptorMeleeAttack(this, 0.95D, true));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Mob.class, 25.0F));
        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, XeronGuard.class, true));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.3D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.77D));
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEvents.SPIDER_AMBIENT, 1.0F, 0.4F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEvents.SPIDER_HURT, 1.0F, 0.4F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.GHAST_DEATH, 1.0F, 2.6F);
        return null;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos blockPos, @NotNull BlockState blockState)
    {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.6F);
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose poseIn, @NotNull EntityDimensions sizeIn) { return 0.5F; }

    /**
     * Registers animation controllers for this Xenaptor entity.
     * Overrides the default registerControllers method to add a custom AnimationController.
     *
     * @param controllerRegistrar The controller registrar to add the AnimationController.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "livingController", 0, event ->
        {
            if (isAttacking()) return event.setAndContinue(AnimationConstants.ATTACK);
            if (isAggressive() && this.walkAnimation.speed() > 0.35F && this.onGround() && !this.swinging) return event.setAndContinue(AnimationConstants.RUN);
            if (event.isMoving() || this.swinging) return event.setAndContinue(AnimationConstants.WALK);
            return event.setAndContinue(AnimationConstants.IDLE);
        }));
    }

    /**
     * Gets the AnimatableInstanceCache for this Xenaptor entity.
     *
     * @return The AnimatableInstanceCache instance associated with this entity.
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    /**
     * Defines the synchronized entity data for this Xenaptor entity.
     * Overrides the default defineSynchedData method to add a custom data entry.
     */
    @Override
    protected void defineSynchedData()
    {
        // Call the superclass implementation of defineSynchedData first to define common synchronized data.
        super.defineSynchedData();

        // Define a new synchronized data entry for the "ATTACKING" boolean attribute.
        // This data will be used to track whether the Xenaptor is currently in an attacking state.
        this.entityData.define(ATTACKING, false);
    }

    public void setAttacking(boolean attack) { this.entityData.set(ATTACKING, attack); }

    public boolean isAttacking() { return this.entityData.get(ATTACKING); }

    /**
     * Represents a custom melee attack goal for the Xenaptor entity.
     * This goal is responsible for performing melee attacks on the target entity.
     */
    public static class XenaptorMeleeAttack extends MeleeAttackGoal
    {
        private Xenaptor entity;
        private int animCounter = 0;

        /**
         * Constructs a new XenaptorMeleeAttack goal for the given entity.
         *
         * @param pathfinderMob The Xenaptor entity that will be performing the melee attacks.
         * @param speedModifier The speed modifier for the melee attack.
         * @param followingTargetEvenIfNotSeen Whether to follow the target even if it's not visible.
         */
        public XenaptorMeleeAttack(PathfinderMob pathfinderMob, double speedModifier, boolean followingTargetEvenIfNotSeen)
        {
            super(pathfinderMob, speedModifier, followingTargetEvenIfNotSeen);
            if(pathfinderMob instanceof Xenaptor xeron)
            {
                entity = xeron;
            }
        }

        /**
         * Checks and performs the melee attack on the target entity.
         * If the target is within attack range and the attack cooldown is ready, the Xenaptor sets its 'attacking' state.
         *
         * @param livingEntity The target entity that the Xenaptor is attacking.
         * @param d1 The squared distance to the target entity.
         */
        @Override
        protected void checkAndPerformAttack(@NotNull LivingEntity livingEntity, double d1)
        {
            if (d1 <= this.getAttackReachSqr(livingEntity) && this.getTicksUntilNextAttack() <= 0)
            {
                if(entity != null)
                {
                    entity.setAttacking(true);
                    animCounter = 0;
                }
            }
            super.checkAndPerformAttack(livingEntity, d1);
        }

        /**
         * Updates the Xenaptor's attack animation counter and stops the attack animation when it's complete.
         */
        @Override
        public void tick()
        {
            super.tick();
            if(entity.isAttacking())
            {
                animCounter++;

                int animTickLength = 19;
                if(animCounter >= animTickLength)
                {
                    animCounter = 0;
                    entity.setAttacking(false);
                }
            }
        }

        /**
         * Stops the Xenaptor's attack animation and resets the animation counter.
         */
        @Override
        public void stop()
        {
            animCounter = 0;
            entity.setAttacking(false);
            super.stop();
        }
    }

    /**
     * Applies a custom effect when the Xenaptor successfully hurts a target entity.
     * In this case, the target entity receives the MobEffectInstance of poison for a duration of 100 ticks.
     *
     * @param entityIn The entity that the Xenaptor has successfully hurt.
     * @return True if the entity was hurt successfully and false otherwise.
     */
    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn)
    {
        if (!super.doHurtTarget(entityIn))  { return false; }
        else
        {
            if (entityIn instanceof LivingEntity)
            {
                ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, 100));
            }
            return true;
        }
    }

    /**
     * Determines whether the Xenaptor should despawn in peaceful difficulty.
     * This method returns true, meaning the Xenaptor will despawn in peaceful difficulty.
     *
     * @return True if the Xenaptor should despawn in peaceful difficulty, false otherwise.
     */
    @Override
    public boolean shouldDespawnInPeaceful() { return true; }
}
