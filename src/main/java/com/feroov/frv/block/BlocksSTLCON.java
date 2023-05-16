package com.feroov.frv.block;

import com.feroov.frv.STLCON;
import com.feroov.frv.block.custom.XenosWoodType;
import com.feroov.frv.block.custom.Xenosgrass;
import com.feroov.frv.block.custom.XenosgrassBlock;
import com.feroov.frv.block.custom.XenosphereTeleporterBlock;
import com.feroov.frv.item.ItemsSTLCON;
import com.feroov.frv.world.tree.XenosTreeGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlocksSTLCON
{
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, STLCON.MOD_ID);

    public static final RegistryObject<Block> XENOSPHERE_PORTAL = registerBlock("xenosphere_portal",
            () -> new XenosphereTeleporterBlock(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().sound(new
                    ForgeSoundType(1f,1f,
                    () -> SoundEvents.SCULK_CATALYST_BLOOM,
                    () -> SoundEvents.SCULK_BLOCK_STEP, //step
                    () -> SoundEvents.SCULK_BLOCK_PLACE, //place
                    () -> SoundEvents.SCULK_BLOCK_HIT, //hit
                    () -> SoundEvents.SCULK_BLOCK_FALL) //fall
            ).strength(15.0F, 15.0F).lightLevel((light) -> {return 15;}))
    );

    public static final RegistryObject<Block> XENOSGRASS_BLOCK = registerBlock("xenosgrass_block",
            () -> new XenosgrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).strength(0.6F).sound(SoundType.GRASS)));

    public static final RegistryObject<Block> XENOSDIRT = registerBlock("xenosdirt",
            () -> new     Block(BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(0.5F).sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> XENOSGRASS = registerBlock("xenosgrass",
            () -> new Xenosgrass(BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT)
                    .instabreak().sound(SoundType.GRASS).noCollission().noOcclusion()));

    public static final RegistryObject<Block> XENOSTONE = registerBlock("xenostone",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).
                    requiresCorrectToolForDrops().strength(2.5F, 7.0F)));

    public static final RegistryObject<Block> XENOCOBBLESTONE = registerBlock("xenocobblestone",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).
                    requiresCorrectToolForDrops().strength(3.1F, 7.0F)));

    public static final RegistryObject<Block> XENOS_LOG = registerBlock("xenos_log",
            () -> new XenosWoodType(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)
                    .strength(3f)));

    public static final RegistryObject<Block> XENOS_WOOD = registerBlock("xenos_wood",
            () -> new XenosWoodType(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)
                    .strength(3f)));

    public static final RegistryObject<Block> STRIPPED_XENOS_LOG = registerBlock("stripped_xenos_log",
            () -> new XenosWoodType(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG)
                    .strength(3f)));

    public static final RegistryObject<Block> STRIPPED_XENOS_WOOD = registerBlock("stripped_xenos_wood",
            () -> new XenosWoodType(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)
                    .strength(3f)));

    public static final RegistryObject<Block> XENOS_PLANKS = registerBlock("xenos_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
                    .strength(3f)) {
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



    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block)
    {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)
    {
        return ItemsSTLCON.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties()));
    }

    public static void register(IEventBus eventBus) { BLOCKS.register(eventBus); }
}
