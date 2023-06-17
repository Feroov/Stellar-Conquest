package com.feroov.frv.entity.monster;


import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.ai.MothershipAttackGoal;
import com.feroov.frv.entity.projectile.MothershipBeam;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.Difficulty;
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

public class Mothership extends Ghast implements Enemy, GeoEntity
{
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS));

    public static final EntityDataAccessor<Integer> DATA_ATTACK_CHARGE_ID = SynchedEntityData.defineId(Mothership.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(Ghast.class, EntityDataSerializers.BOOLEAN);

    private MothershipAttackGoal attackAI;
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    protected int spellCastingTickCount;
    private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterIllager.class, EntityDataSerializers.BYTE);
    private Mothership.SpellType currentSpell = Mothership.SpellType.NONE;


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
        this.goalSelector.addGoal(2, attackAI = new MothershipAttackGoal(this));
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

    public SoundEvent getWarnSound()
    {
        return SoundEvents.BEACON_ACTIVATE;
    }

    @Override
    public boolean isPersistenceRequired() { return super.isPersistenceRequired(); }

    @Override
    public void checkDespawn(){
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) { this.discard(); }
        else { super.checkDespawn(); }
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



    public void setCharging(boolean pCharging) { this.entityData.set(DATA_IS_CHARGING, pCharging); }

    public void shootRayBeam()
    {
        Vec3 vec3d = this.getViewVector(1.0F);
        double d2 = this.getTarget().getX() - (this.getX() + vec3d.x() * 4.0D);
        double d3 = this.getTarget().getBoundingBox().minY + this.getTarget().getBbHeight() / 2.0F - (0.5D + this.getY() + this.getBbHeight() / 2.0F);
        double d4 = this.getTarget().getZ() - (this.getZ() + vec3d.z() * 4.0D);

        MothershipBeam mothershipBeam = new MothershipBeam(this.level(), this, d2, d3, d4, 1);
        mothershipBeam.setPos(this.getX() + vec3d.x() * 4.0D, this.getY() + this.getBbHeight() / 2.0F + 0.5D, this.getZ() + vec3d.z() * 4.0D);
        this.level().addFreshEntity(mothershipBeam);

        if (this.getRandom().nextInt(6) == 0) {
            this.setTarget(null);
        }
    }

    public boolean shouldAttack(LivingEntity living) { return true; }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) { return 1.65F; }


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
    public boolean canBeCollidedWith() { return false; }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public void startSeenByPlayer(ServerPlayer player)
    {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player)
    {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public int getMaxSpawnClusterSize() { return 1; }


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


    public enum SpellType
    {
        NONE(0, 0.0D, 0.0D, 0.0D),
        SUMMON_CELESTROID(1, 0.7D, 0.7D, 0.8D);

        private final int id;
        private final double[] spellColor;

        private SpellType(int id, double d1, double d2, double d3)
        {
            this.id = id;
            this.spellColor = new double[]{d1, d2, d3};
        }

        public static SpellType byId(int id)
        {
            for (SpellType spellType : values()) { if (id == spellType.id) { return spellType; } }
            return NONE;
        }
    }

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

        protected abstract SpellType getSpell();
    }

    protected SoundEvent getCastingSoundEvent() { return null; }

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
            double minY = Math.min(livingEntity.getY(), Mothership.this.getY());
            double maxY = Math.max(livingEntity.getY(), Mothership.this.getY()) + 1.0D;
            float angle = (float) Mth.atan2(livingEntity.getZ() - Mothership.this.getZ(), livingEntity.getX() - Mothership.this.getX());

            if (Mothership.this.distanceToSqr(livingEntity) < 9.0D)
            {
                for (int i = 0; i < 5; ++i)
                {
                    float f1 = angle + (float) i * (float) Math.PI * 0.4F;
                    this.createSpellEntity(Mothership.this.getX() + (double) Mth.cos(f1) * 1.5D, Mothership.this.getZ() + (double) Mth.sin(f1) * 1.5D, minY, maxY, f1, 0);
                }

                for (int k = 0; k < 8; ++k)
                {
                    float f2 = angle + (float) k * (float) Math.PI * 2.0F / 8.0F + 1.2566371F;
                    this.createSpellEntity(Mothership.this.getX() + (double) Mth.cos(f2) * 2.5D, Mothership.this.getZ() + (double) Mth.sin(f2) * 2.5D, minY, maxY, f2, 3);
                }
            }
            else
            {
                for (int l = 0; l < 16; ++l)
                {
                    double d2 = 1.25D * (double) (l + 1);
                    int j = 1 * l;
                    this.createSpellEntity(Mothership.this.getX() + (double) Mth.cos(angle) * d2, Mothership.this.getZ() + (double) Mth.sin(angle) * d2, minY, maxY, angle, j);
                }
            }
        }

        private void createSpellEntity(double x, double z, double minY, double maxY, float angle, int j)
        {
            BlockPos blockPos = new BlockPos((int) x, (int) maxY, (int) z);
            boolean flag = false;
            double d0 = 0.0D;

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

                        if (!voxelShape.isEmpty()) { d0 = voxelShape.max(Direction.Axis.Y); }
                    }
                    flag = true;
                    break;
                }

                blockPos = blockPos.below();
            } while (blockPos.getY() >= Mth.floor(minY) - 1);

            ServerLevel serverWorld = (ServerLevel) Mothership.this.level();

            for (int i = 0; i < 1; ++i)
            {
                BlockPos blockPos2 = Mothership.this.blockPosition().offset(-2 + Mothership.this.random.nextInt(1), 1, -2 + Mothership.this.random.nextInt(1));
                Celestroid corruptMinion = EntitiesSTLCON.CELESTROID.get().create(Mothership.this.level());
                corruptMinion.moveTo(blockPos2, 0.0F, 0.0F);

                corruptMinion.finalizeSpawn(serverWorld, Mothership.this.level().getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                serverWorld.addFreshEntityWithPassengers(corruptMinion);
            }
        }
        protected SoundEvent getSpellPrepareSound() { return SoundEvents.BEACON_ACTIVATE; }
        protected SpellType getSpell() { return SpellType.NONE; }
    }
}