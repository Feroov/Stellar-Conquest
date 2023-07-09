package com.feroov.frv.entity;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.misc.Stardusk;
import com.feroov.frv.entity.monster.*;
import com.feroov.frv.entity.passive.Wispxen;
import com.feroov.frv.entity.passive.Xeron;
import com.feroov.frv.entity.neutral.XeronGuard;
import com.feroov.frv.entity.passive.Zephxen;
import com.feroov.frv.entity.projectile.*;
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
    public static final RegistryObject<EntityType<CelestroidShip>> CELESTROID_SHIP =
            ENTITY_TYPES.register("celestroid_ship",
                    () -> EntityType.Builder.of(CelestroidShip::new, MobCategory.CREATURE)
                            .sized(3.0f, 3.0f).canSpawnFarFromPlayer().fireImmune()
                            .build(new ResourceLocation(STLCON.MOD_ID, "celestroid_ship").toString()));

    public static final RegistryObject<EntityType<Celestroid>> CELESTROID =
            ENTITY_TYPES.register("celestroid",
                    () -> EntityType.Builder.of(Celestroid::new, MobCategory.CREATURE)
                            .sized(0.5f, 1.2f).canSpawnFarFromPlayer()
                            .build(new ResourceLocation(STLCON.MOD_ID, "celestroid").toString()));

    public static final RegistryObject<EntityType<Mothership>> MOTHERSHIP =
            ENTITY_TYPES.register("mothership",
                    () -> EntityType.Builder.of(Mothership::new, MobCategory.MONSTER)
                            .sized(8.0f, 8.0f).canSpawnFarFromPlayer().fireImmune()
                            .build(new ResourceLocation(STLCON.MOD_ID, "mothership").toString()));

    public static final RegistryObject<EntityType<Xenaptor>> XENAPTOR =
            ENTITY_TYPES.register("xenaptor",
                    () -> EntityType.Builder.of(Xenaptor::new, MobCategory.MONSTER)
                            .sized(1.2f, 0.8f).canSpawnFarFromPlayer()
                            .build(new ResourceLocation(STLCON.MOD_ID, "xenaptor").toString()));

    public static final RegistryObject<EntityType<MirrorbornSlime>> MIRRORBORN_SLIME =
            ENTITY_TYPES.register("mirrorborn_slime",
                    () -> EntityType.Builder.of(MirrorbornSlime::new, MobCategory.CREATURE)
                            .sized(0.50f, 0.50f).canSpawnFarFromPlayer()
                            .build(new ResourceLocation(STLCON.MOD_ID, "mirrorborn_slime").toString()));

    public static final RegistryObject<EntityType<MergedMirrorborn>> MERGED_MIRRORBORN_SLIME =
            ENTITY_TYPES.register("merged_mirrorborn",
                    () -> EntityType.Builder.of(MergedMirrorborn::new, MobCategory.CREATURE)
                            .sized(2.0f, 2.0f).canSpawnFarFromPlayer()
                            .build(new ResourceLocation(STLCON.MOD_ID, "merged_mirrorborn").toString()));

    // Passive
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

    public static final RegistryObject<EntityType<Wispxen>> WISPXEN =
            ENTITY_TYPES.register("wispxen",
                    () -> EntityType.Builder.of(Wispxen::new, MobCategory.CREATURE)
                            .sized(0.3f, 0.3f).canSpawnFarFromPlayer()
                            .build(new ResourceLocation(STLCON.MOD_ID, "wispxen").toString()));

    public static final RegistryObject<EntityType<Zephxen>> ZEPHXEN =
            ENTITY_TYPES.register("zephxen",
                    () -> EntityType.Builder.of(Zephxen::new, MobCategory.CREATURE)
                            .sized(2.3f, 2.3f).canSpawnFarFromPlayer().clientTrackingRange(10)
                            .build(new ResourceLocation(STLCON.MOD_ID, "zephxen").toString()));


    // Projectiles Entities
    public static final RegistryObject<EntityType<RaygunBeam>> RAYGUN_BEAM = ENTITY_TYPES.register("raygun_beam",
            () -> EntityType.Builder.<RaygunBeam>of(RaygunBeam::new, MobCategory.MISC).sized(1.0F, 1.0F)
                    .updateInterval(10)
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "raygun_beam").toString()));

    public static final RegistryObject<EntityType<BlastcasterBeam>> BLAST_CASTER = ENTITY_TYPES.register("blastcaster_beam",
            () -> EntityType.Builder.<BlastcasterBeam>of(BlastcasterBeam::new, MobCategory.MISC).sized(1.0F, 1.0F)
                    .updateInterval(10)
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "blastcaster_beam").toString()));

    public static final RegistryObject<EntityType<StarduskBeam>> STARDUSK_BEAM = ENTITY_TYPES.register("stardusk_beam",
            () -> EntityType.Builder.<StarduskBeam>of(StarduskBeam::new, MobCategory.MISC).sized(1.0F, 1.0F)
                    .updateInterval(10).fireImmune()
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "stardusk_beam").toString()));

    public static final RegistryObject<EntityType<CelestroidBeam>> CELESTROID_BEAM = ENTITY_TYPES.register("celestroid_beam",
            () -> EntityType.Builder.<CelestroidBeam>of(CelestroidBeam::new, MobCategory.MISC).sized(1.0F, 1.0F)
                    .updateInterval(10)
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "celestroid_beam").toString()));

    public static final RegistryObject<EntityType<CelestroidBeamNP>> CELESTROID_BEAM_NP = ENTITY_TYPES.register("celestroid_beam_np",
            () -> EntityType.Builder.<CelestroidBeamNP>of(CelestroidBeamNP::new, MobCategory.MISC).sized(0.7F, 0.7F)
                    .updateInterval(10)
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "celestroid_beam_np").toString()));

    public static final RegistryObject<EntityType<MothershipBeam>> MOTHERSHIP_BEAM = ENTITY_TYPES.register("mothership_beam",
            () -> EntityType.Builder.<MothershipBeam>of(MothershipBeam::new, MobCategory.MISC).sized(3.0F, 3.0F)
                    .updateInterval(10)
                    .clientTrackingRange(9).build(new ResourceLocation(STLCON.MOD_ID, "mothership_beam").toString()));

    // Misc
    public static final RegistryObject<EntityType<Stardusk>> STARDUSK = ENTITY_TYPES.register("stardusk",
            () -> EntityType.Builder.of(Stardusk::new, MobCategory.CREATURE).fireImmune()
                    .sized(3.0f,3.0f).fireImmune().build("stardusk"));

    public static void register(IEventBus eventBus)  { ENTITY_TYPES.register(eventBus); }
}
