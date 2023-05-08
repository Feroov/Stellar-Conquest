package com.feroov.frv.item;

import com.feroov.frv.STLCON;
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
    public static CreativeModeTab STLCON_TAB;

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event) {
        STLCON_TAB = event.registerCreativeModeTab(new ResourceLocation(STLCON.MOD_ID, "stlcon_tab"),
                builder -> builder.icon(() -> new ItemStack(ItemsSTLCON.COSMIC_RAY_GUN.get()))
                        .title(Component.translatable("creativemodetab.stlcon_tab")));
    }

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == TabsSTLCON.STLCON_TAB)
        {
            event.accept(ItemsSTLCON.ADMIN_SWORD);
            event.accept(ItemsSTLCON.COSMIC_RAY_GUN);
            event.accept(ItemsSTLCON.CELESTROID_SPAWN_EGG);
        }
    }
}
