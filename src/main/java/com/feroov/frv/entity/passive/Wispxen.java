package com.feroov.frv.entity.passive;

import com.feroov.frv.entity.AnimationConstants;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.events.ModParticles;
import com.feroov.frv.item.ItemsSTLCON;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;

import javax.annotation.Nonnull;


public class Wispxen extends Animal implements GeoEntity
{
    // Cache for the AnimatableInstance used by this entity.
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // The temp items
    private static final Ingredient FOOD_ITEMS = Ingredient.of(ItemsSTLCON.LUMIBLOOM_SEEDS.get());

    /**
     * Constructs a new Wispxen entity with the specified entity type and level.
     *
     * @param entityType The entity type of the Wispxen.
     * @param level      The level in which the Wispxen will exist.
     */
    public Wispxen(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    /**
     * Sets the attributes for the Wispxen entity.
     *
     * @return An AttributeSupplier containing the configured attributes for the Wispxen.
     */
    public static AttributeSupplier setAttributes()
    {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.FLYING_SPEED, 0.17D).build();
    }

    /**
     * Registers the goals for this entity, defining its behavior.
     */
    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Mob.class, 25.0F));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 0.9D, FOOD_ITEMS, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.73D));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.73D));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    /**
     * Registers the animation controllers for the Wispxen entity.
     *
     * @param controllerRegistrar The registrar for the animation controllers.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "livingController", 0, event ->
        {
            return event.setAndContinue(AnimationConstants.IDLE);
        }));
    }

    @Override
    public boolean causeFallDamage(float v, float v2, DamageSource damageSource) { return false; }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) { return 0.2F; }

    /**
     * Checks if the given ItemStack is considered as food for the Wispxen.
     *
     * @param itemStack The ItemStack to check.
     * @return True if the ItemStack is food for the Wispxen, false otherwise.
     */
    @Override
    public boolean isFood(ItemStack itemStack) { return FOOD_ITEMS.test(itemStack); }

    /**
     * Creates a path navigation for the Wispxen in the given level.
     *
     * @param level The level in which the Wispxen will navigate.
     * @return The created path navigation.
     */
    @Override
    protected PathNavigation createNavigation(Level level)
    {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    /**
     * Handles the movement of the Wispxen based on the given Vec3.
     *
     * @param vec3 The Vec3 representing the movement.
     */
    @Override
    public void travel(Vec3 vec3)
    {
        if (this.isControlledByLocalInstance())
        {
            if (this.isInWater())
            {
                this.moveRelative(0.02F, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double)0.8F));
            }
            else if (this.isInLava())
            {
                this.moveRelative(0.02F, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            }
            else
            {
                this.moveRelative(this.getSpeed(), vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double)0.91F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    /**
     * Gets the AnimatableInstanceCache associated with the Wispxen entity.
     * The AnimatableInstanceCache is responsible for caching Animatable instances for animations.
     *
     * @return The AnimatableInstanceCache used by the Wispxen.
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 2.0F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEvents.RABBIT_HURT, 1.0F, 9.0F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.RABBIT_DEATH, 1.0F, 9.0F);
        return null;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageableMob)
    {
        return EntitiesSTLCON.WISPXEN.get().create(level);
    }

    /**
     * Called when the Wispxen reaches the age boundary.
     * This method is typically called when the entity grows to its maximum age.
     * It may perform additional actions when the age boundary is reached.
     */
    @Override
    protected void ageBoundaryReached()
    {
        super.ageBoundaryReached();
        if (!this.isBaby() && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))
        {
            this.spawnAtLocation(ItemsSTLCON.WISPXENDUST.get(), 1);
        }
    }

    /**
     * Handles the AI behavior for the Wispxen entity.
     * This method is called during every tick to update the entity's AI.
     * It may perform actions based on the AI state or the entity's environment.
     */
    @Override
    public void aiStep()
    {
        super.aiStep();
        if (this.level().isClientSide)
        {
            for(int i = 0; i < 1; i++)
            {
                this.level().addParticle(ModParticles.XENOSPHERE_PORTAL_PARTICLES.get(),
                        this.getX(),
                        this.getY() + 0.2D,
                        this.getZ(),
                        this.random.nextDouble() - 0.5D,
                        -this.random.nextDouble(),
                        (this.random.nextDouble() - 0.5D));
            }
        }
    }
}
