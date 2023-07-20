package com.feroov.frv.sound;

import com.feroov.frv.STLCON;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundEventsSTLCON
{
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, STLCON.MOD_ID);

    public static final RegistryObject<SoundEvent> RAYGUN_SHOOT = SOUND_EVENTS.register("raygun_shoot", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "raygun_shoot")));
    public static final RegistryObject<SoundEvent> BLASTCASTER_SHOOT = SOUND_EVENTS.register("blastcaster_shoot", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "blastcaster_shoot")));
    public static final RegistryObject<SoundEvent> BLASTCASTER_CHARGE = SOUND_EVENTS.register("blastcaster_charge", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "blastcaster_charge")));
    public static final RegistryObject<SoundEvent> BLASTCASTER_CHARGE_TICK = SOUND_EVENTS.register("blastcaster_charge_tick", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "blastcaster_charge_tick")));
    public static final RegistryObject<SoundEvent> CELESTROID_AMBIENT = SOUND_EVENTS.register("celestroid_ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "celestroid_ambient")));
    public static final RegistryObject<SoundEvent> STARDUSK_AMBIENT = SOUND_EVENTS.register("stardusk_ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "stardusk_ambient")));
    public static final RegistryObject<SoundEvent> ENGINE_START = SOUND_EVENTS.register("engine_start", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "engine_start")));
    public static final RegistryObject<SoundEvent> ENGINE_OFF = SOUND_EVENTS.register("engine_off", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "engine_off")));

    public static final RegistryObject<SoundEvent> XERON_AMBIENT = SOUND_EVENTS.register("xeron_ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "xeron_ambient")));
    public static final RegistryObject<SoundEvent> XERON_HURT = SOUND_EVENTS.register("xeron_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "xeron_hurt")));

    public static final RegistryObject<SoundEvent> ZEPHXEN_AMBIENT = SOUND_EVENTS.register("zephxen_ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "zephxen_ambient")));
    public static final RegistryObject<SoundEvent> ZEPHXEN_HURT = SOUND_EVENTS.register("zephxen_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "zephxen_hurt")));
    public static final RegistryObject<SoundEvent> ZEPHXEN_DEATH = SOUND_EVENTS.register("zephxen_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "zephxen_death")));

    public static final RegistryObject<SoundEvent> XENOSPHERE_AMBIENCE = SOUND_EVENTS.register("xenosphere_ambience", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "xenosphere_ambience")));
    public static final RegistryObject<SoundEvent> XENOSPHERE_FOREST_AMBIENCE = SOUND_EVENTS.register("xenosphere_forest_ambience", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "xenosphere_forest_ambience")));
    public static final RegistryObject<SoundEvent> MEKKRON_THEME = SOUND_EVENTS.register("mekkron_theme", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "mekkron_theme")));
    public static final RegistryObject<SoundEvent> SILENT = SOUND_EVENTS.register("silent", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "silent")));


    public static void register(IEventBus eventBus) { SOUND_EVENTS.register(eventBus); }
}
