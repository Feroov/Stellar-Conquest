package com.feroov.frv.entity.monster;

import com.feroov.frv.entity.ai.CelestroidAttackGoal;
import com.feroov.frv.entity.projectile.CelestroidBeam;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class Celestroid extends Ghast implements Enemy, GeoEntity
{

    public static final EntityDataAccessor<Integer> DATA_ATTACK_CHARGE_ID = SynchedEntityData.defineId(Celestroid.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(Ghast.class, EntityDataSerializers.BOOLEAN);

    private CelestroidAttackGoal attackAI;
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public Celestroid(EntityType<? extends Celestroid> type, Level level)
    {
        super(type, level);
        this.moveControl = new Celestroid.MoveHelperController(this);
        this.xpReward = 15;
    }

    @Override
    protected void registerGoals()
    {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.goalSelector.addGoal(2, attackAI = new CelestroidAttackGoal(this));
        this.goalSelector.addGoal(6, new Celestroid.LookAroundGoal(this));
        this.goalSelector.addGoal(8, new Celestroid.RandomFlyGoal(this));
    }

    public static AttributeSupplier setAttributes()
    {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 45.0D)
                .add(Attributes.FOLLOW_RANGE, 75.0D).build();
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
    public void checkDespawn(){
        if (this.getLevel().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        } else {
            super.checkDespawn();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState)
    {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACK_CHARGE_ID, 0);
    }


    public void setCharging(boolean pCharging) {
        this.entityData.set(DATA_IS_CHARGING, pCharging);
    }

    public void shootRayBeam()
    {
        Vec3 vec3d = this.getViewVector(1.0F);
        double d2 = this.getTarget().getX() - (this.getX() + vec3d.x() * 4.0D);
        double d3 = this.getTarget().getBoundingBox().minY + this.getTarget().getBbHeight() / 2.0F - (0.5D + this.getY() + this.getBbHeight() / 2.0F);
        double d4 = this.getTarget().getZ() - (this.getZ() + vec3d.z() * 4.0D);

        CelestroidBeam raygunBeam = new CelestroidBeam(this.level, this, d2, d3, d4);
        raygunBeam.setPos(this.getX() + vec3d.x() * 4.0D, this.getY() + this.getBbHeight() / 2.0F + 0.5D, this.getZ() + vec3d.z() * 4.0D);
        this.getLevel().addFreshEntity(raygunBeam);

        if (this.getRandom().nextInt(6) == 0) {
            this.setTarget(null);
        }
    }

    public boolean shouldAttack(LivingEntity living) {
        return true;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) { return 1.65F; }


    static class RandomFlyGoal extends Goal
    {
        private final Celestroid parentEntity;

        public RandomFlyGoal(Celestroid entity)
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
            double d1 = this.parentEntity.getY() + (random.nextFloat() * 2.0F - 1.0F) * 11.0F;
            double d2 = this.parentEntity.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
            this.parentEntity.getMoveControl().setWantedPosition(d0, d1, d2, 1.0);
        }
    }

    static class MoveHelperController extends MoveControl
    {
        private final Celestroid parentEntity;
        private int courseChangeCooldown;

        public MoveHelperController(Celestroid celestroid)
        {
            super(celestroid);
            this.parentEntity = celestroid;
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
                        this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vec3d.scale(0.15)));
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
                if (!this.parentEntity.level.noCollision(this.parentEntity, axisalignedbb)) { return false; }
            }
            return true;
        }
    }

    static class LookAroundGoal extends Goal
    {
        private final Celestroid parentEntity;

        public LookAroundGoal(Celestroid celestroid)
        {
            this.parentEntity = celestroid;
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
}