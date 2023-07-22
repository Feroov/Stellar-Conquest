package com.feroov.frv.block;


import com.feroov.frv.block.custom.vegetation.LumiBloomCropBlock;
import com.feroov.frv.block.custom.wood.XenosWoodType;
import com.feroov.frv.block.custom.vegetation.Blushthorn;
import com.feroov.frv.block.custom.vegetation.Xenosgrass;
import com.feroov.frv.block.custom.vegetation.XenosgrassBlock;
import com.feroov.frv.entity.BossVariant;
import com.feroov.frv.item.ItemsSTLCON;
import com.feroov.frv.world.tree.XenosTreeGrower;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import static com.feroov.frv.STLCON.MOD_ID;
import static net.minecraft.world.level.block.Blocks.*;

public class BlocksSTLCON
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    // ------------------------------------------ Teleporter Blocks ------------------------------------------
    public static final RegistryObject<Block> XENOSPHERE_TELEPORTER = registerBlock("xenosphere_portal", () ->
            new STLCONPortalBlocks(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID, "xenoflux"))));
    // ---------------------------------------------------------------------------------------------------



    // ------------------------------------------ Spawner Blocks ------------------------------------------
    public static final RegistryObject<Block> MOTHERSHIP_SPAWNER = registerBlock("mothership_boss_spawner",
            () -> new BossSpawnerBlock(BlockBehaviour.Properties.copy(STONE).strength(-1.0F, 3600000.8F)
                    .noOcclusion().noLootTable(), BossVariant.MOTHERSHIP));

    public static final RegistryObject<Block> MEKKRON_SPAWNER = registerBlock("mekkron_boss_spawner",
            () -> new BossSpawnerBlock(BlockBehaviour.Properties.copy(STONE).strength(-1.0F, 3600000.8F)
                    .noOcclusion().noLootTable(), BossVariant.MEKKRON));
    // ---------------------------------------------------------------------------------------------------



    // ------------------------------------------ General Blocks ------------------------------------------
    public static final RegistryObject<Block> XENOSGRASS_BLOCK = registerBlock("xenosgrass_block",
            () -> new XenosgrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).strength(0.6F).sound(SoundType.GRASS)));

    public static final RegistryObject<Block> XENOSDIRT = registerBlock("xenosdirt",
            () -> new Block(BlockBehaviour.Properties.copy(DIRT).strength(0.5F).sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> XENOSGRASS = registerBlock("xenosgrass",
            () -> new Xenosgrass(BlockBehaviour.Properties.copy(GRASS)
                    .instabreak().sound(SoundType.GRASS).noCollission().noOcclusion()));

    public static final RegistryObject<Block> XENOSTONE = registerBlock("xenostone",
            () -> new Block(BlockBehaviour.Properties.copy(STONE).
                    requiresCorrectToolForDrops().strength(2.5F, 7.0F)));

    public static final RegistryObject<Block> XENOCOBBLESTONE = registerBlock("xenocobblestone",
            () -> new Block(BlockBehaviour.Properties.copy(STONE).
                    requiresCorrectToolForDrops().strength(3.1F, 7.0F)));

    public static final RegistryObject<Block> BLUSHTHORN = registerBlock("blushthorn",
            () -> new Blushthorn(BlockBehaviour.Properties.copy(GRASS)
                    .instabreak().sound(SoundType.GRASS).noCollission().noOcclusion().lightLevel((light) -> 10)) {@Override public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> components, TooltipFlag flag) {components.add(Component.translatable("Heart soothing plant.").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.ITALIC));super.appendHoverText(stack, level, components, flag);}});

    public static final RegistryObject<Block> XENOFLUX = registerBlock("xenoflux",
            () -> new Block(BlockBehaviour.Properties.copy(STONE).
                    requiresCorrectToolForDrops().strength(4.0F, 7.0F)) { @Override public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> components, TooltipFlag flag) { components.add(Component.translatable("You feel the pulsating energy.").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC)); super.appendHoverText(stack, level, components, flag); }});

    public static final RegistryObject<Block> GLOWXEN = registerBlock("glowxen",
            () -> new Block(BlockBehaviour.Properties.copy(GLASS).strength(0.3F).sound(SoundType.GLASS)
                    .lightLevel((p_50874_) -> { return 15; })));

    public static final RegistryObject<Block> LUMIBLOOM_CROP = BLOCKS.register("lumibloom_crop",
            () -> new LumiBloomCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).lightLevel((light) -> 10)));

    public static final RegistryObject<Block> CELOSTONE = registerBlock("celostone",
            () -> new Block(BlockBehaviour.Properties.copy(DEEPSLATE).
                    requiresCorrectToolForDrops().strength(22.5F, 7.0F).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> CELOSTONE_STAIRS = registerBlock("celostone_stairs",
            () -> new StairBlock(() -> CELOSTONE.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(NETHER_BRICK_STAIRS).strength(4f).sound(SoundType.DEEPSLATE_BRICKS)));

    public static final RegistryObject<Block> CELOSTONE_SLAB = registerBlock("celostone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(NETHER_BRICK_SLAB).sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(4f).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> CELOSTONE_PRESSURE_PLATE = registerBlock("celostone_pressure_plate",() ->
            new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(OAK_PRESSURE_PLATE)
                    .noCollission().strength(1.5F).sound(SoundType.DEEPSLATE), BlockSetType.OAK));

    public static final RegistryObject<Block> CELOSTONE_DOOR = registerBlock("celostone_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(IRON_DOOR).requiresCorrectToolForDrops()
                    .strength(34.0F).noOcclusion(), BlockSetType.IRON));
    // ---------------------------------------------------------------------------------------------------



    // ------------------------------------------ Ore Blocks ------------------------------------------
    public static final RegistryObject<Block> USKIUM_ORE = registerBlock("uskium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(STONE)
                    .strength(5f).requiresCorrectToolForDrops(), UniformInt.of(2, 6)));

    public static final RegistryObject<Block> XENITE_ORE = registerBlock("xenite_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(STONE)
                    .strength(9f).requiresCorrectToolForDrops(), UniformInt.of(2, 6)));

    public static final RegistryObject<Block> ASTRALITE_ORE = registerBlock("astralite_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(STONE)
                    .strength(13f).requiresCorrectToolForDrops(), UniformInt.of(2, 6)));
    // ----------------------------------------------------------------------------------------------------



    // ------------------------------------------ Wood Types ------------------------------------------
    public static final RegistryObject<Block> XENOS_LOG = registerBlock("xenos_log",
            () -> new XenosWoodType(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)
                    .strength(2f)));

    public static final RegistryObject<Block> XENOS_WOOD = registerBlock("xenos_wood",
            () -> new XenosWoodType(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)
                    .strength(2f)));

    public static final RegistryObject<Block> STRIPPED_XENOS_LOG = registerBlock("stripped_xenos_log",
            () -> new XenosWoodType(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)
                    .strength(2f)));

    public static final RegistryObject<Block> STRIPPED_XENOS_WOOD = registerBlock("stripped_xenos_wood",
            () -> new XenosWoodType(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)
                    .strength(2f)));

    public static final RegistryObject<Block> XENOS_PLANKS = registerBlock("xenos_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
                    .strength(2f)) {@Override public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {return false;}});

    public static final RegistryObject<Block> XENOS_STAIRS = registerBlock("xenos_stairs",
            () -> new StairBlock(() -> XENOS_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(OAK_STAIRS).strength(2f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> XENOS_SLAB = registerBlock("xenos_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(OAK_SLAB).sound(SoundType.WOOD)
                    .strength(2f)));

    public static final RegistryObject<Block> XENOS_FENCE = registerBlock("xenos_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(OAK_FENCE).sound(SoundType.WOOD)
                    .strength(2f)));

    public static final RegistryObject<Block> XENOS_FENCE_GATE = registerBlock("xenos_fence_gate",() ->
            new FenceGateBlock(BlockBehaviour.Properties.copy(OAK_FENCE_GATE).strength(2.0F, 3.0F), WoodType.OAK));

    public static final RegistryObject<Block> XENOS_BUTTON = registerBlock("xenos_button",() -> woodenButton(BlockSetType.OAK));

    public static final RegistryObject<Block> XENOS_PRESSURE_PLATE = registerBlock("xenos_pressure_plate",() ->
            new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(OAK_PRESSURE_PLATE)
                    .noCollission().strength(0.5F), BlockSetType.OAK));

    public static final RegistryObject<Block> XENOS_DOOR = registerBlock("xenos_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(OAK_DOOR)
                    .strength(3.0F).noOcclusion(), BlockSetType.DARK_OAK));

    public static final RegistryObject<Block> XENOS_TRAPDOOR = registerBlock("xenos_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(OAK_TRAPDOOR)
                    .strength(3.0F).noOcclusion(), BlockSetType.OAK));

    public static final RegistryObject<Block> XENOS_LEAVES = registerBlock("xenos_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)) {@Override public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {return false;}});

    public static final RegistryObject<SaplingBlock> XENOS_SAPLING = registerBlock("xenos_sapling",
            () -> new SaplingBlock(new XenosTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));
    // ---------------------------------------------------------------------------------------------------


    // ------------------------------------------ Potted Blocks ------------------------------------------
    public static final RegistryObject<Block> POTTED_XENOS_SAPLING = BLOCKS.register("potted_xenos",
            () -> new FlowerPotBlock(() -> ((FlowerPotBlock) Blocks.FLOWER_POT), BlocksSTLCON.XENOS_SAPLING,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_ORANGE_TULIP)));
    // ---------------------------------------------------------------------------------------------------



    private static ButtonBlock woodenButton(BlockSetType p_273357_) {return new ButtonBlock(BlockBehaviour.Properties.copy(OAK_BUTTON).noCollission().strength(0.5F), p_273357_, 30, true);}
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) { RegistryObject<T> toReturn = BLOCKS.register(name, block); registerBlockItem(name, toReturn); return toReturn; }
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)  { return ItemsSTLCON.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties())); }
    public static void register(IEventBus eventBus) { BLOCKS.register(eventBus); }
}
