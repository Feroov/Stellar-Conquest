package com.feroov.frv.world.structure;

import com.feroov.frv.STLCON;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class StructuresSTLCON
{
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registries.STRUCTURE_TYPE, STLCON.MOD_ID);

    public static final RegistryObject<StructureType<StructureMain>> STLCON_STRUCTURES =
            DEFERRED_REGISTRY_STRUCTURE.register("stlcon_structures", () -> typeConvert(StructureMain.CODEC));

    private static <S extends Structure> StructureType<S> typeConvert(Codec<S> codec)
    {
        return () -> codec;
    }
}
