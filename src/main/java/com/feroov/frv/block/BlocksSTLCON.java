package com.feroov.frv.block;

import com.feroov.frv.block.custom.XenosWoodType;
import com.feroov.frv.block.custom.vegetation.Blushthorn;
import com.feroov.frv.block.custom.vegetation.Xenosgrass;
import com.feroov.frv.block.custom.XenosgrassBlock;
import com.feroov.frv.item.ItemsSTLCON;
import com.feroov.frv.world.tree.XenosTreeGrower;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import static com.feroov.frv.STLCON.MOD_ID;

public class BlocksSTLCON
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    // ------------------------------------------ Teleporter Blocks ------------------------------------------
    public static final RegistryObject<Block> XENOSPHERE_TELEPORTER = registerBlock("xenosphere_portal", () ->
            new STLCONPortalBlocks(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID, "xenosdirt"))));
    // ---------------------------------------------------------------------------------------------------


    // ------------------------------------------ General Blocks ------------------------------------------
    public static final RegistryObject<Block> XENOSGRASS_BLOCK = registerBlock("xenosgrass_block",
            () -> new XenosgrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).strength(0.6F).sound(SoundType.GRASS)));

    public static final RegistryObject<Block> XENOSDIRT = registerBlock("xenosdirt",
            () -> new Block(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(0.5F).sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> XENOSGRASS = registerBlock("xenosgrass",
            () -> new Xenosgrass(BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT)
                    .instabreak().sound(SoundType.GRASS).noCollission().noOcclusion()));

    public static final RegistryObject<Block> XENOSTONE = registerBlock("xenostone",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).
                    requiresCorrectToolForDrops().strength(2.5F, 7.0F)));

    public static final RegistryObject<Block> XENOCOBBLESTONE = registerBlock("xenocobblestone",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).
                    requiresCorrectToolForDrops().strength(3.1F, 7.0F)));

    public static final RegistryObject<Block> BLUSHTHORN = registerBlock("blushthorn",
            () -> new Blushthorn(BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT)
                    .instabreak().sound(SoundType.GRASS).noCollission().noOcclusion().lightLevel((light) -> 10))
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> components, TooltipFlag flag)
                {
                    components.add(Component.translatable("Pain soothing plant")
                            .withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
                    super.appendHoverText(stack, level, components, flag);
                }});

    public static final RegistryObject<Block> XENOFLUX = registerBlock("xenoflux",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DEEPSLATE).
                    requiresCorrectToolForDrops().strength(4.0F, 7.0F))
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> components, TooltipFlag flag)
                {
                    components.add(Component.translatable("You feel the pulsating energy.")
                            .withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
                    super.appendHoverText(stack, level, components, flag);
                }
            });
    // ---------------------------------------------------------------------------------------------------


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
                    .strength(2f)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                    return false;
                }
            });
    public static final RegistryObject<Block> XENOS_LEAVES = registerBlock("xenos_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                    return false;
                }
            });

    public static final RegistryObject<SaplingBlock> XENOS_SAPLING = registerBlock("xenos_sapling",
            () -> new SaplingBlock(new XenosTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));
    // ---------------------------------------------------------------------------------------------------


    // ------------------------------------------ Potted Blocks ------------------------------------------
    public static final RegistryObject<Block> POTTED_XENOS_SAPLING = BLOCKS.register("potted_xenos",
            () -> new FlowerPotBlock(() -> ((FlowerPotBlock) Blocks.FLOWER_POT), BlocksSTLCON.XENOS_SAPLING,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_ORANGE_TULIP)));
    // ---------------------------------------------------------------------------------------------------



    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) { RegistryObject<T> toReturn = BLOCKS.register(name, block); registerBlockItem(name, toReturn); return toReturn; }
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)  { return ItemsSTLCON.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties())); }
    public static void register(IEventBus eventBus) { BLOCKS.register(eventBus); }
}
