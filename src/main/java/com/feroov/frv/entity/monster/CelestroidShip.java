package com.feroov.frv.entity.monster;

import com.feroov.frv.entity.AnimationConstants;
import com.feroov.frv.entity.projectile.CelestroidBeam;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class CelestroidShip extends Ghast implements Enemy, GeoEntity
{

    public static final EntityDataAccessor<Integer> DATA_ATTACK_CHARGE_ID = SynchedEntityData.defineId(CelestroidShip.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(Ghast.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> ATTACK = SynchedEntityData.defineId(CelestroidShip.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public CelestroidShip(EntityType<? extends CelestroidShip> type, Level level)
    {
        super(type, level);
        this.moveControl = new CelestroidShip.MoveHelperController(this);
        this.xpReward = 15;
    }

    @Override
    protected void registerGoals()
    {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.goalSelector.addGoal(2, new CelestroidRangedAttackGoal(this, 0.3D, 63.0D, 120.0F, 0));
        this.goalSelector.addGoal(6, new CelestroidShip.LookAroundGoal(this));
        this.goalSelector.addGoal(8, new CelestroidShip.RandomFlyGoal(this));
    }

    public static AttributeSupplier setAttributes()
    {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.FOLLOW_RANGE, 120.0D).build();
    }


    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEventsSTLCON.CELESTROID_AMBIENT.get(), 1.5F, 1.0F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.5F, 0.2F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.IRON_GOLEM_HURT, 4.0F, 0.2F);
        return null;
    }


    @Override
    public boolean isPersistenceRequired() { return super.isPersistenceRequired(); }

    @Override
    public boolean shouldDespawnInPeaceful() { return true; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "livingController", 0, event ->
                event.setAndContinue(AnimationConstants.IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACK_CHARGE_ID, 0);
    }


    public void setCharging(boolean pCharging) { this.entityData.set(DATA_IS_CHARGING, pCharging); }


    @Override
    protected float getStandingEyeHeight(@NotNull Pose poseIn, @NotNull EntityDimensions sizeIn) { return 1.65F; }


    static class RandomFlyGoal extends Goal
    {
        private final CelestroidShip parentEntity;

        public RandomFlyGoal(CelestroidShip entity)
        {
            this.parentEntity = entity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            MoveControl moveControl = this.parentEntity.getMoveControl();
            if (!moveControl.hasWanted())
            {
                return true;
            }
            else
            {
                double d0 = moveControl.getWantedX() - this.parentEntity.getX();
                double d1 = moveControl.getWantedY() - this.parentEntity.getY();
                double d2 = moveControl.getWantedZ() - this.parentEntity.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0 || d3 > 3600.0;
            }
        }

        @Override
        public boolean canContinueToUse() { return false; }

        @Override
        public void start()
        {
            RandomSource random = this.parentEntity.getRandom();
            double d0 = this.parentEntity.getX() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            double d1 = this.parentEntity.getY() + (random.nextFloat() * 2.0F - 1.0F) * 5.0F;
            double d2 = this.parentEntity.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            this.parentEntity.getMoveControl().setWantedPosition(d0, d1, d2, 1.0);
        }
    }

    static class MoveHelperController extends MoveControl
    {
        private final CelestroidShip parentEntity;
        private int courseChangeCooldown;

        public MoveHelperController(CelestroidShip celestroidShip)
        {
            super(celestroidShip);
            this.parentEntity = celestroidShip;
        }

        @Override
        public void tick()
        {
            if (this.operation == MoveControl.Operation.MOVE_TO)
            {
                if (this.courseChangeCooldown-- <= 0)
                {
                    this.courseChangeCooldown += this.parentEntity.getRandom().nextInt(5) + 2;
                    Vec3 vec3d = new Vec3(this.wantedX - this.parentEntity.getX(),
                            this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
                    double d0 = vec3d.length();
                    vec3d = vec3d.normalize();

                    if (this.isNotColliding(vec3d, Mth.ceil(d0)))
                    {
                        this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vec3d.scale(0.1)));
                    }
                    else { this.operation = MoveControl.Operation.WAIT; }
                }
            }
        }

        private boolean isNotColliding(Vec3 pos, int distance)
        {
            AABB axisalignedbb = this.parentEntity.getBoundingBox();
            for (int i = 1; i < distance; ++i)
            {
                axisalignedbb = axisalignedbb.move(pos);
                if (!this.parentEntity.level().noCollision(this.parentEntity, axisalignedbb)) { return false; }
            }
            return true;
        }
    }

    static class LookAroundGoal extends Goal
    {
        private final CelestroidShip parentEntity;

        public LookAroundGoal(CelestroidShip celestroidShip)
        {
            this.parentEntity = celestroidShip;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() { return true; }

        @Override
        public void tick()
        {
            if (this.parentEntity.getTarget() == null)
            {
                Vec3 vec3d = this.parentEntity.getDeltaMovement();
                this.parentEntity.setYRot(-((float) Mth.atan2(vec3d.x, vec3d.z)) * (180.0F / (float) Math.PI));
                this.parentEntity.yBodyRot = this.parentEntity.getYRot();

            }
            else
            {
                LivingEntity livingEntity = this.parentEntity.getTarget();
                if (livingEntity.distanceToSqr(this.parentEntity) < 64 * 64) {
                    double x = livingEntity.getX() - this.parentEntity.getX();
                    double z = livingEntity.getZ() - this.parentEntity.getZ();
                    this.parentEntity.setYRot(-((float) Mth.atan2(x, z)) * (180.0F / (float) Math.PI));
                    this.parentEntity.yBodyRot = this.parentEntity.getYRot();
                }
            }
        }
    }

    public static class CelestroidRangedAttackGoal extends Goal
    {
        // The CelestroidShip and the ranged attack mob
        private final CelestroidShip mob;
        private final CelestroidShip rangedAttackMob;

        // The target entity and attack-related variables
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private int seeTime;
        private final int stateCheck;

        // Attack parameters
        private final double attackIntervalMin, attackIntervalMax, speedModifier;
        private final float attackRadius, attackRadiusSqr;

        // Strafing variables
        private boolean strafingClockwise, strafingBackwards;
        private int strafingTime = -1;

        /**
         * Constructs a CelestroidRangedAttackGoal for the CelestroidShip.
         *
         * @param celestroid        The Celestroid entity.
         * @param speedIn           The speed modifier.
         * @param dpsIn             The minimum attack interval.
         * @param rangeIn           The attack range.
         * @param state             The attack state.
         */
        public CelestroidRangedAttackGoal(CelestroidShip celestroid, double speedIn, double dpsIn, float rangeIn, int state)
        {
            this(celestroid, speedIn, dpsIn, dpsIn, rangeIn, state);
        }

        /**
         * Constructs a CelestroidRangedAttackGoal for the CelestroidShip with variable attack intervals.
         *
         * @param celestroid        The Celestroid entity.
         * @param speedIn           The speed modifier.
         * @param attackIntervalMin   The minimum attack interval.
         * @param attackIntervalMax   The maximum attack interval.
         * @param attackRadius        The attack radius.
         * @param state             The attack state.
         */
        public CelestroidRangedAttackGoal(CelestroidShip celestroid, double speedIn, double attackIntervalMin, double attackIntervalMax, float attackRadius, int state)
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
                this.attackIntervalMin = attackIntervalMin;
                this.attackIntervalMax = attackIntervalMax;
                this.attackRadius = attackRadius;
                this.attackRadiusSqr = attackRadius * attackRadius;
                this.stateCheck = state;

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
            assert this.target != null;
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
                        int i = this.mob.getTicksUsingItem();
                        if (i >= 19) { this.mob.setAttackingState(stateCheck); }
                        if (i >= 20) { this.mob.stopUsingItem(); }
                    }
                    float f = (float)Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.rangedAttackMob.performRangedAttack(this.target, f1);
                    this.attackTime = Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
                }
                else if (this.attackTime < 0)
                {
                    this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0)
                            / (double)this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
                }

            }
        }
    }

    public void performRangedAttack(LivingEntity targetEntity, float partialTicks)
    {
        Vec3 eyePos = getEyePosition(partialTicks);

        double targetX = targetEntity.getX() + targetEntity.getBbWidth() / 2.0;
        double targetY = targetEntity.getBoundingBox().minY + targetEntity.getBbHeight() / 2.0;
        double targetZ = targetEntity.getZ() + targetEntity.getBbWidth() / 2.0;

        double d2 = targetX - eyePos.x;
        double d3 = targetY - eyePos.y;
        double d4 = targetZ - eyePos.z;

        double velocityFactor = 2.0;

        CelestroidBeam raygunBeam = new CelestroidBeam(level(), this, d2, d3, d4);
        raygunBeam.shoot(d2, d3, d4, (float) velocityFactor, 0.1F);
        playSound(SoundEventsSTLCON.RAYGUN_SHOOT.get(), 7.0F, 1.0F);
        raygunBeam.setPos(eyePos.x, eyePos.y, eyePos.z);
        level().addFreshEntity(raygunBeam);

        if (getRandom().nextInt(6) == 0) { setTarget(null); }
    }

    public void setAttackingState(int time) { this.entityData.set(ATTACK, time); }

}