package com.feroov.frv.entity.neutral;

import com.feroov.frv.entity.AnimationConstants;
import com.feroov.frv.entity.monster.Celestroid;
import com.feroov.frv.entity.monster.Xenaptor;
import com.feroov.frv.entity.passive.Xeron;
import com.feroov.frv.item.ItemsSTLCON;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;

import javax.annotation.Nonnull;
import java.util.UUID;


public class XeronGuard extends TamableAnimal implements GeoEntity, NeutralMob
{
    // Cache for the AnimatableInstance used by this entity.
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // DataAccessor for tracking the 'attacking' state of the entity.
    protected static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(XeronGuard.class, EntityDataSerializers.BOOLEAN);

    // DataAccessor for tracking the remaining persistent anger time of the entity.
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(XeronGuard.class, EntityDataSerializers.INT);

    // Range for the persistent anger time (used for resetting).
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    // UUID of the persistent anger target entity.
    @Nullable
    private UUID persistentAngerTarget;

    // Ingredient representing the item that interests this entity.
    private static final Ingredient ITEM_INTEREST = Ingredient.of(ItemsSTLCON.XENITE_INGOT.get());

    /**
     * Constructs a new XeronGuard entity.
     *
     * @param entityType The EntityType of the entity.
     * @param level The Level in which the entity is spawned.
     */
    public XeronGuard(EntityType<? extends TamableAnimal> entityType, Level level)
    {
        super(entityType, level);
        this.xpReward = 20;
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        this.setTame(false);
    }

