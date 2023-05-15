package com.feroov.frv.block;

import com.feroov.frv.STLCON;
import com.feroov.frv.block.custom.Xenosgrass;
import com.feroov.frv.block.custom.XenosgrassBlock;
import com.feroov.frv.block.custom.XenosphereTeleporterBlock;
import com.feroov.frv.item.ItemsSTLCON;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
