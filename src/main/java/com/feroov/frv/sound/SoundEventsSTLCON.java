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

    public static final RegistryObject<SoundEvent> RATGUN_SHOOT = SOUND_EVENTS.register("raygun_shoot", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(STLCON.MOD_ID, "raygun_shoot")));

    public static void register(IEventBus eventBus) { SOUND_EVENTS.register(eventBus); }
}
