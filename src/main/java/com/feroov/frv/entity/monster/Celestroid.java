package com.feroov.frv.entity.monster;

import com.feroov.frv.entity.neutral.XeronGuard;
import com.feroov.frv.entity.passive.Xeron;
import com.feroov.frv.entity.projectile.CelestroidBeamNP;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class Celestroid extends Monster implements GeoEntity
{
    public static final EntityDataAccessor<Integer> ATTACK = SynchedEntityData.defineId(Celestroid.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    /**
     * Constructs a Celestroid entity.
     *
     * @param type  The entity type.
     * @param level The level in which the entity exists.
     */
    public Celestroid(EntityType<? extends Celestroid> type, Level level)
    {
        super(type, level);
        this.xpReward = 15;
    }

    @Override
    protected void registerGoals()
    {
        // Register entity goals
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this,true));
        this.targetSelector.addGoal(2, new CelestroidAttackGoal(this, 0.5D, true, 3));
        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Xeron.class, true));
        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, XeronGuard.class, true));
        this.goalSelector.addGoal(4, new CelestroidRangedAttackGoal(this, 0.4D, 32.0D, 23.0F, 0));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.4D));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    /**
     * Sets the entity's attributes.
     *
     * @return The attribute supplier with the configured attributes.
     */
    public static AttributeSupplier setAttributes()
    {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.FOLLOW_RANGE, 23.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.6f).build();
    }

    // Override sound methods to play appropriate sounds
    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEventsSTLCON.XERON_AMBIENT.get(), 1.5F, 0.8F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEventsSTLCON.XERON_HURT.get(), 1.5F, 0.8F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.SHULKER_DEATH, 4.0F, 5.2F);
        return null;
    }

    // Indicates whether persistence is required for the Celestroid entity.
    // If true, the entity will not be removed when the chunk unloads or when the world is saved.
    @Override
    public boolean isPersistenceRequired() { return super.isPersistenceRequired(); }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) { return 1.0F; }

    @Override
    protected boolean shouldDespawnInPeaceful() { return true; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackController", 0, this::attack));
    }

    /**
     * Animation predicate that determines the animation to play based on the entity's movement state.
     *
     * @param animationState The animation state.
     * @return The play state of the animation.
     */
    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState)
    {
        if(animationState.isMoving())
        {
            animationState.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    /**
     * Animation predicate that determines the attack animation to play based on the entity's aggressive state.
     *
     * @param event The animation state.
     * @return The play state of the attack animation.
     */
    private <E extends GeoEntity> PlayState attack(AnimationState<E> event)
    {
        if (isAggressive())
        {
            event.getController().setAnimation(RawAnimation.begin().then("attack", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    /**
     * Performs a ranged attack towards the target entity.
     *
     * @param targetEntity  The target entity.
     * @param partialTicks  The partial tick time.
     */
    public void performRangedAttack(LivingEntity targetEntity, float partialTicks)
    {
        Vec3 eyePos = getEyePosition(partialTicks);

        double targetX = targetEntity.getX() + targetEntity.getBbWidth() / 2.0;
        double targetY = targetEntity.getBoundingBox().minY + targetEntity.getBbHeight() / 2.0;
        double targetZ = targetEntity.getZ() + targetEntity.getBbWidth() / 2.0;

        double d2 = targetX - eyePos.x;
        double d3 = targetY - eyePos.y;
        double d4 = targetZ - eyePos.z;

        double velocityFactor = 1.15;

        CelestroidBeamNP raygunBeam = new CelestroidBeamNP(level(), this, d2, d3, d4);
        raygunBeam.shoot(d2, d3, d4, (float) velocityFactor, 0.1F);
        playSound(SoundEventsSTLCON.RAYGUN_SHOOT.get(), 7.0F, 1.0F);
        raygunBeam.setPos(eyePos.x, eyePos.y, eyePos.z);
        level().addFreshEntity(raygunBeam);

        if (getRandom().nextInt(6) == 0) { setTarget(null); }
    }

    /**
     * Checks if the Celestroid can fire the specified projectile weapon.
     *
     * @param projectileWeaponItem The projectile weapon item.
     * @return True if the Celestroid can fire the projectile weapon, false otherwise.
     */
    public boolean canFireProjectileWeapon(ProjectileWeaponItem projectileWeaponItem) { return projectileWeaponItem == Items.BOW; }

    /**
     * Defines the synchronized entity data for the Celestroid.
     */
    @Override
    protected void defineSynchedData() { super.defineSynchedData(); this.entityData.define(ATTACK, 1); }

    /**
     * Sets the attacking state of the Celestroid.
     *
     * @param time The time of the attacking state.
     */
    public void setAttackingState(int time) { this.entityData.set(ATTACK, time); }

    /**
     * Represents a ranged attack goal for the Celestroid.
     */
    public static class CelestroidRangedAttackGoal extends Goal
    {
        // The Celestroid and the ranged attack mob
        private final Celestroid mob;
        private final Celestroid rangedAttackMob;

        // The target entity and attack-related variables
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private int seeTime, statecheck;

        // Attack parameters
        private final double attackIntervalMin, attackIntervalMax, speedModifier;
        private final float attackRadius, attackRadiusSqr;

        // Strafing variables
        private boolean strafingClockwise, strafingBackwards;
        private int strafingTime = -1;

        /**
         * Constructs a CelestroidRangedAttackGoal for the Celestroid.
         *
         * @param celestroid        The Celestroid entity.
         * @param speedIn           The speed modifier.
         * @param dpsIn             The minimum attack interval.
         * @param rangeIn           The attack range.
         * @param state             The attack state.
         */
        public CelestroidRangedAttackGoal(Celestroid celestroid, double speedIn, double dpsIn, float rangeIn, int state)
        {
            this(celestroid, speedIn, dpsIn, dpsIn, rangeIn, state);
        }

        /**
         * Constructs a CelestroidRangedAttackGoal for the Celestroid with variable attack intervals.
         *
         * @param celestroid        The Celestroid entity.
         * @param speedIn           The speed modifier.
         * @param atckIntervalMin   The minimum attack interval.
         * @param atckIntervalMax   The maximum attack interval.
         * @param atckRadius        The attack radius.
         * @param state             The attack state.
         */
        public CelestroidRangedAttackGoal(Celestroid celestroid, double speedIn, double atckIntervalMin, double atckIntervalMax, float atckRadius, int state)
        {
            if (celestroid == null)
            {
                throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
            }
            else
            {
                this.rangedAttackMob =  celestroid;
                this.mob =  celestroid;
                this.speedModifier = speedIn;
                this.attackIntervalMin = atckIntervalMin;
                this.attackIntervalMax = atckIntervalMax;
                this.attackRadius = atckRadius;
                this.attackRadiusSqr = atckRadius * atckRadius;
                this.statecheck = state;

                this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            }
        }

        public boolean canUse()
        {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) { this.target = livingentity; return true; }
            else  { return false; }
        }

        public boolean canContinueToUse() { return this.canUse() || !this.mob.getNavigation().isDone(); }

        public void stop()
        {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
        }

        public boolean requiresUpdateEveryTick() { return true; }

        public void tick()
        {
            LivingEntity livingentity = this.mob.getTarget();
            double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
            if (flag) { ++this.seeTime; } else { this.seeTime = 0; }

            if (!(d0 > (double)this.attackRadiusSqr) && this.seeTime >= 5) {
                this.mob.getNavigation().stop();
            } else { this.mob.getNavigation().moveTo(this.target, this.speedModifier); }

            if (livingentity != null)
            {
                boolean flag1 = this.seeTime > 0;
                if (flag != flag1) { this.seeTime = 0;}
                if (flag) { ++this.seeTime;} else {--this.seeTime;}
                if (!(d0 > (double)this.attackRadiusSqr) && this.seeTime >= 20)
                {
                    this.mob.getNavigation().stop();
                    ++this.strafingTime;
                } else { this.mob.getNavigation().moveTo(livingentity, this.speedModifier); this.strafingTime = -1; }

                if (this.strafingTime >= 20)
                {
                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) { this.strafingClockwise = !this.strafingClockwise; }
                    if ((double)this.mob.getRandom().nextFloat() < 0.3D) { this.strafingBackwards = !this.strafingBackwards; }
                    this.strafingTime = 0;
                }

                if (this.strafingTime > -1)
                {
                    if (d0 > (double)(this.attackRadiusSqr * 0.75F)) { this.strafingBackwards = false; }
                    else if (d0 < (double)(this.attackRadiusSqr * 0.25F)) { this.strafingBackwards = true; }
                    //speed shit
                    this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.0F : 0.0F, this.strafingClockwise ? 0.3F : -0.3F);
                    this.mob.lookAt(livingentity, 30.0F, 30.0F);
                }
                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                if (--this.attackTime == 0)
                {
                    if (!flag) { return; }
                    if (this.mob.isUsingItem())
                    {
                        if (!flag && this.seeTime < -60) { this.mob.stopUsingItem(); }
                        else if (flag)
                        {
                            int i = this.mob.getTicksUsingItem();
                            if (i >= 19) { this.mob.setAttackingState(statecheck); }
                            if (i >= 20) { this.mob.stopUsingItem(); }
                        }
                    }
                    float f = (float)Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.rangedAttackMob.performRangedAttack(this.target, f1);
                    this.attackTime = Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
                }
                else if (this.attackTime < 0)
                {
                    this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0)
                            / (double)this.attackRadius, (double)this.attackIntervalMin, (double)this.attackIntervalMax));
                }

            }
        }
    }

    /**
     * Represents a melee attack goal for the Celestroid.
     */
    static class CelestroidAttackGoal extends MeleeAttackGoal
    {
        // The Celestroid entity
        private final Celestroid entity;

        // Attack-related variables
        private final double speedModifier;
        private int statecheck, ticksUntilNextPathRecalculation, ticksUntilNextAttack;
        private double pathedTargetX, pathedTargetY, pathedTargetZ;

        /**
         * Constructs a CelestroidAttackGoal for the Celestroid.
         *
         * @param celestroid     The Celestroid entity.
         * @param speedIn        The speed modifier.
         * @param longMemoryIn   The long memory flag.
         * @param state          The attack state.
         */
        public CelestroidAttackGoal(Celestroid celestroid, double speedIn, boolean longMemoryIn, int state)
        {
            super(celestroid, speedIn, longMemoryIn);
            this.entity = celestroid;
            this.statecheck = state;
            this.speedModifier = speedIn;
        }

        public void start() { super.start();}
        public boolean canUse() { return super.canUse();}

        public void stop() { super.stop(); this.entity.setAggressive(false); this.entity.setAttackingState(0); }

        public void tick()
        {
            LivingEntity livingentity = this.entity.getTarget();
            if (livingentity != null)
            {
                this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                if ((this.mob.getSensing().hasLineOfSight(livingentity))
                        && this.ticksUntilNextPathRecalculation <= 0
                        && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D
                        || livingentity.distanceToSqr(this.pathedTargetX, this.pathedTargetY,
                        this.pathedTargetZ) >= 1.0D
                        || this.mob.getRandom().nextFloat() < 0.05F))
                {
                    this.pathedTargetX = livingentity.getX();
                    this.pathedTargetY = livingentity.getY();
                    this.pathedTargetZ = livingentity.getZ();
                    this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                    if (d0 > 1024.0D) { this.ticksUntilNextPathRecalculation += 10; }
                    else if (d0 > 256.0D) { this.ticksUntilNextPathRecalculation += 5;}
                    if (!this.mob.getNavigation().moveTo(livingentity, this.speedModifier))
                    {
                        this.ticksUntilNextPathRecalculation += 15;
                    }
                }
                this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 0, 0);
                this.checkAndPerformAttack(livingentity, d0);
            }
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity livingentity, double squaredDistance)
        {
            double d0 = this.getAttackReachSqr(livingentity);
            if (squaredDistance <= d0 && this.getTicksUntilNextAttack() <= 0)
            {
                this.resetAttackCooldown();
                this.entity.setAttackingState(statecheck);
                this.mob.doHurtTarget(livingentity);
            }
        }

        @Override
        protected int getAttackInterval() { return 50;}

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget)
        {
            return this.mob.getBbWidth() * 1.0F * this.mob.getBbWidth() * 1.0F + attackTarget.getBbWidth();
        }
    }
}