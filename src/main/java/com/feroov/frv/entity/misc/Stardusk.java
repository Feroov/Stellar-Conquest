package com.feroov.frv.entity.misc;

import com.feroov.frv.entity.projectile.StarduskBeam;
import com.feroov.frv.events.ModParticles;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = "frv", bus = Mod.EventBusSubscriber.Bus.MOD)
public class Stardusk extends Animal implements IModBusEvent, GeoEntity
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int soundTimer = 0;
    private int soundDelay = 20;
    private long lastShotTime = 0L;
    private static final long COOLDOWN_DURATION = 3000L;

    /**
     * Creates a Stardusk entity.
     *
     * @param entityType The entity type.
     * @param level      The level.
     */
    public Stardusk(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
        this.noCulling = true;
    }

    /**
     * Sets the attributes for the Stardusk entity.
     *
     * @return The attribute supplier.
     */
    public static AttributeSupplier setAttributes()
    {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FLYING_SPEED, 0.98D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 100.0D).build();
    }

    /**
     * Registers the goals for the Stardusk entity.
     */
    @Override
    protected void registerGoals()  { this.goalSelector.addGoal(0, new FloatGoal(this)); }

    /**
     * Registers the animation controllers for the Stardusk entity.
     *
     * @param controllerRegistrar The controller registrar.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState)
    {
        if ((this.dead || this.getHealth() < 0.01 || this.isDeadOrDying()))
        {
            animationState.getController().setAnimation(RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.PLAY_ONCE));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) { return null; }

    @Override
    public boolean causeFallDamage(float f1, float f2, DamageSource damageSource) { return false; }

    public LivingEntity getControllingPassenger() { return this.getPassengers().isEmpty() ? null : (LivingEntity) this.getPassengers().get(0); }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)  { this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.5F, 0.2F); return null; }

    @Override
    protected SoundEvent getDeathSound()  { this.playSound(SoundEventsSTLCON.ENGINE_OFF.get(), 4.0F, 1.0F); return null; }

    @Override
    public int getExperienceReward() { return 0; }

    /**
     * Checks if the Stardusk entity can stand on the given fluid.
     *
     * @param fluidState The fluid state.
     * @return True if the entity can stand on the fluid, false otherwise.
     */
    @Override
    public boolean canStandOnFluid(FluidState fluidState) { return fluidState.is(FluidTags.WATER) && fluidState.is(FluidTags.LAVA); }

    /**
     * Ticks the death animation for the Stardusk entity.
     */
    @Override
    protected void tickDeath()
    {
        ++this.deathTime;
        if (this.deathTime == 40 && !this.level().isClientSide())
        {
            this.level().broadcastEntityEvent(this, (byte)40);
            this.remove(RemovalReason.KILLED);
        }
    }

    /**
     * Handles the interaction with the Stardusk entity.
     *
     * @param player The interacting player.
     * @param hand   The interaction hand.
     * @return The interaction result.
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        if (!this.isVehicle())
        {
            player.startRiding(this);
            if (!this.level().isClientSide)
            {
                MutableComponent msg = Component.literal("Spacebar to ascend, CTRL to descend, right-click to shoot!");
                player.level().players().forEach(p -> p.displayClientMessage(msg, true));
            }
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                    SoundEventsSTLCON.ENGINE_START.get(),
                    SoundSource.AMBIENT, 5.0f, 1.0f, false);
            return super.mobInteract(player, hand);
        }
        else if (hand == InteractionHand.MAIN_HAND && this.getControllingPassenger() == player)
        {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime >= COOLDOWN_DURATION)
            {
                level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.0F, 7.0F);

                if (!this.level().isClientSide)
                {
                    double pitch = Math.toRadians(player.getXRot());
                    double yaw = Math.toRadians(player.getYRot());
                    double dirX = -Math.sin(yaw) * Math.cos(pitch);
                    double dirY = -Math.sin(pitch);
                    double dirZ = Math.cos(yaw) * Math.cos(pitch);
                    double x = player.getX() + dirX;
                    double y = player.getEyeY() + dirY;
                    double z = player.getZ() + dirZ;
                    StarduskBeam arrow = new StarduskBeam(this.level(), player);
                    arrow.shoot(dirX, dirY, dirZ, 3.0F, 3.0F); // Adjust the velocity as needed
                    arrow.setPos(x, y, z);
                    level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEventsSTLCON.RAYGUN_SHOOT.get(), SoundSource.PLAYERS, 2.0F,
                            1.0F / (level().random.nextFloat() * 0.4F + 10.2F) + 0.25F);
                    this.level().addFreshEntity(arrow);
                }
                lastShotTime = currentTime;
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    /**
     * Handles the damage taken by the Stardusk entity.
     *
     * @param source The damage source.
     * @param amount The amount of damage.
     * @return True if the damage was successfully applied, false otherwise.
     */
    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (this.isVehicle())
        {
            Entity attacker = source.getEntity();
            if (attacker instanceof Player && this.getControllingPassenger() == attacker)
            {
                return false;
            }
        }
        return super.hurt(source, amount);
    }

    /**
     * Ticks the entity logic for the Stardusk entity.
     */
    @Override
    public void tick()
    {
        super.tick();
        if (this.level().isClientSide && this.isAlive() && this.getControllingPassenger() != null)
        {
            if (soundTimer <= 0)
            {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                        SoundEventsSTLCON.STARDUSK_AMBIENT.get(),
                        SoundSource.AMBIENT, 5.0f, 1.0f, false);

                soundTimer = soundDelay;
            } else { soundTimer--; }
        }
    }

    /**
     * Positions the rider on the Stardusk entity.
     *
     * @param ridden   The ridden entity.
     * @param pCallback The callback function.
     */
    @Override
    protected void positionRider(Entity ridden, MoveFunction pCallback)
    {
        if (hasPassenger(ridden))
        {
            double offsetY = 0.8;

            double x = getX();
            double y = getY() + offsetY;
            double z = getZ();

            pCallback.accept(ridden, x, y, z);

            if (getFirstPassenger() instanceof LivingEntity)
            {
                ridden.xRotO = ridden.getXRot();
                ridden.yRotO = ridden.getYRot();
                ridden.setYBodyRot(yBodyRot);
            }
        }
    }

    /**
     * Handles the client setup for the Stardusk entity.
     * (Had to be created this way instead of SubscribeEvent
     * Due to crashes and errors!)
     * @param event The client setup event.
     */
    public static void clientSetup(FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.addListener(Stardusk::clientTickEvent);
    }

    /**
     * Retrieves the Stardusk entity instance.
     *
     * @return The Stardusk entity instance.
     */
    private static Stardusk getStarduskInstance()
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null)
        {
            Entity entity = mc.player.getVehicle();
            if (entity instanceof Stardusk) { return (Stardusk) entity; }
        }
        return null;
    }

    /**
     * Checks if the specified key is currently pressed.
     *
     * @param windowHandle The window handle.
     * @param keyCode      The key code.
     * @return True if the key is pressed, false otherwise.
     */
    public static boolean isKeyDown(long windowHandle, int keyCode)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null && minecraft.getWindow() != null)
        {
            long handle = minecraft.getWindow().getWindow();
            if (handle != 0) { return GLFW.glfwGetKey(handle, keyCode) == GLFW.GLFW_PRESS; }
        }
        return false;
    }
    private boolean initialDelay = true;
    /**
     * Handles the travel logic for the Stardusk entity.
     *
     * @param movementInput The movement input vector.
     */
    @Override
    public void travel(Vec3 movementInput)
    {
        if (this.isVehicle() && this.getControllingPassenger() instanceof LivingEntity)
        {
            LivingEntity livingEntity = (LivingEntity) this.getControllingPassenger();
            this.setYRot(livingEntity.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(livingEntity.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;

            float forward = livingEntity.zza;
            float strafe = livingEntity.xxa;

            handleAscendDescendLogic();

            if (isKeyDown(GLFW.glfwGetCurrentContext(), GLFW.GLFW_KEY_SPACE) && !isKeyDown(GLFW.glfwGetCurrentContext(), GLFW.GLFW_KEY_LEFT_CONTROL))
            {
                double hoverY = livingEntity.getY() + this.getEyeHeight() - this.getY();
                double hoverStrength = 0.1;

                if (Math.abs(this.getDeltaMovement().y) < 0.01)
                {
                    this.setDeltaMovement(new Vec3(this.getDeltaMovement().x, 0, this.getDeltaMovement().z));
                }
                else
                {
                    double hoverMotionY = hoverY * hoverStrength;
                    this.setDeltaMovement(this.getDeltaMovement().add(0, hoverMotionY, 0));
                }
            }

            if (livingEntity.isShiftKeyDown()) { forward = 0.5F; strafe = 0.5F; }
            float flyingSpeed = (float) this.getAttributeValue(Attributes.FLYING_SPEED);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(flyingSpeed);
            this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(new Vec3(strafe, movementInput.y, forward));

            this.tryCheckInsideBlocks();
        } else { super.travel(movementInput); }
    }

    /**
     * Handles the ascend and descend logic for the Stardusk entity.
     */
    private void handleAscendDescendLogic()
    {
        long windowHandle = Minecraft.getInstance().getWindow().getWindow();
        if (isKeyDown(windowHandle, GLFW.GLFW_KEY_SPACE))
        {
            double ascendSpeed = 0.1;
            Vec3 motion = new Vec3(this.getDeltaMovement().x, ascendSpeed, this.getDeltaMovement().z);
            this.setDeltaMovement(motion);
        }
        else if (isKeyDown(windowHandle, GLFW.GLFW_KEY_LEFT_CONTROL))
        {
            if (this.onGround())
            {
                Vec3 motion = new Vec3(0, this.getDeltaMovement().y, 0);
                this.setDeltaMovement(motion);
            }
            else
            {
                double descendSpeed = -0.3;
                Vec3 motion = new Vec3(this.getDeltaMovement().x, descendSpeed, this.getDeltaMovement().z);
                this.setDeltaMovement(motion);
            }
        }
        else
        {
            Vec3 motion = new Vec3(this.getDeltaMovement().x, 0, this.getDeltaMovement().z);
            this.setDeltaMovement(motion);
        }
    }

    /**
     * Handles the client tick event for the Stardusk entity.
     *
     * @param event The client tick event.
     */
    public static void clientTickEvent(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().level != null)
        {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getWindow() != null)
            {
                long windowHandle = mc.getWindow().getWindow();

                if (isKeyDown(windowHandle, GLFW.GLFW_KEY_SPACE))
                {
                    Stardusk entity = getStarduskInstance();
                    if (entity != null && entity.isAlive() && entity.isVehicle())
                    {
                        Vec3 pos = entity.getDeltaMovement().add(0, 0.07, 0);
                        entity.setDeltaMovement(pos);
                    }
                }
                else if (isKeyDown(windowHandle, GLFW.GLFW_KEY_LEFT_CONTROL))
                {
                    Stardusk entity = getStarduskInstance();
                    if (entity != null && entity.isAlive() && entity.isVehicle())
                    {
                        Vec3 pos = entity.getDeltaMovement().subtract(0, 0.07, 0);
                        entity.setDeltaMovement(pos);
                    }
                }
            }
        }
    }

    @Override
    public boolean isPushable()  { return false; }

    /**
     * Performs AI logic for the Stardusk entity (spawning particles).
     */
    @Override
    public void aiStep()
    {
        super.aiStep();
        if (this.getControllingPassenger() != null && this.level().isClientSide)
        {
            for (int i = 0; i < 4; ++i)
            {
                this.level().addParticle(ModParticles.RAYGUN_PARTICLES.get(), this.getRandomX(0.5D),
                        this.getRandomY() - 1.45D, this.getRandomZ(0.5D),
                        (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }
}
