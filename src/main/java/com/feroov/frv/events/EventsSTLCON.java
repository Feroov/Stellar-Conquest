package com.feroov.frv.events;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.monster.Celestroid;
import com.feroov.frv.entity.monster.Mothership;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = STLCON.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventsSTLCON
{
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event)
    {
        event.put(EntitiesSTLCON.CELESTROID.get(), Celestroid.setAttributes());
        event.put(EntitiesSTLCON.MOTHERSHIP.get(), Mothership.setAttributes());
    }
}
