package com.feroov.frv.entity;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Celestroid;
import com.feroov.frv.entity.projectile.RaygunBeam;
import com.feroov.frv.entity.projectile.CelestroidBeam;
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

    // Monster Entities
    public static final RegistryObject<EntityType<Celestroid>> CELESTROID =
            ENTITY_TYPES.register("celestroid",
                    () -> EntityType.Builder.of(Celestroid::new, MobCategory.MONSTER)
                            .sized(2.0f, 2.0f)
                            .build(new ResourceLocation(STLCON.MOD_ID, "celestroid").toString()));

    // Projectiles Entities
    public static final RegistryObject<EntityType<RaygunBeam>> RAYGUN_BEAM = ENTITY_TYPES.register("raygun_beam",
            () -> EntityType.Builder.<RaygunBeam>of(RaygunBeam::new, MobCategory.MISC).sized(1.0F, 1.0F)
                    .updateInterval(10)
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "raygun_beam").toString()));

    public static final RegistryObject<EntityType<CelestroidBeam>> CELESTROID_BEAM = ENTITY_TYPES.register("celestroid_beam",
            () -> EntityType.Builder.<CelestroidBeam>of(CelestroidBeam::new, MobCategory.MISC).sized(1.0F, 1.0F)
                    .updateInterval(10)
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "celestroid_beam").toString()));


    public static void register(IEventBus eventBus) { ENTITY_TYPES.register(eventBus); }
}
