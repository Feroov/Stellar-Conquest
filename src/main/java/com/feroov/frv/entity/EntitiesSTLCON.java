package com.feroov.frv.entity;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.neutral.Celestroid;
import com.feroov.frv.entity.neutral.Mothership;
import com.feroov.frv.entity.neutral.Xeron;
import com.feroov.frv.entity.neutral.XeronGuard;
import com.feroov.frv.entity.projectile.CelestroidBeam;
import com.feroov.frv.entity.projectile.MothershipBeam;
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

    // Monster Entities
    public static final RegistryObject<EntityType<Celestroid>> CELESTROID =
            ENTITY_TYPES.register("celestroid",
                    () -> EntityType.Builder.of(Celestroid::new, MobCategory.CREATURE)
                            .sized(3.0f, 3.0f).canSpawnFarFromPlayer()
                            .build(new ResourceLocation(STLCON.MOD_ID, "celestroid").toString()));


    public static final RegistryObject<EntityType<Mothership>> MOTHERSHIP =
            ENTITY_TYPES.register("mothership",
                    () -> EntityType.Builder.of(Mothership::new, MobCategory.MONSTER)
                            .sized(8.0f, 8.0f).canSpawnFarFromPlayer()
                            .build(new ResourceLocation(STLCON.MOD_ID, "mothership").toString()));

    public static final RegistryObject<EntityType<Xeron>> XERON =
            ENTITY_TYPES.register("xeron",
                    () -> EntityType.Builder.of(Xeron::new, MobCategory.CREATURE)
                            .sized(0.5f, 1.2f).canSpawnFarFromPlayer()
                            .build(new ResourceLocation(STLCON.MOD_ID, "xeron").toString()));

    public static final RegistryObject<EntityType<XeronGuard>> XERON_GUARD =
            ENTITY_TYPES.register("xeron_guard",
                    () -> EntityType.Builder.of(XeronGuard::new, MobCategory.CREATURE)
                            .sized(0.5f, 1.2f).canSpawnFarFromPlayer()
                            .build(new ResourceLocation(STLCON.MOD_ID, "xeron_guard").toString()));

    // Projectiles Entities
    public static final RegistryObject<EntityType<RaygunBeam>> RAYGUN_BEAM = ENTITY_TYPES.register("raygun_beam",
            () -> EntityType.Builder.<RaygunBeam>of(RaygunBeam::new, MobCategory.MISC).sized(1.0F, 1.0F)
                    .updateInterval(10)
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "raygun_beam").toString()));

    public static final RegistryObject<EntityType<CelestroidBeam>> CELESTROID_BEAM = ENTITY_TYPES.register("celestroid_beam",
            () -> EntityType.Builder.<CelestroidBeam>of(CelestroidBeam::new, MobCategory.MISC).sized(1.0F, 1.0F)
                    .updateInterval(10)
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "celestroid_beam").toString()));

    public static final RegistryObject<EntityType<MothershipBeam>> MOTHERSHIP_BEAM = ENTITY_TYPES.register("mothership_beam",
            () -> EntityType.Builder.<MothershipBeam>of(MothershipBeam::new, MobCategory.MISC).sized(3.0F, 3.0F)
                    .updateInterval(10)
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "mothership_beam").toString()));

    public static void register(IEventBus eventBus)  { ENTITY_TYPES.register(eventBus); }
}
