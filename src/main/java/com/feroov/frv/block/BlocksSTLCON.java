package com.feroov.frv.block;

import com.feroov.frv.STLCON;
import com.feroov.frv.block.custom.XenosphereBlock;
import com.feroov.frv.item.ItemsSTLCON;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
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
            () -> new XenosphereBlock(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().sound(new
                    ForgeSoundType(1f,1f,
                    () -> SoundEvents.SCULK_CATALYST_BLOOM,
                    () -> SoundEvents.SCULK_BLOCK_STEP, //step
                    () -> SoundEvents.SCULK_BLOCK_PLACE, //place
                    () -> SoundEvents.SCULK_BLOCK_HIT, //hit
                    () -> SoundEvents.SCULK_BLOCK_FALL) //fall
            ).strength(15.0F, 15.0F).lightLevel((light) -> {return 15;}))
    );


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
