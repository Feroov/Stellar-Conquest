package com.feroov.frv.item;

import com.feroov.frv.STLCON;
import com.feroov.frv.block.BlocksSTLCON;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = STLCON.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TabsSTLCON
{
    public static CreativeModeTab STLCON_ITEMS;
    public static CreativeModeTab STLCON_BLOCKS;
    public static CreativeModeTab STLCON_GEAR;

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event)
    {
        STLCON_GEAR = event.registerCreativeModeTab(new ResourceLocation(STLCON.MOD_ID, "stlcon_gear"),
                builder -> builder.icon(() -> new ItemStack(ItemsSTLCON.COSMIC_RAY_GUN.get()))
                        .title(Component.translatable("creativemodetab.stlcon_gear")));

        STLCON_ITEMS = event.registerCreativeModeTab(new ResourceLocation(STLCON.MOD_ID, "stlcon_items"),
                builder -> builder.icon(() -> new ItemStack(ItemsSTLCON.ASTRALITE_INGOT.get()))
                        .title(Component.translatable("creativemodetab.stlcon_items")));

        STLCON_BLOCKS = event.registerCreativeModeTab(new ResourceLocation(STLCON.MOD_ID, "stlcon_blocks"),
                builder -> builder.icon(() -> new ItemStack(BlocksSTLCON.XENOFLUX.get()))
                        .title(Component.translatable("creativemodetab.stlcon_blocks")));
    }

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.BuildContents event)
    {
        if(event.getTab() == TabsSTLCON.STLCON_BLOCKS)
        {
            event.accept(BlocksSTLCON.BLUSHTHORN);
            event.accept(BlocksSTLCON.XENOSGRASS);
            event.accept(BlocksSTLCON.XENOS_SAPLING);
            event.accept(BlocksSTLCON.XENOSGRASS_BLOCK);
            event.accept(BlocksSTLCON.XENOSDIRT);
            event.accept(BlocksSTLCON.XENOS_LOG);
            event.accept(BlocksSTLCON.XENOS_WOOD);
            event.accept(BlocksSTLCON.XENOS_PLANKS);
            event.accept(BlocksSTLCON.STRIPPED_XENOS_LOG);
            event.accept(BlocksSTLCON.STRIPPED_XENOS_WOOD);
            event.accept(BlocksSTLCON.XENOS_STAIRS);
            event.accept(BlocksSTLCON.XENOS_SLAB);
            event.accept(BlocksSTLCON.XENOS_FENCE);
            event.accept(BlocksSTLCON.XENOS_FENCE_GATE);
            event.accept(BlocksSTLCON.XENOS_DOOR);
            event.accept(BlocksSTLCON.XENOS_TRAPDOOR);
            event.accept(BlocksSTLCON.XENOS_BUTTON);
            event.accept(BlocksSTLCON.XENOS_PRESSURE_PLATE);
            event.accept(BlocksSTLCON.XENOS_LEAVES);
            event.accept(BlocksSTLCON.XENOFLUX);
            event.accept(BlocksSTLCON.GLOWXEN);
            event.accept(BlocksSTLCON.XENOSTONE);
            event.accept(BlocksSTLCON.XENOCOBBLESTONE);
            event.accept(BlocksSTLCON.USKIUM_ORE);
            event.accept(BlocksSTLCON.XENITE_ORE);
            event.accept(BlocksSTLCON.ASTRALITE_ORE);
        }

        if(event.getTab() == TabsSTLCON.STLCON_ITEMS)
        {

            event.accept(ItemsSTLCON.USKIUM);
            event.accept(ItemsSTLCON.XENITE_INGOT);
            event.accept(ItemsSTLCON.ASTRALITE_INGOT);
            event.accept(ItemsSTLCON.XENOS_EYE);
            event.accept(ItemsSTLCON.BLUSHTHORN_NECTAR_BOTTLE);
            event.accept(ItemsSTLCON.XERON_SPAWN_EGG);
            event.accept(ItemsSTLCON.XERON_GUARD_SPAWN_EGG);
            event.accept(ItemsSTLCON.CELESTROID_SPAWN_EGG);
            event.accept(ItemsSTLCON.MOTHERSHIP_SPAWN_EGG);
        }

        if(event.getTab() == TabsSTLCON.STLCON_GEAR)
        {
            event.accept(ItemsSTLCON.ADMIN_SWORD);
            event.accept(ItemsSTLCON.COSMIC_RAY_GUN);
            event.accept(ItemsSTLCON.XENOSTONE_SWORD);
            event.accept(ItemsSTLCON.XENOSTONE_PICKAXE);
            event.accept(ItemsSTLCON.XENOSTONE_AXE);
            event.accept(ItemsSTLCON.XENOSTONE_SHOVEL);
            event.accept(ItemsSTLCON.XENOSTONE_HOE);
        }
    }
}
