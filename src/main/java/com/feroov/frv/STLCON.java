package com.feroov.frv;


import com.feroov.frv.block.BlocksSTLCON;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.item.ItemsSTLCON;
import com.feroov.frv.item.RangedItems;
import com.feroov.frv.sound.SoundEventsSTLCON;
import com.feroov.frv.world.dimension.DimensionsSTLCON;
import net.minecraftforge.common.MinecraftForge;
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
        BlocksSTLCON.register(eventBus);
        DimensionsSTLCON.register();

        eventBus.addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        RangedItems.addRanged();
    }
}
