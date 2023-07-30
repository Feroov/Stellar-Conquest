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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;

import javax.annotation.Nonnull;


public class Skeleroid extends PathfinderMob implements GeoEntity
{
    // AnimatableInstanceCache for managing animations.
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // EntityDataAccessor for the attacking state.
    protected static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(Skeleroid.class, EntityDataSerializers.BOOLEAN);

    /**
     * Constructs a Skeleroid entity with the specified EntityType and Level.
     *
     * @param entityType The EntityType of the Skeleroid.
     * @param level      The Level in which the Skeleroid exists.
     */
    public Skeleroid(EntityType<? extends PathfinderMob> entityType, Level level)
    {
        super(entityType, level);
        this.xpReward = 5;
    }

    /**
     * Sets the attributes for the Skeleroid entity, such as max health, movement speed, follow range, and attack damage.
     *
     * @return The AttributeSupplier containing the Skeleroid's attributes.
     */
    public static AttributeSupplier setAttributes()
    {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.5D).build();
    }

    /**
     * Registers the goals (AI tasks) for the Skeleroid entity.
     * Adds goals for floating in water, melee attacking, looking at players, attacking players,
     * attacking XeronGuards, moving towards restrictions, and random wandering.
     */
    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SkeleroidMeleeAttack(this, 0.95D, true));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Mob.class, 25.0F));
        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, XeronGuard.class, true));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.3D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.77D));
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEvents.SKELETON_AMBIENT, 1.0F, 2.4F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEvents.SKELETON_HURT, 1.0F, 2.4F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.SKELETON_DEATH, 1.0F, 2.6F);
        return null;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos blockPos, @NotNull BlockState blockState)
    {
        this.playSound(SoundEvents.SKELETON_STEP, 0.15F, 1.6F);
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose poseIn, @NotNull EntityDimensions sizeIn) { return 1.0F; }

    /**
     * Registers animation controllers for this Skeleroid entity.
     * Overrides the default registerControllers method to add a custom AnimationController.
     *
     * @param controllerRegistrar The controller registrar to add the AnimationController.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "livingController", 0, event ->
        {
            if (isAttacking()) return event.setAndContinue(AnimationConstants.SWORD);
            if (isAggressive() && this.walkAnimation.speed() > 0.35F && this.onGround() && !this.swinging) return event.setAndContinue(AnimationConstants.RUN);
            if (event.isMoving() || this.swinging) return event.setAndContinue(AnimationConstants.WALK);
            return event.setAndContinue(AnimationConstants.IDLE);
        }));
    }

    /**
     * Gets the AnimatableInstanceCache for this Skeleroid entity.
     *
     * @return The AnimatableInstanceCache instance associated with this entity.
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    /**
     * Defines the synchronized entity data for this Skeleroid entity.
     * Overrides the default defineSynchedData method to add a custom data entry.
     */
    @Override
    protected void defineSynchedData()
    {
        // Call the superclass implementation of defineSynchedData first to define common synchronized data.
        super.defineSynchedData();

        // Define a new synchronized data entry for the "ATTACKING" boolean attribute.
        // This data will be used to track whether the Skeleroid is currently in an attacking state.
        this.entityData.define(ATTACKING, false);
    }

    public void setAttacking(boolean attack) { this.entityData.set(ATTACKING, attack); }

    public boolean isAttacking() { return this.entityData.get(ATTACKING); }

    /**
     * Represents the melee attack goal for the Skeleroid entity.
     * The SkeleroidMeleeAttack inherits from the MeleeAttackGoal class.
     */
    public static class SkeleroidMeleeAttack extends MeleeAttackGoal
    {
        private Skeleroid entity;
        private int animCounter = 0;

        /**
         * Constructs the SkeleroidMeleeAttack goal with the specified parameters.
         *
         * @param pathfinderMob              The entity that performs the attack (Skeleroid).
         * @param speedModifier              The movement speed modifier for the attack.
         * @param followingTargetEvenIfNotSeen Whether to continue following the target even if it's not seen.
         */
        public SkeleroidMeleeAttack(PathfinderMob pathfinderMob, double speedModifier, boolean followingTargetEvenIfNotSeen)
        {
            super(pathfinderMob, speedModifier, followingTargetEvenIfNotSeen);
            if(pathfinderMob instanceof Skeleroid xeron)
            {
                entity = xeron;
            }
        }

        /**
         * Checks if the target is within attack range and performs the attack if possible.
         * Sets the Skeleroid's attacking state to true when it starts the attack animation.
         *
         * @param livingEntity The target entity to attack.
         * @param d1           The distance to the target squared.
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
         * Updates the attack animation counter and resets the Skeleroid's attacking state
         * after the attack animation has played for a specific number of ticks.
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
         * Stops the attack animation and resets the Skeleroid's attacking state.
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
     * Inflicts damage to the target entity and applies a Hunger effect to the target if it is a LivingEntity.
     * Overrides the default doHurtTarget method to provide additional functionality.
     *
     * @param entityIn The target entity to be damaged.
     * @return True if the target entity was successfully damaged, false otherwise.
     */
    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn)
    {
        if (!super.doHurtTarget(entityIn))  { return false; }
        else
        {
            if (entityIn instanceof LivingEntity)
            {
                ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.HUNGER, 100));
            }
            return true;
        }
    }

    /**
     * Determines whether the Skeleroid should despawn in peaceful difficulty.
     * Overrides the default shouldDespawnInPeaceful method to return true, allowing despawning in peaceful difficulty.
     *
     * @return True if the Skeleroid can despawn in peaceful difficulty, false otherwise.
     */
    @Override
    protected boolean shouldDespawnInPeaceful() { return true; }
}

