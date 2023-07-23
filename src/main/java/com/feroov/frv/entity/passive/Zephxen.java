package com.feroov.frv.entity.passive;

import com.feroov.frv.entity.AnimationConstants;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;


public class Zephxen extends Animal implements GeoEntity
{
    public static final EntityDataAccessor<Float> DATA_X_ROT_ID = SynchedEntityData.defineId(Zephxen.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DATA_Y_ROT_ID = SynchedEntityData.defineId(Zephxen.class, EntityDataSerializers.FLOAT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public Zephxen(EntityType<? extends Animal> type, Level level)
    {
        super(type, level);
        this.lookControl = new BlankLookControl(this);
        this.moveControl = new ZephxenMoveControl(this);
    }

    @Override
    public void registerGoals() { this.goalSelector.addGoal(1, new SetTravelCourseGoal(this)); }

    public static AttributeSupplier setAttributes()
    {
        return FlyingMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FLYING_SPEED, 0.2)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.4).build();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) { return null; }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(DATA_X_ROT_ID, this.getXRot());
        this.entityData.define(DATA_Y_ROT_ID, this.getYRot());
    }

    @Override
    public void aiStep()
    {
        super.aiStep();
        this.setXRot(this.getXRotData());
        this.setYRot(this.getYRotData());
        this.yBodyRot = this.getYRotData();
        this.yHeadRot = this.getYRotData();
    }

    @Override
    public void travel(@Nonnull Vec3 positionIn)
    {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance())
        {
            List<Entity> passengers = this.getPassengers();
            if (!passengers.isEmpty())
            {
                Entity entity = passengers.get(0);
                if (entity instanceof Player player)
                {
                    this.setYRot(player.getYRot() + 90F);
                    this.yRotO = this.getYRot();
                    this.setXRot(-player.getXRot());
                    this.xRotO = this.getXRot() * 0.5F;

                    this.yHeadRot = player.yHeadRot;

                    positionIn = new Vec3(player.xxa, 0.0, (player.zza <= 0.0F) ? player.zza * 0.25F : player.zza);

                        double d0 = Math.toRadians(this.getYRot());
                        double d1 = Math.toRadians(-player.getXRot());
                        double d2 = Math.cos(d1);
                        this.setDeltaMovement(
                                0.98 * (this.getDeltaMovement().x + 0.05 * Math.cos(d0) * d2),
                                0.98 * (this.getDeltaMovement().y + 0.02 * Math.sin(d1)),
                                0.98 * (this.getDeltaMovement().z + 0.05 * Math.sin(d0) * d2)
                        );

                    if (!this.level().isClientSide) { super.travel(positionIn); }

                    this.setXRot(this.getXRotData());
                    this.setYRot(this.getYRotData());
                    this.yBodyRot = this.getYRotData();
                    this.yHeadRot = this.getYRotData();
                }
            } else { super.travel(positionIn); }
        }
    }

    @Override
    protected float getFlyingSpeed() { return this.isVehicle() ? this.getSpeed() * 0.6F : 0.02F; }

    @Override
    public void tick() { super.tick(); this.clearFire();}

    @Override
    public int getMaxSpawnClusterSize() { return 1; }
    public void setXRotData(float rot) { this.entityData.set(DATA_X_ROT_ID, Mth.wrapDegrees(rot)); }
    public float getXRotData() { return this.entityData.get(DATA_X_ROT_ID); }
    public void setYRotData(float rot) { this.entityData.set(DATA_Y_ROT_ID, Mth.wrapDegrees(rot)); }
    public float getYRotData() { return this.entityData.get(DATA_Y_ROT_ID); }

    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEventsSTLCON.ZEPHXEN_AMBIENT.get(), 1.0F, 2.0F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEventsSTLCON.ZEPHXEN_HURT.get(), 1.0F, 2.0F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEventsSTLCON.ZEPHXEN_DEATH.get(), 1.0F, 2.0F);
        return null;
    }

    @Override
    protected float getSoundVolume() { return 2.0F; }

    @Override
    public int getExperienceReward() { return 1 + this.level().random.nextInt(3); }

    @Override
    public AABB getBoundingBoxForCulling() { return this.getBoundingBox().inflate(3.0); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "livingController", 0, event ->
        {
            return event.setAndContinue(AnimationConstants.IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    public static class SetTravelCourseGoal extends Goal
    {
        private final Mob mob;
        public SetTravelCourseGoal(Mob mob) { this.mob = mob; this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override
        public boolean canUse()
        {
            MoveControl moveControl = this.mob.getMoveControl();
            if (!moveControl.hasWanted()) { return true;}
            else
            {
                double d0 = moveControl.getWantedX() - this.mob.getX();
                double d1 = moveControl.getWantedY() - this.mob.getY();
                double d2 = moveControl.getWantedZ() - this.mob.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0;
            }
        }

        @Override
        public boolean canContinueToUse() { return false; }

        @Override
        public void start()
        {
            RandomSource random = this.mob.getRandom();
            double x = (random.nextFloat() * 2F - 1F) * 16;
            double z = (random.nextFloat() * 2F - 1F) * 16;
            double y = this.mob.getY() + (random.nextFloat() * 2F - 1F) * 16;

            x = x >= 0 ? x + 32 : x - 32;
            z = z >= 0 ? z + 32 : z - 32;

            x += this.mob.getX();
            z += this.mob.getZ();

            y = Mth.clamp(y, this.mob.level().getMinBuildHeight(), this.mob.level().getMaxBuildHeight());

            this.mob.getMoveControl().setWantedPosition(x, y, z, 1.0);
        }
    }

    public static class ZephxenMoveControl extends MoveControl
    {
        protected final Zephxen mob;

        public ZephxenMoveControl(Zephxen pMob) { super(pMob); this.mob = pMob; }

        @Override
        public void tick()
        {
            if (this.mob.isVehicle()) { return; }
            double x = this.getWantedX() - this.mob.getX();
            double y = this.getWantedY() - this.mob.getY();
            double z = this.getWantedZ() - this.mob.getZ();
            double distance = Math.sqrt(x * x + z * z);
            if (distance < 3 || this.isColliding(new Vec3(x, y, z).normalize())) { this.operation = Operation.WAIT; }

            float xRotTarget = (float) (Mth.atan2(y, distance) * (180F / (float) Math.PI)); // Pitch
            float xRot = Mth.wrapDegrees(this.mob.getXRot());
            xRot = Mth.approachDegrees(xRot, xRotTarget, 0.2F);
            this.mob.setXRot(xRot);
            this.mob.setXRotData(this.mob.getXRot());

            float yRotTarget = Mth.wrapDegrees((float) Mth.atan2(z, x) * (180F / (float) Math.PI)); // Yaw
            float yRot = Mth.wrapDegrees(this.mob.getYRot() + 90F);
            yRot = Mth.approachDegrees(yRot, yRotTarget, 0.5F);
            this.mob.setYRot(yRot - 90F);
            this.mob.setYRotData(this.mob.getYRot());
            this.mob.yBodyRot = yRot;
            this.mob.yHeadRot = yRot;

            x = this.mob.getAttributeValue(Attributes.FLYING_SPEED) * Mth.cos(yRot * ((float) Math.PI / 180F));
            y = this.mob.getAttributeValue(Attributes.FLYING_SPEED) * Mth.sin(xRot * ((float) Math.PI / 180F));
            z = this.mob.getAttributeValue(Attributes.FLYING_SPEED) * Mth.sin(yRot * ((float) Math.PI / 180F));

            Vec3 motion = new Vec3(x, y, z);
            this.mob.setDeltaMovement(motion);
        }

        private boolean isColliding(Vec3 pos)
        {
            AABB axisalignedbb = this.mob.getBoundingBox();

            for (int i = 1; i < 7; ++i)
            {
                axisalignedbb = axisalignedbb.move(pos);
                if (!this.mob.level().noCollision(this.mob, axisalignedbb)) { return true; }
            } return false;
        }
    }

    public static class BlankLookControl extends LookControl
    {
        public BlankLookControl(Mob pMob) { super(pMob); }

        @Override
        public void tick() {}
    }
}