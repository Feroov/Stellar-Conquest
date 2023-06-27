package com.feroov.frv.entity.monster;

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
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class MergedMirrorborn extends Animal
{

    public float mirrorbornHeight = 3.5F;
    public float oMirrorbornHeight = 3.5F;
    public float mirrorbornWidth = 3.5F;
    public float oMirrorbornWidth = 3.5F;
    private int jumpTicks;
    private int jumpDuration;
    private int jumpDelayTicks;
    private boolean wasOnGround;

    public MergedMirrorborn(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
        this.jumpControl = new MirrorbornJumpControl(this);
        this.moveControl = new MirrorbornMoveControl(this);
        this.setSpeedModifier(0.0D);
        this.xpReward = 40;
    }

    public static AttributeSupplier setAttributes()
    {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 17.0)
                .add(Attributes.ATTACK_DAMAGE, 5.5).build();
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Mob.class, 25.0F));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.3D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.77D));
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEvents.WITCH_DRINK, 1.0F, 1.4F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEvents.SLIME_HURT, 1.0F, 1.3F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.SLIME_DEATH, 1.0F, 1.3F);
        return null;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() { return true; }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob)  { return null; }

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
        this.jumpDuration = 10;
        this.jumpTicks = 0;
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
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        }
        else { super.handleEntityEvent(b); }
    }

    public static class MirrorbornJumpControl extends JumpControl
    {
        private final MergedMirrorborn mirrorbornSlime;
        private boolean canJump;

        public MirrorbornJumpControl(MergedMirrorborn mirrorbornSlime)
        {
            super(mirrorbornSlime);
            this.mirrorbornSlime = mirrorbornSlime;
        }

        public boolean wantJump() { return this.jump; }

        public boolean canJump() { return this.canJump; }

        public void setCanJump(boolean canJump) { this.canJump = canJump; }

        public void tick()
        {
            if (this.jump)
            {
                this.mirrorbornSlime.startJumping();
                this.jump = false;
            }
        }
    }

    static class MirrorbornMoveControl extends MoveControl
    {
        private final MergedMirrorborn mirrorbornSlime;
        private double nextJumpSpeed;

        public MirrorbornMoveControl(MergedMirrorborn mirrorbornSlime)
        {
            super(mirrorbornSlime);
            this.mirrorbornSlime = mirrorbornSlime;
        }

        public void tick()
        {
            if (this.mirrorbornSlime.onGround() && !this.mirrorbornSlime.jumping
                    && !((MirrorbornJumpControl)this.mirrorbornSlime.jumpControl).wantJump())
            {
                this.mirrorbornSlime.setSpeedModifier(0.0D);
            }
            else if (this.hasWanted()) { this.mirrorbornSlime.setSpeedModifier(this.nextJumpSpeed); }

            super.tick();
        }

        public void setWantedPosition(double x, double y, double z, double d)
        {
            if (this.mirrorbornSlime.isInWater()) { d = 1.5D; }

            super.setWantedPosition(x, y, z, d);

            if (d > 0.0D) { this.nextJumpSpeed = d; }
        }
    }

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
            if (!jumpControl1.wantJump())
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

    private void facePoint(double x, double z)
    {
        this.setYRot((float)(Mth.atan2(z - this.getZ(), x - this.getX()) * (double)(180F / (float)Math.PI)) - 90.0F);
    }

    @Override
    public boolean doHurtTarget(Entity entityIn)
    {
        this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, 0.4F);
        return super.doHurtTarget(entityIn);
    }
}
