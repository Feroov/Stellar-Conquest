package com.feroov.frv;


import com.feroov.frv.item.ItemsSTLCON;
import com.feroov.frv.item.TabsSTLCON;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(STLCON.MOD_ID)
public class STLCON
{

    public static final String MOD_ID = "frv";

    public STLCON()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemsSTLCON.register(eventBus);

        eventBus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event)
    {
        if(event.getTab() == TabsSTLCON.STLCON_TAB)
        {
            event.accept(ItemsSTLCON.ADMIN_SWORD);
        }
    }
}
