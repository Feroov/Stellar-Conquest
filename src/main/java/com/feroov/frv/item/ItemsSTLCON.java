package com.feroov.frv.item;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.item.custom.CosmicRayGun;
import com.feroov.frv.item.custom.XenosEye;
import com.feroov.frv.item.custom.consumeable.BushthornNectar;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static net.minecraft.world.item.Items.GLASS_BOTTLE;

public class ItemsSTLCON
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, STLCON.MOD_ID);

    public static final RegistryObject<Item> ADMIN_SWORD = ITEMS.register("admin_sword",
            () -> new SwordItem(TiersSTLCON.ADMIN, 0, 9996f,
                    new Item.Properties())
    {
        @Override
        public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
        {
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
            tooltip.add(Component.translatable("Banish everything off the screen").withStyle(ChatFormatting.RED));
            tooltip.add(Component.translatable(" "));
            tooltip.add(Component.translatable("Creative Mode Exclusive!").withStyle(ChatFormatting.UNDERLINE));
        }
    });

    public static final RegistryObject<Item> XENOS_EYE = ITEMS.register("xenos_eye", () -> new XenosEye((new Item.Properties())));

    public static final RegistryObject<Item> BLUSHTHORN_NECTAR_BOTTLE = ITEMS.register("blushthorn_nectar",
            () -> new BushthornNectar((new Item.Properties()).craftRemainder(GLASS_BOTTLE).food(ConsumablesSTLCON.BLUSHTHORN_NECTAR_BOTTLE).stacksTo(16)));

    public static final RegistryObject<CosmicRayGun> COSMIC_RAY_GUN = ITEMS.register("raygun", CosmicRayGun::new);

    public static final RegistryObject<Item> CELESTROID_SPAWN_EGG = ITEMS.register("celestroid_spawn_egg",
            () -> new ForgeSpawnEggItem(EntitiesSTLCON.CELESTROID, 0x029AF7, 0x5E5C5D, new Item.Properties()));

    public static final RegistryObject<Item> MOTHERSHIP_SPAWN_EGG = ITEMS.register("mothership_spawn_egg",
            () -> new ForgeSpawnEggItem(EntitiesSTLCON.MOTHERSHIP,  0x5E5C5D, 0x029AF7, new Item.Properties()));

    public static final RegistryObject<Item> XERON_SPAWN_EGG = ITEMS.register("xeron_spawn_egg",
            () -> new ForgeSpawnEggItem(EntitiesSTLCON.XERON,  0x029AF7, 0X006AFF, new Item.Properties()));





    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
