package com.feroov.frv.entity.monster;

import com.feroov.frv.entity.AnimationConstants;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.projectile.MothershipBeam;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class Mothership extends Ghast implements Enemy, GeoEntity
{
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS));

    public static final EntityDataAccessor<Integer> DATA_ATTACK_CHARGE_ID = SynchedEntityData.defineId(Mothership.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(Ghast.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> ATTACK = SynchedEntityData.defineId(CelestroidShip.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    protected int spellCastingTickCount;
    private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterIllager.class, EntityDataSerializers.BYTE);

    public Mothership(EntityType<? extends Mothership> type, Level level)
    {
        super(type, level);
        this.moveControl = new Mothership.MoveHelperController(this);
        this.xpReward = 100;
    }

    @Override
    protected void registerGoals()
    {

        this.goalSelector.addGoal(0, new Mothership.AttackSpellGoal());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.goalSelector.addGoal(2, new CelestroidRangedAttackGoal(this, 0.3D, 63.0D, 120.0F, 0));
        this.goalSelector.addGoal(6, new Mothership.LookAroundGoal(this));
        this.goalSelector.addGoal(8, new Mothership.RandomFlyGoal(this));
    }

    public static AttributeSupplier setAttributes()
    {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 250.0D)
                .add(Attributes.FOLLOW_RANGE, 120.0D).build();
    }


    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEventsSTLCON.CELESTROID_AMBIENT.get(), 1.5F, 0.1F);
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
        private final Mothership parentEntity;

        public RandomFlyGoal(Mothership entity)
        {
            this.parentEntity = entity;
            this.setFlags(EnumSet.of(Flag.MOVE));
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
        private final Mothership parentEntity;
        private int courseChangeCooldown;

        public MoveHelperController(Mothership celestroid)
        {
            super(celestroid);
            this.parentEntity = celestroid;
        }

        @Override
        public void tick()
        {
            if (this.operation == Operation.MOVE_TO)
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
                        this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vec3d.scale(0.07)));
                    }
                    else { this.operation = Operation.WAIT; }
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
        private final Mothership parentEntity;

        public LookAroundGoal(Mothership celestroid)
        {
            this.parentEntity = celestroid;
            this.setFlags(EnumSet.of(Flag.LOOK));
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

    @Override
    public boolean canBeCollidedWith() { return super.canBeCollidedWith(); }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player)
    {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player)
    {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public int getMaxSpawnClusterSize() { return super.getMaxSpawnClusterSize(); }

    @Override
    public void setCustomName(Component name)
    {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public static class CelestroidRangedAttackGoal extends Goal
    {
        // The CelestroidShip and the ranged attack mob
        private final Mothership mob;
        private final Mothership rangedAttackMob;

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
        public CelestroidRangedAttackGoal(Mothership celestroid, double speedIn, double dpsIn, float rangeIn, int state)
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
        public CelestroidRangedAttackGoal(Mothership celestroid, double speedIn, double attackIntervalMin, double attackIntervalMax, float attackRadius, int state)
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

        double velocityFactor = 1.15;

        MothershipBeam raygunBeam = new MothershipBeam(level(), this, d2, d3, d4);
        raygunBeam.shoot(d2, d3, d4, (float) velocityFactor, 0.1F);
        playSound(SoundEventsSTLCON.RAYGUN_SHOOT.get(), 7.0F, 1.0F);
        raygunBeam.setPos(eyePos.x, eyePos.y, eyePos.z);
        level().addFreshEntity(raygunBeam);

        if (getRandom().nextInt(6) == 0) { setTarget(null); }
    }

    public void setAttackingState(int time) { this.entityData.set(ATTACK, time); }


    public abstract class UseSpellGoal extends Goal
    {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        protected UseSpellGoal() { }

        public boolean canUse()
        {
            LivingEntity livingEntity = Mothership.this.getTarget();
            return livingEntity != null && livingEntity.isAlive() && !Mothership.this.isCastingSpell() && Mothership.this.tickCount >= this.nextAttackTickCount;
        }

        public boolean canContinueToUse()
        {
            LivingEntity livingEntity = Mothership.this.getTarget();
            return livingEntity != null && livingEntity.isAlive() && this.attackWarmupDelay > 0;
        }

        public void start()
        {
            this.attackWarmupDelay = this.getCastWarmupTime();
            Mothership.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = Mothership.this.tickCount + this.getCastingInterval();
            SoundEvent soundEvent = this.getSpellPrepareSound();

            if (soundEvent != null) { Mothership.this.playSound(soundEvent, 0.0F, 1.0F); }
        }

        public void tick()
        {
            --this.attackWarmupDelay; if (this.attackWarmupDelay == 0) { this.performSpellCasting(); }
        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() { return 20; }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();
    }

    public boolean isCastingSpell()
    {
        return this.level().isClientSide ? this.entityData.get(DATA_SPELL_CASTING_ID) > 0 : this.spellCastingTickCount > 0;
    }

    class AttackSpellGoal extends UseSpellGoal
    {
        private AttackSpellGoal() { }
        protected int getCastingTime() { return 40; }
        protected int getCastingInterval() { return 100; }

        protected void performSpellCasting()
        {
            LivingEntity livingEntity = Mothership.this.getTarget();
            assert livingEntity != null;
            double minY = Math.min(livingEntity.getY(), Mothership.this.getY());
            double maxY = Math.max(livingEntity.getY(), Mothership.this.getY()) + 1.0D;
            float angle = (float) Mth.atan2(livingEntity.getZ() - Mothership.this.getZ(), livingEntity.getX() - Mothership.this.getX());

            if (Mothership.this.distanceToSqr(livingEntity) < 9.0D)
            {
                for (int i = 0; i < 5; ++i)
                {
                    float f1 = angle + (float) i * (float) Math.PI * 0.4F;
                    this.createSpellEntity(Mothership.this.getX() + (double) Mth.cos(f1) * 1.5D, Mothership.this.getZ() + (double) Mth.sin(f1) * 1.5D, minY, maxY);
                }

                for (int k = 0; k < 8; ++k)
                {
                    float f2 = angle + (float) k * (float) Math.PI * 2.0F / 8.0F + 1.2566371F;
                    this.createSpellEntity(Mothership.this.getX() + (double) Mth.cos(f2) * 2.5D, Mothership.this.getZ() + (double) Mth.sin(f2) * 2.5D, minY, maxY);
                }
            }
            else
            {
                for (int l = 0; l < 16; ++l)
                {
                    double d2 = 1.25D * (double) (l + 1);
                    this.createSpellEntity(Mothership.this.getX() + (double) Mth.cos(angle) * d2, Mothership.this.getZ() + (double) Mth.sin(angle) * d2, minY, maxY);
                }
            }
        }

        private void createSpellEntity(double x, double z, double minY, double maxY)
        {
            BlockPos blockPos = new BlockPos((int) x, (int) maxY, (int) z);

            do
            {
                BlockPos blockPos1 = blockPos.below();
                BlockState blockState = Mothership.this.level().getBlockState(blockPos1);
                if (blockState.isFaceSturdy(Mothership.this.level(), blockPos1, Direction.UP))
                {
                    if (!Mothership.this.level().isEmptyBlock(blockPos))
                    {
                        BlockState blockState1 = Mothership.this.level().getBlockState(blockPos);
                        VoxelShape voxelShape = blockState1.getCollisionShape(Mothership.this.level(), blockPos);

                        if (!voxelShape.isEmpty()) { voxelShape.max(Direction.Axis.Y); }
                    }
                    break;
                }

                blockPos = blockPos.below();
            } while (blockPos.getY() >= Mth.floor(minY) - 1);

            ServerLevel serverWorld = (ServerLevel) Mothership.this.level();

            for (int i = 0; i < 1; ++i)
            {
                BlockPos blockPos2 = Mothership.this.blockPosition().offset(-2 + Mothership.this.random.nextInt(1), 1, -2 + Mothership.this.random.nextInt(1));
                CelestroidShip celestroidShip = EntitiesSTLCON.CELESTROID_SHIP.get().create(Mothership.this.level());
                assert celestroidShip != null;
                celestroidShip.moveTo(blockPos2, 0.0F, 0.0F);

                celestroidShip.finalizeSpawn(serverWorld, Mothership.this.level().getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                serverWorld.addFreshEntityWithPassengers(celestroidShip);
            }
        }
        protected SoundEvent getSpellPrepareSound() { return SoundEvents.BEACON_ACTIVATE; }
    }
}