package com.feroov.frv.entity;

import com.feroov.frv.STLCON;
import com.feroov.frv.block.BlocksSTLCON;
import com.feroov.frv.entity.spawner.MothershipSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntitiesSTLCON
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, STLCON.MOD_ID);


    public static final RegistryObject<BlockEntityType<MothershipSpawnerBlockEntity>> MOTHERSHIP_SPAWNER = BLOCK_ENTITIES.register("mothership_spawner", () ->
            BlockEntityType.Builder.of(MothershipSpawnerBlockEntity::new, BlocksSTLCON.MOTHERSHIP_SPAWNER.get()).build(null));
}
