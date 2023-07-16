package com.feroov.frv.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nonnull;


public class Mekkron extends Monster implements GeoEntity
{
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS));
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    protected static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(Mekkron.class, EntityDataSerializers.BOOLEAN);

    /**
     * Constructs a new Mekkron entity.
     *
     * @param entityType The entity type.
     * @param level      The level.
     */
    public Mekkron(EntityType<? extends Monster> entityType, Level level)
    {
        super(entityType, level);
        this.xpReward = 120;
    }

    /**
     * Sets the attributes for the Mekkron entity.
     *
     * @return The attribute supplier.
     */
    public static AttributeSupplier setAttributes()
    {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 350.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.5D).build();
    }

    /**
     * Registers the goals for the Mekkron entity.
     */
    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MekkronMeleeAttack(this, 0.95D, true));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Mob.class, 25.0F));
        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.3D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.77D));
    }

    /**
     * Gets the ambient, hurt, death, step sound for the Mekkron entity.
     *
     * @return The ambient, hurt, death, step sound.
     */
    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 0.4F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEvents.ENDER_DRAGON_HURT, 1.0F, 0.4F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.IRON_GOLEM_DEATH, 1.0F, 2.6F);
        return null;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState)
    {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 2.0F, 0.6F);
    }

    /**
     * Gets the standing eye height for the Mekkron entity.
     *
     * @param poseIn  The pose of the entity.
     * @param sizeIn  The dimensions of the entity.
     * @return The standing eye height.
     */
    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) { return 3.8F; }

    /**
     * Registers the animation controllers for the Mekkron entity.
     *
     * @param controllerRegistrar The controller registrar.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    /**
     * Determines the animation state for the Mekkron entity.
     *
     * @param animationState The animation state.
     * @param <T>            The type of the GeoAnimatable.
     * @return The play state.
     */
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
            animationState.getController().setAnimation(RawAnimation.begin().then("aggresive", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    /**
     * Defines the synched data for the Mekkron entity.
     */
    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
    }

    /**
     * Sets the attacking state for the Mekkron entity.
     *
     * @param attack The attacking state.
     */
    public void setAttacking(boolean attack) { this.entityData.set(ATTACKING, attack); }

    /**
     * Checks if the Mekkron entity is attacking.
     *
     * @return True if attacking, false otherwise.
     */
    public boolean isAttacking() { return this.entityData.get(ATTACKING); }

    /**
     * Represents the melee attack goal for the Mekkron entity.
     */
    public static class MekkronMeleeAttack extends MeleeAttackGoal
    {
        private Mekkron entity;
        private int attackCooldown = 60;
        private int attackTimer = 0;
        private int animCounter = 0;
        private int animTickLength = 25;

        /**
         * Constructs a new MekkronMeleeAttack goal.
         *
         * @param pathfinderMob               The pathfinder mob.
         * @param speedModifier               The speed modifier.
         * @param followingTargetEvenIfNotSeen Whether to follow the target even if not seen.
         */
        public MekkronMeleeAttack(PathfinderMob pathfinderMob, double speedModifier, boolean followingTargetEvenIfNotSeen)
        {
            super(pathfinderMob, speedModifier, followingTargetEvenIfNotSeen);
            if (pathfinderMob instanceof Mekkron mekkron)
            {
                entity = mekkron;
            }
        }

        /**
         * Checks and performs the attack for the MekkronMeleeAttack goal.
         *
         * @param livingEntity The living entity.
         * @param d1           The distance squared.
         */
        @Override
        protected void checkAndPerformAttack(LivingEntity livingEntity, double d1)
        {
            if (attackTimer <= 0 && d1 <= this.getAttackReachSqr(livingEntity))
            {
                if (entity != null) { entity.setAttacking(true); animCounter = 0; }
                super.checkAndPerformAttack(livingEntity, d1);
                attackTimer = attackCooldown;
            }
        }

        /**
         * Updates the MekkronMeleeAttack goal.
         */
        @Override
        public void tick()
        {
            super.tick();
            if (attackTimer > 0) { attackTimer--; }

            if (entity != null && entity.isAttacking())
            {
                animCounter++;
                if (animCounter >= animTickLength)
                {
                    animCounter = 0;
                    entity.setAttacking(false);
                }
            }
        }

        /**
         * Stops the MekkronMeleeAttack goal.
         */
        @Override
        public void stop()
        {
            attackTimer = 0;
            animCounter = 0;
            if (entity != null) { entity.setAttacking(false); }
            super.stop();
        }
    }

    /**
     * Starts tracking the Mekkron entity by the specified player.
     *
     * @param player The player to start tracking.
     */
    @Override
    public void startSeenByPlayer(ServerPlayer player)
    {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    /**
     * Stops tracking the Mekkron entity by the specified player.
     *
     * @param player The player to stop tracking.
     */
    @Override
    public void stopSeenByPlayer(ServerPlayer player)
    {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    /**
     * Sets the custom name for the Mekkron entity.
     *
     * @param name The custom name.
     */
    @Override
    public void setCustomName(Component name)
    {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    /**
     * Executes custom server AI step logic for the Mekkron entity.
     */
    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    protected boolean shouldDespawnInPeaceful() { return true; }
}
