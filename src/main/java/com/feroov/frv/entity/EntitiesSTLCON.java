package com.feroov.frv.entity;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.projectile.RaygunBeam;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntitiesSTLCON
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, STLCON.MOD_ID);

    // Projectiles
    public static final RegistryObject<EntityType<RaygunBeam>> RAYGUN_BEAM = ENTITY_TYPES.register("raygun_beam",
            () -> EntityType.Builder.<RaygunBeam>of(RaygunBeam::new, MobCategory.MISC).sized(1.0F, 1.0F)
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "raygun_beam").toString()));


    public static void register(IEventBus eventBus) { ENTITY_TYPES.register(eventBus); }
}