    /**
     * Sets the attributes for this entity.
     *
     * @return An AttributeSupplier with the specified attributes.
     */
    public static AttributeSupplier setAttributes()
    {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.5D).build();
    }

    /**
     * Registers the goals for this entity, defining its behavior.
     */
    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new TemptGoal(this, 0.6D, ITEM_INTEREST, false));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(1, new XeronMeleeAttack(this, 0.95D, true));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Mob.class, 25.0F));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.goalSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, this::isAngryAt));
        this.goalSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Celestroid.class, true));
        this.goalSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Xenaptor.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.73D));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.73D));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, true));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    /**
     * Handles the interaction between the XeronGuard entity and a player.
     *
     * @param player The player interacting with the entity.
     * @param interactionHand The hand used for the interaction.
     * @return The result of the interaction.
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand)
    {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (this.level().isClientSide)
        {
            boolean flag = this.isOwnedBy(player) || this.isTame() || (itemstack.is(ItemsSTLCON.ASTRALITE_INGOT.get()) && !this.isAngry());
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        else
        {
            if (this.isTame())
            {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth())
                {
                    if (!player.getAbilities().instabuild) { itemstack.shrink(1); }
                    this.heal((float) itemstack.getFoodProperties(this).getNutrition());
                    this.gameEvent(GameEvent.EAT, this);
                    return InteractionResult.SUCCESS;
                }
                return super.mobInteract(player, interactionHand);
            }
            else if (itemstack.is(ItemsSTLCON.ASTRALITE_INGOT.get()) && !this.isAngry())
            {
                if (!player.getAbilities().instabuild) { itemstack.shrink(1); }
                if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player))
                {
                    this.tame(player);
                    this.navigation.stop();
                    this.setTarget((LivingEntity)null);
                    this.level().broadcastEntityEvent(this, (byte)7);
                } else { this.level().broadcastEntityEvent(this, (byte)6); }
                return InteractionResult.SUCCESS;
            }
            return super.mobInteract(player, interactionHand);
        }
    }

    /**
     * Determines if this XeronGuard entity wants to attack the specified entities.
     *
     * @param livingEntity The entity to attack.
     * @param livingEntity2 The second entity (usually the owner or another entity).
     * @return True if the XeronGuard wants to attack, otherwise false.
     */
    @Override
    public boolean wantsToAttack(LivingEntity livingEntity, LivingEntity livingEntity2)
    {
        if (!(livingEntity instanceof Creeper) && !(livingEntity instanceof Ghast) && !(livingEntity instanceof Xeron))
        {
            if (livingEntity instanceof XeronGuard)
            {
                XeronGuard xeronGuard = (XeronGuard)livingEntity;
                return !xeronGuard.isTame() || xeronGuard.getOwner() != livingEntity2;
            }
            else if (livingEntity instanceof Player && livingEntity2 instanceof Player && !((Player)livingEntity2).canHarmPlayer((Player)livingEntity)) { return false; }
            else if (livingEntity instanceof AbstractHorse && ((AbstractHorse)livingEntity).isTamed()) { return false; }
            else { return !(livingEntity instanceof TamableAnimal) || !((TamableAnimal)livingEntity).isTame();} } else { return false;}
    }

    /**
     * Handles hurting the specified entity.
     *
     * @param entity The entity to hurt.
     * @return True if the entity was hurt successfully, otherwise false.
     */
    @Override
    public boolean doHurtTarget(Entity entity)
    {
        boolean success = entity.hurt(this.damageSources().mobAttack(this), 7.0F);
        if (success) { entity.push(0.0D, 0.2D, 0.0D); }
        return success;
    }

    /**
     * Sets the tame state of the XeronGuard entity and adjusts its attributes accordingly.
     *
     * @param b The new tame state.
     */
    @Override
    public void setTame(boolean b)
    {
        super.setTame(b);
        if (b)
        {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
            this.setHealth(20.0F);
        }
        else { this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0D); }

        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEventsSTLCON.XERON_AMBIENT.get(), 1.0F, 0.7F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEventsSTLCON.XERON_HURT.get(), 1.0F, 0.7F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.SHULKER_DEATH, 1.0F, 1.6F);
        return null;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) { return 1.0F; }

    /**
     * Registers the animation controllers for the XeronGuard entity.
     *
     * @param controllerRegistrar The registrar for the animation controllers.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "livingController", 0, event ->
        {
            if (isAttacking()) return event.setAndContinue(AnimationConstants.ATTACK);
            if (isAggressive() && this.walkAnimation.speed() > 0.35F && this.onGround() && !this.swinging) return event.setAndContinue(AnimationConstants.DEFENSIVE);
            if (event.isMoving() || this.swinging) return event.setAndContinue(AnimationConstants.WALK);
            return event.setAndContinue(AnimationConstants.IDLE);
        }));
    }

    /**
     * Gets the animatable instance cache for the XeronGuard entity.
     *
     * @return The animatable instance cache.
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob)
    {
        return null;
    }

    /**
     * Defines the synched data for the XeronGuard entity.
     */
    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    public void setAttacking(boolean attack) { this.entityData.set(ATTACKING, attack); }

    public boolean isAttacking() { return this.entityData.get(ATTACKING); }

    /**
     * Saves additional entity data to NBT.
     *
     * @param compoundTag The NBT tag to save the data to.
     */
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag)
    {
        super.addAdditionalSaveData(compoundTag);
        this.addPersistentAngerSaveData(compoundTag);
    }

    /**
     * Reads additional entity data from NBT.
     *
     * @param compoundTag The NBT tag to read the data from.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag)
    {
        super.readAdditionalSaveData(compoundTag);
        this.readPersistentAngerSaveData(this.level(), compoundTag);
    }

    /**
     * Handles the AI behavior of the XeronGuard entity.
     */
    public void aiStep()
    {
        super.aiStep();
        if (!this.level().isClientSide)
        {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
    }

    /**
     * Gets the remaining persistent anger time of the XeronGuard entity.
     *
     * @return The remaining persistent anger time.
     */
    @Override
    public int getRemainingPersistentAngerTime()
    {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    /**
     * Sets the remaining persistent anger time of the XeronGuard entity.
     *
     * @param remainingPersistentAngerTime The remaining persistent anger time.
     */
    @Override
    public void setRemainingPersistentAngerTime(int remainingPersistentAngerTime)
    {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, remainingPersistentAngerTime);
    }

    /**
     * Gets the UUID of the persistent anger target of the XeronGuard entity.
     *
     * @return The UUID of the persistent anger target.
     */
    @Nullable
    @Override
    public UUID getPersistentAngerTarget() { return this.persistentAngerTarget; }

    /**
     * Sets the UUID of the persistent anger target of the XeronGuard entity.
     *
     * @param uuid The UUID of the persistent anger target.
     */
    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) { this.persistentAngerTarget = uuid; }

    /**
     * Starts the persistent anger timer for the XeronGuard entity.
     */
    @Override
    public void startPersistentAngerTimer() { this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random)); }

    /**
     * Custom melee attack goal for the XeronGuard entity.
     */
    public static class XeronMeleeAttack extends MeleeAttackGoal
    {
        private XeronGuard entity;
        private int animCounter = 0;
        private int animTickLength = 19;

        /**
         * Custom melee attack goal for the XeronGuard entity.
         *
         * @param pathfinderMob               The entity that will be performing the melee attack.
         * @param speedModifier               The speed modifier for the attack movement.
         * @param followingTargetEvenIfNotSeen Whether to continue following the target even if it's not seen.
         */
        public XeronMeleeAttack(PathfinderMob pathfinderMob, double speedModifier, boolean followingTargetEvenIfNotSeen)
        {
            super(pathfinderMob, speedModifier, followingTargetEvenIfNotSeen);
            if(pathfinderMob instanceof XeronGuard xeron)
            {
                entity = xeron;
            }
        }

        /**
         * Checks and performs the attack on the living entity.
         *
         * @param livingEntity The living entity to attack.
         * @param d1 The distance squared to the target.
         */
        @Override
        protected void checkAndPerformAttack(LivingEntity livingEntity, double d1)
        {
            if (d1 <= this.getAttackReachSqr(livingEntity) && this.getTicksUntilNextAttack() <= 0)
            {
                if(entity != null)
                {
                    entity.setAttacking(true);
                    animCounter = 0;
                }
            }

            super.checkAndPerformAttack(livingEntity, d1);
        }

        /**
         * Ticks the attack animation.
         */
        @Override
        public void tick()
        {
            super.tick();
            if(entity.isAttacking())
            {
                animCounter++;

                if(animCounter >= animTickLength)
                {
                    animCounter = 0;
                    entity.setAttacking(false);
                }
            }
        }

        /**
         * Stops the attack animation.
         */
        @Override
        public void stop()
        {
            animCounter = 0;
            entity.setAttacking(false);
            super.stop();
        }
    }
}
