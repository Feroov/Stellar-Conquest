package com.feroov.frv;


import com.feroov.frv.block.BlocksSTLCON;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.item.ItemsSTLCON;
import com.feroov.frv.item.RangedItems;
import com.feroov.frv.sound.SoundEventsSTLCON;
import com.feroov.frv.world.dimension.DimensionsSTLCON;
import com.feroov.frv.world.feature.FeatureModifiers;
import com.feroov.frv.world.placement.PlacementRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
        FeatureModifiers.FOLIAGE_PLACERS.register(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        PlacementRegistry.init();
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        RangedItems.addRanged();
    }
}
