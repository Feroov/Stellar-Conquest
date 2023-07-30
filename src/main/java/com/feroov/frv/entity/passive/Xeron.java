package com.feroov.frv.entity.passive;

import com.feroov.frv.block.BlocksSTLCON;
import com.feroov.frv.entity.AnimationConstants;
import com.feroov.frv.entity.monster.Celestroid;
import com.feroov.frv.entity.monster.Xenaptor;
import com.feroov.frv.item.ItemsSTLCON;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;

import javax.annotation.Nonnull;
import java.util.Random;


public class Xeron extends Animal implements GeoEntity
{
    // Cache for the AnimatableInstance used by this entity.
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // The temp items
    private static final Ingredient ITEM_INTEREST = Ingredient.of(ItemsSTLCON.LUMIBLOOM.get(), ItemsSTLCON.LUMIBLOOM_SEEDS.get());
    private static final int INTERACT_COOLDOWN = 20;
    private int interactCooldown = 0;

    /**
     * Constructs a new Xeron entity with the specified entity type and level.
     *
     * @param entityType The entity type of the Xeron.
     * @param level      The level in which the Xeron will exist.
     */
    public Xeron(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
    }

    /**
     * Sets the attributes for the Xeron entity.
     *
     * @return An AttributeSupplier containing the configured attributes for the Xeron.
     */
    public static AttributeSupplier setAttributes()
    {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 7.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D).build();
    }

    /**
     * Registers the goals for this entity, defining its behavior.
     */
    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Mob.class, 25.0F));
        this.goalSelector.addGoal(1, new TemptGoal(this, 0.8D, ITEM_INTEREST, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.73D));
        this.goalSelector.addGoal(7, new MoveTowardsRestrictionGoal(this, 0.73D));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Xenaptor.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Celestroid.class, 6.0F, 1.0D, 1.2D));
    }

    /**
     * Called during each game tick to update the Xeron entity.
     * It updates the Xeron's state and behaviors.
     */
    @Override
    public void tick() { super.tick(); if (interactCooldown > 0) { interactCooldown--; }}

    /**
     * Handles player interaction with the Xeron entity.
     *
     * @param player           The player interacting with the Xeron.
     * @param interactionHand  The hand used for the interaction.
     * @return The result of the interaction.
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand)
    {
        if (interactCooldown > 0) { return InteractionResult.PASS; }
        interactCooldown = INTERACT_COOLDOWN;
        this.playSound(SoundEventsSTLCON.XERON_AMBIENT.get(), 1.0F, 1.6F);

        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (level().isClientSide())
        {
            boolean flag = itemstack.is(ItemsSTLCON.LUMIBLOOM.get()) || itemstack.is(ItemsSTLCON.LUMIBLOOM_SEEDS.get());
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        else
        {
            if (itemstack.is(ItemsSTLCON.LUMIBLOOM.get()) || itemstack.is(ItemsSTLCON.LUMIBLOOM_SEEDS.get()))
            {
                if (!player.getAbilities().instabuild) { itemstack.shrink(1); }

                double jumpVelocity = 0.4;
                setDeltaMovement(getDeltaMovement().add(0, jumpVelocity, 0));

                ItemStack droppedItem = getRandomDrop();
                if (droppedItem != null)
                {
                    double x = getX();
                    double y = getY() + 1.2;
                    double z = getZ();
                    spawnAtLocation(droppedItem, x, y, z);

                    return InteractionResult.SUCCESS;
                }
            }
            return super.mobInteract(player, interactionHand);
        }
    }

    /**
     * Get a random item to drop when Xeron is interacted with.
     *
     * @return The ItemStack representing the item to drop, or null if no item should be dropped.
     */
    private ItemStack getRandomDrop()
    {
        Random random = new Random();
        int randomNumber = random.nextInt(6);

        // Rare drops
        if (randomNumber == 0) { return new ItemStack(ItemsSTLCON.XENITE_INGOT.get());}
        else
        {
            return switch (randomNumber) // Common drops
            {
                case 1 -> new ItemStack(BlocksSTLCON.XENOS_SAPLING.get());
                case 2 -> new ItemStack(BlocksSTLCON.XENOSDIRT.get());
                case 3 -> new ItemStack(BlocksSTLCON.XENOS_LOG.get());
                case 4 -> new ItemStack(ItemsSTLCON.USKIUM.get());
                case 5 -> new ItemStack(Items.SUGAR);
                default -> null;
            };
        }
    }

    /**
     * Spawns an ItemEntity at the specified location with the given ItemStack.
     *
     * @param stack The ItemStack to spawn as an ItemEntity.
     * @param x     The x-coordinate of the spawn location.
     * @param y     The y-coordinate of the spawn location.
     * @param z     The z-coordinate of the spawn location.
     * @return The spawned ItemEntity instance, or null if no entity was spawned.
     */
    @Nullable
    public ItemEntity spawnAtLocation(ItemStack stack, double x, double y, double z)
    {
        if (stack.isEmpty()) { return null; }
        else if (level().isClientSide()) { return null; }
        else
        {
            ItemEntity itemEntity = new ItemEntity(level(), x, y, z, stack);
            itemEntity.setDefaultPickUpDelay();
            if (captureDrops() != null)  { captureDrops().add(itemEntity); }
            else { level().addFreshEntity(itemEntity); }
            return itemEntity;
        }
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        this.playSound(SoundEventsSTLCON.XERON_AMBIENT.get(), 1.0F, 1.0F);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        this.playSound(SoundEventsSTLCON.XERON_HURT.get(), 1.0F, 1.0F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        this.playSound(SoundEvents.SHULKER_DEATH, 1.0F, 2.0F);
        return null;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) { return 1.0F; }

    /**
     * Registers the animation controllers for the Xeron entity.
     *
     * @param controllerRegistrar The registrar for the animation controllers.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "livingController", 0, event ->
        {
            if (event.isMoving() || this.swinging) return event.setAndContinue(AnimationConstants.WALK);
            return event.setAndContinue(AnimationConstants.IDLE);
        }));
    }

    /**
     * Gets the AnimatableInstanceCache associated with the Xeron entity.
     * The AnimatableInstanceCache is responsible for caching Animatable instances for animations.
     *
     * @return The AnimatableInstanceCache used by the Xeron.
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageableMob) { return null; }
}
