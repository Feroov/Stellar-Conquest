package com.feroov.frv.entity.monster;

import com.feroov.frv.entity.EntitiesSTLCON;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class MirrorbornSlime extends Animal
{

    private static final int MAX_SPAWN_LIMIT = 4;
    private int spawnCount = 1;
    public float mirrorbornHeight = 0.7F;
    public float oMirrorbornHeight = 0.7F;
    public float mirrorbornWidth = 0.7F;
    public float oMirrorbornWidth = 0.7F;
    private int jumpDelayTicks;
    private boolean wasOnGround;

    /**
     * Constructs a new MirrorbornSlime entity with the specified entity type and level.
     *
     * @param entityType The entity type of the MergedMirrorborn.
     * @param level The level where the MergedMirrorborn exists.
     */
    public MirrorbornSlime(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
        this.jumpControl = new MirrorbornJumpControl(this);
        this.moveControl = new MirrorbornMoveControl(this);
        this.setSpeedModifier(0.0D);
    }

    /**
     * Sets the attributes for the MirrorbornSlime entity, such as health, movement speed, and attack damage.
     *
     * @return The attribute supplier containing the entity's attributes.
     */
    public static AttributeSupplier setAttributes()
    {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 17.0)
                .add(Attributes.ATTACK_DAMAGE, 2.5).build();
    }

    /**
     * Registers the AI goals for the MergedMirrorborn entity.
     */
    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.65D, true));
        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Mob.class, 25.0F));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.3D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.77D));
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEvents.WITCH_DRINK, 1.0F, 5.4F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEvents.SLIME_HURT_SMALL, 1.0F, 1.3F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.SLIME_DEATH_SMALL, 1.0F, 1.3F);
        return null;
    }

    /**
     * Determines whether the MergedMirrorborn should despawn in peaceful mode.
     *
     * @return True if the MergedMirrorborn should despawn in peaceful mode, false otherwise.
     */
    @Override
    protected boolean shouldDespawnInPeaceful() { return true; }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob)  { return null; }

    protected float getJumpPower()
    {
        float jumpHeight = 0.08F;
        float jumpPower = (float) Math.sqrt(2.0F * jumpHeight * 0.98F);
        if (this.horizontalCollision || (this.moveControl.hasWanted() && this.moveControl.getWantedY() > this.getY() + 0.5D))
        {
            jumpPower *= 1.5F;
        }

        Path path = this.navigation.getPath();
        if (path != null && !path.isDone())
        {
            Vec3 vec3 = path.getNextEntityPos(this);
            if (vec3.y > this.getY() + 0.5D) { jumpPower *= 1.5F; }
        }

        if (this.moveControl.getSpeedModifier() <= 0.6D) { jumpPower *= 0.6F; }

        return jumpPower + this.getJumpBoostPower();
    }

    protected void jumpFromGround()
    {
        super.jumpFromGround();
        double d0 = this.moveControl.getSpeedModifier();
        if (d0 > 0.0D)
        {
            double d1 = this.getDeltaMovement().horizontalDistanceSqr();
            if (d1 < 0.01D)
            {
                this.moveRelative(0.1F, new Vec3(0.0D, 0.0D, 1.0D));
            }
        }

        if (!this.level().isClientSide) { this.level().broadcastEntityEvent(this, (byte)1); }
    }


    public void setSpeedModifier(double position)
    {
        this.getNavigation().setSpeedModifier(position);
        this.moveControl.setWantedPosition(this.moveControl.getWantedX(),
                this.moveControl.getWantedY(), this.moveControl.getWantedZ(), position);
    }

    public void setJumping(boolean jumping)
    {
        super.setJumping(jumping);
        if (jumping)
        {
            this.playSound(this.getJumpSound(), this.getSoundVolume(),
                    ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }
    }

    public void startJumping()
    {
        this.setJumping(true);
    }

    private void enableJumpControl()
    {
        ((MirrorbornJumpControl)this.jumpControl).setCanJump(true);
    }

    private void disableJumpControl()
    {
        ((MirrorbornJumpControl)this.jumpControl).setCanJump(false);
    }

    private void setLandingDelay()
    {
        if (this.moveControl.getSpeedModifier() < 2.2D) { this.jumpDelayTicks = 10; }
        else { this.jumpDelayTicks = 1; }
    }

    private void checkLandingDelay()
    {
        this.setLandingDelay();
        this.disableJumpControl();
    }

    protected SoundEvent getJumpSound()
    {
        return SoundEvents.SLIME_JUMP_SMALL;
    }

    public void handleEntityEvent(byte b)
    {
        if (b == 1)
        {
            this.spawnSprintParticle();
        }
        else { super.handleEntityEvent(b); }
    }

    /**
     * The nested class representing the jump control for the MirrorbornSlime entity.
     * Manages jump behavior for the entity.
     */
    public static class MirrorbornJumpControl extends JumpControl
    {
        private final MirrorbornSlime mirrorbornSlime;
        private boolean canJump;

        public MirrorbornJumpControl(MirrorbornSlime mirrorbornSlime)
        {
            super(mirrorbornSlime);
            this.mirrorbornSlime = mirrorbornSlime;
        }

        /**
         * Checks if the entity wants to jump.
         *
         * @return True if the entity wants to jump, false otherwise.
         */
        public boolean wantJump() { return !this.jump; }

        /**
         * Checks if the entity is allowed to jump.
         *
         * @return True if the entity is allowed to jump, false otherwise.
         */
        public boolean canJump() { return this.canJump; }

        /**
         * Sets whether the entity is allowed to jump.
         *
         * @param canJump True to allow jumping, false to disallow.
         */
        public void setCanJump(boolean canJump) { this.canJump = canJump; }

        /**
         * Updates the jump control, starting the jump action if necessary.
         */
        public void tick()
        {
            if (this.jump)
            {
                this.mirrorbornSlime.startJumping();
                this.jump = false;
            }
        }
    }

    /**
     * The nested class representing the move control for the MirrorbornSlime entity.
     * Manages movement behavior for the entity.
     */
    static class MirrorbornMoveControl extends MoveControl
    {
        private final MirrorbornSlime mirrorbornSlime;
        private double nextJumpSpeed;

        public MirrorbornMoveControl(MirrorbornSlime mirrorbornSlime)
        {
            super(mirrorbornSlime);
            this.mirrorbornSlime = mirrorbornSlime;
        }

        /**
         * Updates the move control, adjusting the entity's speed if necessary.
         */
        public void tick()
        {
            if (this.mirrorbornSlime.onGround() && !this.mirrorbornSlime.jumping
                    && ((MirrorbornJumpControl) this.mirrorbornSlime.jumpControl).wantJump())
            {
                this.mirrorbornSlime.setSpeedModifier(0.0D);
            }
            else if (this.hasWanted()) { this.mirrorbornSlime.setSpeedModifier(this.nextJumpSpeed); }

            super.tick();
        }

        /**
         * Sets the desired position and speed for the move control.
         *
         * @param x The desired x-coordinate.
         * @param y The desired y-coordinate.
         * @param z The desired z-coordinate.
         * @param d The desired speed modifier.
         */
        public void setWantedPosition(double x, double y, double z, double d)
        {
            if (this.mirrorbornSlime.isInWater()) { d = 1.5D; }

            super.setWantedPosition(x, y, z, d);

            if (d > 0.0D) { this.nextJumpSpeed = d; }
        }
    }

    /**
     * Custom method to handle server-side AI updates for the MirrorbornSlime entity.
     * Handles jumping behavior and AI control based on the entity's state.
     */
    public void customServerAiStep()
    {
        if (this.jumpDelayTicks > 0) { --this.jumpDelayTicks; }

        if (this.onGround())
        {
            if (!this.wasOnGround)
            {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            MirrorbornJumpControl jumpControl1 = (MirrorbornJumpControl)this.jumpControl;
            if (jumpControl1.wantJump())
            {
                if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0)
                {
                    Path path = this.navigation.getPath();
                    Vec3 vec3 = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
                    if (path != null && !path.isDone()) { vec3 = path.getNextEntityPos(this); }

                    this.facePoint(vec3.x, vec3.z);
                    this.startJumping();
                }
            }
            else if (!jumpControl1.canJump()) { this.enableJumpControl(); }
        }

        this.wasOnGround = this.onGround();
    }

    /**
     * Faces the given point (x, z) by setting the entity's yaw rotation accordingly.
     *
     * @param x The x-coordinate of the point.
     * @param z The z-coordinate of the point.
     */
    private void facePoint(double x, double z)
    {
        this.setYRot((float)(Mth.atan2(z - this.getZ(), x - this.getX()) * (double)(180F / (float)Math.PI)) - 90.0F);
    }

    /**
     * Handles the logic when the MergedMirrorborn attacks a target entity.
     * The MergedMirrorborn plays an attack sound and has a chance to spawn additional MirrorbornSlime entities.
     *
     * @param entityIn The target entity being attacked.
     * @return True if the target was successfully attacked, false otherwise.
     */
    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn)
    {
        this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, 1.4F);
        if (!this.level().isClientSide && spawnCount <= MAX_SPAWN_LIMIT)
        {
            boolean shouldSpawn;
            if (spawnCount == 1)
            {
                shouldSpawn = true;
            }
            else
            {
                double spawnProbability = 1.0 / (spawnCount - 1);
                shouldSpawn = this.random.nextDouble() < spawnProbability;
            }

            if (shouldSpawn)
            {
                spawnCount++;
                MirrorbornSlime mirrorbornSlime = (MirrorbornSlime) this.getType().create(this.level());
                if (mirrorbornSlime != null)
                {
                    mirrorbornSlime.copyPosition(this);
                    this.level().addFreshEntity(mirrorbornSlime);

                    if (spawnCount >= MAX_SPAWN_LIMIT)
                    {
                        mergeSlimes();
                    }
                }
            }
        }
        return super.doHurtTarget(entityIn);
    }

    /**
     * Merges nearby MirrorbornSlime entities into a single MergedMirrorborn entity
     * when the spawn count reaches the maximum spawn limit (mergeThreshold).
     * The merged entity replaces the original MergedMirrorborn.
     */
    private void mergeSlimes()
    {
        int mergeRadius = 2;
        int mergeThreshold = 20;

        int slimeCount = 0;
        List<MirrorbornSlime> nearbySlimes = this.level().getEntitiesOfClass(MirrorbornSlime.class,
                this.getBoundingBox().inflate(mergeRadius));

        for (MirrorbornSlime ignored : nearbySlimes)
        {
            slimeCount++;
            if (slimeCount >= mergeThreshold)
            {
                break;
            }
        }

        if (slimeCount == mergeThreshold)
        {
            for (MirrorbornSlime slime : nearbySlimes)
            {
                slime.discard();
                slime.playSound(SoundEvents.SLIME_SQUISH, 1.0F, 0.4F);

                if (this.level() instanceof ServerLevel)
                {
                    double spread = 0.5;

                    for (int i = 0; i < 10; i++)
                    {
                        double offsetX = this.random.nextGaussian() * spread;
                        double offsetY = this.random.nextGaussian() * spread;
                        double offsetZ = this.random.nextGaussian() * spread;

                        ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION,
                                this.getX() + offsetX, this.getY() + this.getEyeHeight() / 2 + offsetY,
                                this.getZ() + offsetZ, 1, 0.0, 0.0, 0.0, 0.0);
                    }
                }
            }

            MergedMirrorborn mergedSlime = EntitiesSTLCON.MERGED_MIRRORBORN_SLIME.get().create(this.level());
            if (mergedSlime != null)
            {
                Vec3 centerPos = this.position();
                mergedSlime.absMoveTo(centerPos.x, centerPos.y, centerPos.z);
                this.level().addFreshEntity(mergedSlime);
            }
        }
    }

    /**
     * Gets the amount of experience points dropped by the MergedMirrorborn upon death.
     * This entity does not drop any experience points upon death.
     *
     * @return The amount of experience points dropped (always 0).
     */
    @Override
    public int getExperienceReward() { return 0; }
}
