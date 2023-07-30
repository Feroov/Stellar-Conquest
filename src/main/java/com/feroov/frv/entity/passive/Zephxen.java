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
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;


public class Zephxen extends Animal implements GeoEntity
{
    // Data Accessors for X and Y rotations of the entity.
    public static final EntityDataAccessor<Float> DATA_X_ROT_ID = SynchedEntityData.defineId(Zephxen.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DATA_Y_ROT_ID = SynchedEntityData.defineId(Zephxen.class, EntityDataSerializers.FLOAT);

    // Cache for the AnimatableInstance used by this entity.
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    /**
     * Constructs a new Zephxen entity.
     *
     * @param type  The entity type.
     * @param level The world level.
     */
    public Zephxen(EntityType<? extends Animal> type, Level level)
    {
        super(type, level);
        this.lookControl = new BlankLookControl(this);
        this.moveControl = new ZephxenMoveControl(this);
    }

    /**
     * Sets the attributes for the Zephxen entity.
     *
     * @return The attribute supplier with the set attributes.
     */
    public static AttributeSupplier setAttributes()
    {
        return FlyingMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FLYING_SPEED, 0.2)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.4).build();
    }

    /**
     * Registers the goals for this entity, defining its behavior.
     */
    @Override
    public void registerGoals() { this.goalSelector.addGoal(1, new SetTravelCourseGoal(this)); }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) { return null; }

    /**
     * Defines the synchronized data for this entity.
     */
    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(DATA_X_ROT_ID, this.getXRot());
        this.entityData.define(DATA_Y_ROT_ID, this.getYRot());
    }

    /**
     * Handles the AI logic for the entity on each tick.
     */
    @Override
    public void aiStep()
    {
        super.aiStep();
        this.setXRot(this.getXRotData());
        this.setYRot(this.getYRotData());
        this.yBodyRot = this.getYRotData();
        this.yHeadRot = this.getYRotData();
    }

    /**
     * Handles the entity's movement logic.
     *
     * @param positionIn The desired position.
     */
    @Override
    public void travel(@Nonnull Vec3 positionIn)
    {
        // Check if entity has AI control or is controlled by a player.
        if (this.isEffectiveAi() || this.isControlledByLocalInstance())
        {
            // Get passengers of the entity (if any).
            List<Entity> passengers = this.getPassengers();
            if (!passengers.isEmpty())
            {
                Entity entity = passengers.get(0);
                if (entity instanceof Player player)
                {
                    // Update rotations based on player's view.
                    this.setYRot(player.getYRot() + 90F);
                    this.yRotO = this.getYRot();
                    this.setXRot(-player.getXRot());
                    this.xRotO = this.getXRot() * 0.5F;

                    // Update head rotation to match player's view.
                    this.yHeadRot = player.yHeadRot;

                    // Update the movement vector based on player's input.
                    positionIn = new Vec3(player.xxa, 0.0, (player.zza <= 0.0F) ? player.zza * 0.25F : player.zza);

                    // Calculate new velocity based on rotations and player's input.
                    double d0 = Math.toRadians(this.getYRot());
                    double d1 = Math.toRadians(-player.getXRot());
                    double d2 = Math.cos(d1);
                    this.setDeltaMovement(
                            0.98 * (this.getDeltaMovement().x + 0.05 * Math.cos(d0) * d2),
                            0.98 * (this.getDeltaMovement().y + 0.02 * Math.sin(d1)),
                            0.98 * (this.getDeltaMovement().z + 0.05 * Math.sin(d0) * d2));

                    // Perform the movement on the server side.
                    if (!this.level().isClientSide) { super.travel(positionIn); }

                    // Update rotations after the movement.
                    this.setXRot(this.getXRotData());
                    this.setYRot(this.getYRotData());
                    this.yBodyRot = this.getYRotData();
                    this.yHeadRot = this.getYRotData();
                }
            } else { super.travel(positionIn); }
        }
    }

    /**
     * Gets the flying speed of the Zephxen entity.
     *
     * @return The flying speed.
     */
    @Override
    protected float getFlyingSpeed() { return this.isVehicle() ? this.getSpeed() * 0.6F : 0.02F; }

    /**
     * Handles the ticking logic for the Zephxen entity.
     */
    @Override
    public void tick() { super.tick(); this.clearFire();}


    @Override
    public int getMaxSpawnClusterSize() { return 1; }

    public float getXRotData() { return this.entityData.get(DATA_X_ROT_ID); }

    public float getYRotData() { return this.entityData.get(DATA_Y_ROT_ID); }

    public void setXRotData(float rot) { this.entityData.set(DATA_X_ROT_ID, Mth.wrapDegrees(rot)); }

    public void setYRotData(float rot) { this.entityData.set(DATA_Y_ROT_ID, Mth.wrapDegrees(rot)); }

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

    /**
     * Gets the volume of sounds played by the Zephxen entity.
     *
     * @return The sound volume.
     */
    @Override
    protected float getSoundVolume() { return 2.0F; }

    /**
     * Gets the experience reward for killing the Zephxen entity.
     *
     * @return The experience reward value.
     */
    @Override
    public int getExperienceReward() { return 1 + this.level().random.nextInt(3); }

    /**
     * Gets the bounding box used for culling checks for the Zephxen entity.
     *
     * @return The culling bounding box.
     */
    @Override
    public AABB getBoundingBoxForCulling() { return this.getBoundingBox().inflate(3.0); }

    /**
     * Registers controllers for animations of the Zephxen entity.
     *
     * @param controllerRegistrar The controller registrar to add the animation controller to.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "livingController", 0, event ->
        {
            return event.setAndContinue(AnimationConstants.IDLE);
        }));
    }

    /**
     * Gets the cache for animatable instances of the Zephxen entity.
     *
     * @return The animatable instance cache.
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    /**
     * This inner class represents a Goal that sets the travel course for the Zephxen entity.
     */
    public static class SetTravelCourseGoal extends Goal
    {
        private final Mob mob;

        /**
         * Constructs a SetTravelCourseGoal.
         *
         * @param mob The mob entity to set the travel course for.
         */
        public SetTravelCourseGoal(Mob mob) { this.mob = mob; this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        /**
         * Checks if the goal can be used.
         *
         * @return True if the goal can be used, false otherwise.
         */
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

        /**
         * Checks if the goal can continue to be used.
         *
         * @return Always returns false.
         */
        @Override
        public boolean canContinueToUse() { return false; }

        /**
         * Starts the goal. Sets a random travel course for the mob entity.
         */
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

    /**
     * This inner class represents a MoveControl for the Zephxen entity.
     */
    public static class ZephxenMoveControl extends MoveControl
    {
        protected final Zephxen mob;

        /**
         * Constructs a ZephxenMoveControl.
         *
         * @param pMob The mob entity associated with this move control.
         */
        public ZephxenMoveControl(Zephxen pMob) { super(pMob); this.mob = pMob; }

        /**
         * Ticks the move control logic for the mob entity.
         */
        @Override
        public void tick()
        {
            // If the Zephxen entity is currently riding another entity (a vehicle), return and skip the logic.
            if (this.mob.isVehicle()) { return; }

            // Calculate the differences between the current position and the desired position (target) in the X, Y, and Z directions.
            double x = this.getWantedX() - this.mob.getX();
            double y = this.getWantedY() - this.mob.getY();
            double z = this.getWantedZ() - this.mob.getZ();

            // Calculate the distance between the current position and the desired position using the Pythagorean theorem.
            double distance = Math.sqrt(x * x + z * z);

            // Check if the distance is less than 3 units or if there is a collision with obstacles along the path.
            // If either condition is true, set the movement operation to WAIT, pausing the movement.
            if (distance < 3 || this.isColliding(new Vec3(x, y, z).normalize())) { this.operation = Operation.WAIT; }

            // Calculate the pitch angle (X rotation) to face towards the target point.
            float xRotTarget = (float) (Mth.atan2(y, distance) * (180F / (float) Math.PI)); // Pitch

            // Get the current X rotation of the Zephxen entity and ensure it stays within a certain range (-180 to 180 degrees).
            float xRot = Mth.wrapDegrees(this.mob.getXRot());

            // Gradually adjust the X rotation towards the target pitch angle (smooth rotation).
            xRot = Mth.approachDegrees(xRot, xRotTarget, 0.2F);

            // Set the adjusted X rotation back to the Zephxen entity and update its X rotation data.
            this.mob.setXRot(xRot);
            this.mob.setXRotData(this.mob.getXRot());

            // Calculate the yaw angle (Y rotation) to face towards the target point.
            float yRotTarget = Mth.wrapDegrees((float) Mth.atan2(z, x) * (180F / (float) Math.PI)); // Yaw

            // Get the current Y rotation of the Zephxen entity and adjust it to align with the player's viewpoint (player model's yaw).
            float yRot = Mth.wrapDegrees(this.mob.getYRot() + 90F);

            // Gradually adjust the Y rotation towards the target yaw angle (smooth rotation).
            yRot = Mth.approachDegrees(yRot, yRotTarget, 0.5F);

            // Apply the adjusted Y rotation to the Zephxen entity's body and head rotations.
            this.mob.setYRot(yRot - 90F);
            this.mob.setYRotData(this.mob.getYRot());
            this.mob.yBodyRot = yRot;
            this.mob.yHeadRot = yRot;

            // Calculate the motion (movement) in the X, Y, and Z directions based on the adjusted Y rotation (yaw) and X rotation (pitch).
            x = this.mob.getAttributeValue(Attributes.FLYING_SPEED) * Mth.cos(yRot * ((float) Math.PI / 180F));
            y = this.mob.getAttributeValue(Attributes.FLYING_SPEED) * Mth.sin(xRot * ((float) Math.PI / 180F));
            z = this.mob.getAttributeValue(Attributes.FLYING_SPEED) * Mth.sin(yRot * ((float) Math.PI / 180F));

            // Create a new 3D vector representing the calculated motion (movement) in the X, Y, and Z directions.
            Vec3 motion = new Vec3(x, y, z);

            // Set the motion (movement) vector as the Zephxen entity's new velocity, affecting its movement in the world.
            this.mob.setDeltaMovement(motion);
        }

        /**
         * Checks if the entity is colliding with obstacles at the given position.
         *
         * @param pos The position to check for collisions.
         * @return True if there is a collision, false otherwise.
         */
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

    /**
     * This inner class represents a LookControl for the Zephxen entity.
     */
    public static class BlankLookControl extends LookControl
    {
        /**
         * Constructs a BlankLookControl.
         *
         * @param pMob The mob entity associated with this look control.
         */
        public BlankLookControl(Mob pMob) { super(pMob); }

        @Override
        public void tick() {}
    }
}