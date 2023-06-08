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

    @Override
    public boolean isPersistenceRequired() {
        return super.isPersistenceRequired();
    }

    @Override
    public void checkDespawn(){
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
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

        MothershipBeam mothershipBeam = new MothershipBeam(this.level(), this, d2, d3, d4, 1);
        mothershipBeam.setPos(this.getX() + vec3d.x() * 4.0D, this.getY() + this.getBbHeight() / 2.0F + 0.5D, this.getZ() + vec3d.z() * 4.0D);
        this.level().addFreshEntity(mothershipBeam);

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
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }


    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }


    public enum SpellType {
        NONE(0, 0.0D, 0.0D, 0.0D),
        SUMMON_CELESTROID(1, 0.7D, 0.7D, 0.8D);

        private final int id;
        private final double[] spellColor;

        private SpellType(int p_i47561_3_, double p_i47561_4_, double p_i47561_6_, double p_i47561_8_) {
            this.id = p_i47561_3_;
            this.spellColor = new double[]{p_i47561_4_, p_i47561_6_, p_i47561_8_};
        }

        public static SpellType byId(int p_193337_0_) {
            for(Mothership.SpellType spellcastingillagerentity$spelltype : values()) {
                if (p_193337_0_ == spellcastingillagerentity$spelltype.id) {
                    return spellcastingillagerentity$spelltype;
                }
            }
            return NONE;
        }
    }

    public abstract class UseSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        protected UseSpellGoal() {
        }

        public boolean canUse() {
            LivingEntity livingentity = Mothership.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                if (Mothership.this.isCastingSpell()) {
                    return false;
                } else {
                    return Mothership.this.tickCount >= this.nextAttackTickCount;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = Mothership.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
        }

        public void start() {
            this.attackWarmupDelay = this.getCastWarmupTime();
            Mothership.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = Mothership.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                Mothership.this.playSound(soundevent, 0.0F, 1.0F);
            }
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
            }

        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract Mothership.SpellType getSpell();
    }

    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    public boolean isCastingSpell() {
        if (this.level().isClientSide) {
            return this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
        } else {
            return this.spellCastingTickCount > 0;
        }
    }

    class AttackSpellGoal extends Mothership.UseSpellGoal {
        private AttackSpellGoal() {
        }

        protected int getCastingTime() {
            return 40;
        }

        protected int getCastingInterval() {
            return 100;
        }

        protected void performSpellCasting() {
            LivingEntity livingentity = Mothership.this.getTarget();
            double d0 = Math.min(livingentity.getY(), Mothership.this.getY());
            double d1 = Math.max(livingentity.getY(), Mothership.this.getY()) + 1.0D;
            float f = (float) Mth.atan2(livingentity.getZ() -
                    Mothership.this.getZ(), livingentity.getX() - Mothership.this.getX());
            if (Mothership.this.distanceToSqr(livingentity) < 9.0D) {
                for(int i = 0; i < 5; ++i) {
                    float f1 = f + (float)i * (float)Math.PI * 0.4F;
                    this.createSpellEntity(Mothership.this.getX() + (double)Mth.cos(f1) * 1.5D,
                            Mothership.this.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1, f1, 0);
                }

                for(int k = 0; k < 8; ++k) {
                    float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                    this.createSpellEntity(Mothership.this.getX() + (double)Mth.cos(f2) * 2.5D,
                            Mothership.this.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1, f2, 3);
                }
            } else {
                for(int l = 0; l < 16; ++l) {
                    double d2 = 1.25D * (double)(l + 1);
                    int j = 1 * l;
                    this.createSpellEntity(Mothership.this.getX() +
                            (double)Mth.cos(f) * d2, Mothership.this.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, j);
                }
            }

        }

        private void createSpellEntity(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
            BlockPos blockpos = new BlockPos((int) p_190876_1_, (int) p_190876_7_, (int) p_190876_3_);
            boolean flag = false;
            double d0 = 0.0D;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = Mothership.this.level().getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(Mothership.this.level(), blockpos1, Direction.UP)) {
                    if (!Mothership.this.level().isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = Mothership.this.level().getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(Mothership.this.level(), blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while(blockpos.getY() >= Mth.floor(p_190876_5_) - 1);

            ServerLevel serverworld = (ServerLevel)Mothership.this.level();

            for(int i = 0; i < 1; ++i) {
                BlockPos blockpos2 = Mothership.this.blockPosition().offset(-2 +
                        Mothership.this.random.nextInt(1), 1, -2 + Mothership.this.random.nextInt(1));
                Celestroid corruptMinion = EntitiesSTLCON.CELESTROID.get().create(Mothership.this.level());
                corruptMinion.moveTo(blockpos2, 0.0F, 0.0F);
                corruptMinion.finalizeSpawn(serverworld, Mothership.this.level().getCurrentDifficultyAt(blockpos),
                        MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
                serverworld.addFreshEntityWithPassengers(corruptMinion);
            }

        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.BEACON_ACTIVATE;
        }

        protected Mothership.SpellType getSpell() {
            return SpellType.NONE;
        }
    }
}