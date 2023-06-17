package com.feroov.frv.item;

import com.feroov.frv.STLCON;
import com.feroov.frv.block.BlocksSTLCON;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.item.custom.CosmicRayGun;
import com.feroov.frv.item.custom.XenosEye;
import com.feroov.frv.item.custom.misc.BushthornNectar;
import com.feroov.frv.item.custom.tools.PickaxeItemSTLCON;
import com.feroov.frv.item.custom.tools.SwordItemSTLCON;
import com.feroov.frv.item.tiers.TiersSTLCON;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.world.item.Items.GLASS_BOTTLE;

public class ItemsSTLCON
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, STLCON.MOD_ID);


    // ------------------------------- Unique & Creative only -----------------------------------------
    public static final RegistryObject<Item> ADMIN_SWORD = ITEMS.register("admin_sword", () -> new SwordItem(TiersSTLCON.ADMIN, 0, 9996f, new Item.Properties())  { @Override  public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)  { super.appendHoverText(stack, worldIn, tooltip, flagIn); tooltip.add(Component.translatable("Banish everything off the screen").withStyle(ChatFormatting.RED)); tooltip.add(Component.translatable(" ")); tooltip.add(Component.translatable("Creative Mode Exclusive!").withStyle(ChatFormatting.UNDERLINE)); }});
    // ------------------------------------------------------------------------------------------------



    // ------------------------------------ Special Weapons ------------------------------------------
    public static final RegistryObject<CosmicRayGun> COSMIC_RAY_GUN = ITEMS.register("raygun", CosmicRayGun::new);
    // -----------------------------------------------------------------------------------------------



    // ----------------------------------------- Tools ------------------------------------------------
    public static final RegistryObject<Item> XENOSTONE_SWORD = ITEMS.register("xenostone_sword", () -> new SwordItemSTLCON(TiersSTLCON.XENOSTONE, 3.5F, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> XENOSTONE_PICKAXE = ITEMS.register("xenostone_pickaxe", () -> new PickaxeItemSTLCON(TiersSTLCON.XENOSTONE, 1.4f, -2.8f, new Item.Properties()));
    public static final RegistryObject<Item> XENOSTONE_AXE = ITEMS.register("xenostone_axe", () -> new AxeItem(TiersSTLCON.XENOSTONE, 7.0F, -3.2f, new Item.Properties()));
    public static final RegistryObject<Item> XENOSTONE_SHOVEL = ITEMS.register("xenostone_shovel", () -> new ShovelItem(TiersSTLCON.XENOSTONE, 2.0F, -3.0f, new Item.Properties()));
    public static final RegistryObject<Item> XENOSTONE_HOE = ITEMS.register("xenostone_hoe", () -> new HoeItem(TiersSTLCON.XENOSTONE, (int) -1, -2.0F, new Item.Properties()));
    // ------------------------------------------------------------------------------------------------


    
    // ----------------------------------------- Armor ------------------------------------------------
//    public static final RegistryObject<Item> XENOSTONE_HELMET = ITEMS.register("xenostone_helmet", () -> new ArmorItem(ModArmorMaterial.XENOSTONE, EquipmentSlot.HEAD, new Item.Properties()));
//    public static final RegistryObject<Item> XENOSTONE_CHESTPLATE = ITEMS.register("xenostone_chestplate", () -> new ArmorItem(ModArmorMaterial.XENOSTONE, EquipmentSlot.CHEST, new Item.Properties()));
//    public static final RegistryObject<Item> XENOSTONE_LEGGINGS = ITEMS.register("xenostone_leggings", () -> new ArmorItem(ModArmorMaterial.XENOSTONE, EquipmentSlot.LEGS, new Item.Properties()));
//    public static final RegistryObject<Item> XENOSTONE_BOOTS = ITEMS.register("xenostone_boots", () -> new ArmorItem(ModArmorMaterial.XENOSTONE, EquipmentSlot.FEET, new Item.Properties()));
    // ------------------------------------------------------------------------------------------------
    
    
    
    // ------------------------------------------ Materials ------------------------------------------
    public static final RegistryObject<Item> XENOS_EYE = ITEMS.register("xenos_eye", () -> new XenosEye((new Item.Properties())));
    public static final RegistryObject<Item> RAYGUN_HANDLE = ITEMS.register("raygun_handle", () -> new Item((new Item.Properties().stacksTo(1))));
    public static final RegistryObject<Item> RAYGUN_FRAME = ITEMS.register("raygun_frame", () -> new Item((new Item.Properties().stacksTo(1))));
    public static final RegistryObject<Item> RAYGUN_ENERGY = ITEMS.register("raygun_energy", () -> new Item((new Item.Properties().stacksTo(1))));
    public static final RegistryObject<Item> XENITE_INGOT = ITEMS.register("xenite_ingot", () -> new Item(new Item.Properties()) { @Override  public boolean isFoil(ItemStack stack) { return true; } });
    public static final RegistryObject<Item> ASTRALITE_INGOT = ITEMS.register("astralite_ingot", () -> new Item(new Item.Properties()) { @Override  public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)  { super.appendHoverText(stack, worldIn, tooltip, flagIn); tooltip.add(Component.translatable("Glimmering Xenospheric").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC)); tooltip.add(Component.translatable("metal unyielding,").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC)); tooltip.add(Component.translatable("azure brilliance with").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC)); tooltip.add(Component.translatable("unparalleled strength.").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));}});
    public static final RegistryObject<Item> USKIUM = ITEMS.register("uskium", () -> new Item(new Item.Properties())  { @Override  public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) { return 2000; }});
    // -----------------------------------------------------------------------------------------------
    
    
    
    // ------------------------------------- Consumables & Misc ---------------------------------------
    public static final RegistryObject<Item> BLUSHTHORN_NECTAR_BOTTLE = ITEMS.register("blushthorn_nectar",
            () -> new BushthornNectar((new Item.Properties()).craftRemainder(GLASS_BOTTLE).food(ConsumablesSTLCON.BLUSHTHORN_NECTAR_BOTTLE).stacksTo(16)));

    public static final RegistryObject<Item> LUMIBLOOM_SEEDS = ITEMS.register("lumibloom_seeds",
            () -> new ItemNameBlockItem(BlocksSTLCON.LUMIBLOOM_CROP.get(), new Item.Properties()));

    public static final RegistryObject<Item> LUMIBLOOM = ITEMS.register("lumibloom",
            () -> new Item(new Item.Properties().food(ConsumablesSTLCON.LUMIBLOOM)));

    public static final RegistryObject<Item> WISPXENDUST = ITEMS.register("wispxen_dust",
            () -> new Item(new Item.Properties()) { @Override public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) { super.appendHoverText(stack, worldIn, tooltip, flagIn); tooltip.add(Component.translatable("Wispxen's growth").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC)); tooltip.add(Component.translatable("unleashes potent dust,").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC)); tooltip.add(Component.translatable("essential for crafting power.").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC)); }});
    // ------------------------------------------------------------------------------------------------



    // ------------------------------------------ Spawn Eggs-------------------------------------------
    public static final RegistryObject<Item> CELESTROID_SPAWN_EGG = ITEMS.register("celestroid_spawn_egg",
            () -> new ForgeSpawnEggItem(EntitiesSTLCON.CELESTROID,  0xFFFFFF, 0x004977, new Item.Properties()));

    public static final RegistryObject<Item> MOTHERSHIP_SPAWN_EGG = ITEMS.register("mothership_spawn_egg",
            () -> new ForgeSpawnEggItem(EntitiesSTLCON.MOTHERSHIP, 0x454545, 0x004977, new Item.Properties()));

    public static final RegistryObject<Item> XERON_SPAWN_EGG = ITEMS.register("xeron_spawn_egg",
            () -> new ForgeSpawnEggItem(EntitiesSTLCON.XERON, 0x029AF7, 0X006AFF, new Item.Properties()));

    public static final RegistryObject<Item> XERON_GUARD_SPAWN_EGG = ITEMS.register("xeron_guard_spawn_egg",
            () -> new ForgeSpawnEggItem(EntitiesSTLCON.XERON_GUARD, 0x029AF7, 0XF1C232, new Item.Properties()));

    public static final RegistryObject<Item> WISPXEN_SPAWN_EGG = ITEMS.register("wispxen_spawn_egg",
            () -> new ForgeSpawnEggItem(EntitiesSTLCON.WISPXEN, 0x02F7ED, 0X9CF7F4, new Item.Properties()));

    public static final RegistryObject<Item> ZEPHXEN_SPAWN_EGG = ITEMS.register("zephxen_spawn_egg",
            () -> new ForgeSpawnEggItem(EntitiesSTLCON.ZEPHXEN, 0x0031AF, 0XFFFFFF, new Item.Properties()));
    // ------------------------------------------------------------------------------------------------




    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
