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
    public static final RegistryObject<SoundEvent> CELESTROID_AMBIENT = SOUND_EVENTS.register("celestroid_ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "celestroid_ambient")));

    public static final RegistryObject<SoundEvent> XERON_AMBIENT = SOUND_EVENTS.register("xeron_ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "xeron_ambient")));
    public static final RegistryObject<SoundEvent> XERON_HURT = SOUND_EVENTS.register("xeron_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "xeron_hurt")));

    public static final RegistryObject<SoundEvent> ZEPHXEN_AMBIENT = SOUND_EVENTS.register("zephxen_ambient", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "zephxen_ambient")));
    public static final RegistryObject<SoundEvent> ZEPHXEN_HURT = SOUND_EVENTS.register("zephxen_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "zephxen_hurt")));
    public static final RegistryObject<SoundEvent> ZEPHXEN_DEATH = SOUND_EVENTS.register("zephxen_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "zephxen_death")));

    public static final RegistryObject<SoundEvent> XENOSPHERE_AMBIENCE = SOUND_EVENTS.register("xenosphere_ambience", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "xenosphere_ambience")));


    public static void register(IEventBus eventBus) { SOUND_EVENTS.register(eventBus); }
}
