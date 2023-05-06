package com.feroov.frv;


import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.item.ItemsSTLCON;
import com.feroov.frv.item.TabsSTLCON;
import com.feroov.frv.item.RangedItems;
import com.feroov.frv.sound.SoundEventsSTLCON;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(STLCON.MOD_ID)
public class STLCON
{

    public static final String MOD_ID = "frv";

    public STLCON()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        SoundEventsSTLCON.register(eventBus);
        EntitiesSTLCON.register(eventBus);
        ItemsSTLCON.register(eventBus);

        eventBus.addListener(this::addCreative);
        eventBus.addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        RangedItems.addRanged();
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event)
    {
        if(event.getTab() == TabsSTLCON.STLCON_TAB)
        {
            event.accept(ItemsSTLCON.ADMIN_SWORD);
            event.accept(ItemsSTLCON.COSMIC_RAY_GUN);
            event.accept(ItemsSTLCON.CELESTROID_SPAWN_EGG);
        }
    }
}
