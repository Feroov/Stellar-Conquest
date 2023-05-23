package com.feroov.frv;

import com.feroov.frv.block.BlocksSTLCON;
import com.feroov.frv.entity.BlockEntitiesSTLCON;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.events.ModParticles;
import com.feroov.frv.item.ItemsSTLCON;
import com.feroov.frv.item.RangedItems;
import com.feroov.frv.sound.SoundEventsSTLCON;
import com.feroov.frv.world.dimension.DimensionsSTLCON;
import com.feroov.frv.world.dimension.POIRegistry;
import com.feroov.frv.world.feature.FeatureModifiers;
import com.feroov.frv.world.placement.PlacementRegistry;
import com.feroov.frv.world.structure.StructuresSTLCON;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
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
        BlocksSTLCON.register(eventBus);
        EntitiesSTLCON.register(eventBus);
        ModParticles.register(eventBus);
        ItemsSTLCON.register(eventBus);
        DimensionsSTLCON.register();
        StructuresSTLCON.DEFERRED_REGISTRY_STRUCTURE.register(eventBus);
        POIRegistry.POI.register(eventBus);
        BlockEntitiesSTLCON.BLOCK_ENTITIES.register(eventBus);
        FeatureModifiers.FOLIAGE_PLACERS.register(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        PlacementRegistry.init();

        event.enqueueWork(() ->
        {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(BlocksSTLCON.XENOS_SAPLING.getId(), BlocksSTLCON.POTTED_XENOS_SAPLING);
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        RangedItems.addRanged();
    }
}
