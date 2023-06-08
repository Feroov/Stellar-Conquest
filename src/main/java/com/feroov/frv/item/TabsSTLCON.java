package com.feroov.frv.item;

import com.feroov.frv.STLCON;
import com.feroov.frv.block.BlocksSTLCON;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class TabsSTLCON
{
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, STLCON.MOD_ID);

    public static final RegistryObject<CreativeModeTab> STLCON_GEAR = TABS.register("stlcon_gear", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativemodetab.stlcon_gear"))
            .icon(() -> new ItemStack(ItemsSTLCON.COSMIC_RAY_GUN.get()))
            .displayItems((enabledFeatures, event) -> {
                event.accept(ItemsSTLCON.ADMIN_SWORD.get());
                event.accept(ItemsSTLCON.COSMIC_RAY_GUN.get());
                event.accept(ItemsSTLCON.XENOSTONE_SWORD.get());
                event.accept(ItemsSTLCON.XENOSTONE_PICKAXE.get());
                event.accept(ItemsSTLCON.XENOSTONE_AXE.get());
                event.accept(ItemsSTLCON.XENOSTONE_SHOVEL.get());
                event.accept(ItemsSTLCON.XENOSTONE_HOE.get());
            }).build());

    public static final RegistryObject<CreativeModeTab> STLCON_ITEMS = TABS.register("stlcon_items", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativemodetab.stlcon_items"))
            .icon(() -> new ItemStack(ItemsSTLCON.ASTRALITE_INGOT.get()))
            .displayItems((enabledFeatures, event) -> {
                event.accept(ItemsSTLCON.USKIUM.get());
                event.accept(ItemsSTLCON.XENITE_INGOT.get());
                event.accept(ItemsSTLCON.ASTRALITE_INGOT.get());
                event.accept(ItemsSTLCON.XENOS_EYE.get());
                event.accept(ItemsSTLCON.BLUSHTHORN_NECTAR_BOTTLE.get());
                event.accept(ItemsSTLCON.LUMIBLOOM_SEEDS.get());
                event.accept(ItemsSTLCON.LUMIBLOOM.get());
                event.accept(ItemsSTLCON.WISPXENDUST.get());
                event.accept(ItemsSTLCON.XERON_SPAWN_EGG.get());
                event.accept(ItemsSTLCON.XERON_GUARD_SPAWN_EGG.get());
                event.accept(ItemsSTLCON.WISPXEN_SPAWN_EGG.get());
                event.accept(ItemsSTLCON.CELESTROID_SPAWN_EGG.get());
                event.accept(ItemsSTLCON.MOTHERSHIP_SPAWN_EGG.get());
            }).build());

    public static final RegistryObject<CreativeModeTab> STLCON_BLOCKS = TABS.register("stlcon_blocks", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativemodetab.stlcon_blocks"))
            .icon(() -> new ItemStack(BlocksSTLCON.XENOFLUX.get()))
            .displayItems((enabledFeatures, event) -> {
                event.accept(BlocksSTLCON.BLUSHTHORN.get());
                event.accept(BlocksSTLCON.XENOSGRASS.get());
                event.accept(BlocksSTLCON.XENOS_SAPLING.get());
                event.accept(BlocksSTLCON.XENOSGRASS_BLOCK.get());
                event.accept(BlocksSTLCON.XENOSDIRT.get());
                event.accept(BlocksSTLCON.XENOS_LOG.get());
                event.accept(BlocksSTLCON.XENOS_WOOD.get());
                event.accept(BlocksSTLCON.XENOS_PLANKS.get());
                event.accept(BlocksSTLCON.STRIPPED_XENOS_LOG.get());
                event.accept(BlocksSTLCON.STRIPPED_XENOS_WOOD.get());
                event.accept(BlocksSTLCON.XENOS_STAIRS.get());
                event.accept(BlocksSTLCON.XENOS_SLAB.get());
                event.accept(BlocksSTLCON.XENOS_FENCE.get());
                event.accept(BlocksSTLCON.XENOS_FENCE_GATE.get());
                event.accept(BlocksSTLCON.XENOS_DOOR.get());
                event.accept(BlocksSTLCON.XENOS_TRAPDOOR.get());
                event.accept(BlocksSTLCON.XENOS_BUTTON.get());
                event.accept(BlocksSTLCON.XENOS_PRESSURE_PLATE.get());
                event.accept(BlocksSTLCON.XENOS_LEAVES.get());
                event.accept(BlocksSTLCON.XENOFLUX.get());
                event.accept(BlocksSTLCON.GLOWXEN.get());
                event.accept(BlocksSTLCON.XENOSTONE.get());
                event.accept(BlocksSTLCON.XENOCOBBLESTONE.get());
                event.accept(BlocksSTLCON.USKIUM_ORE.get());
                event.accept(BlocksSTLCON.XENITE_ORE.get());
                event.accept(BlocksSTLCON.ASTRALITE_ORE.get());
            }).build());
}
