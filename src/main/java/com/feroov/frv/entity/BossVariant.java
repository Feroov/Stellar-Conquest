package com.feroov.frv.entity;

import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import java.util.Locale;
import java.util.function.Supplier;

public enum BossVariant implements StringRepresentable, SkullBlock.Type
{

    MOTHERSHIP(BlockEntitiesSTLCON.MOTHERSHIP_SPAWNER::get),
    MEKKRON(BlockEntitiesSTLCON.MEKKRON_SPAWNER::get);

    private final Supplier<BlockEntityType<? extends BossSpawnerBlockEntity<?>>> blockEntityType;

    public static final BossVariant[] VARIANTS = values();

    BossVariant(@Nullable Supplier<BlockEntityType<? extends BossSpawnerBlockEntity<?>>> blockEntityType)
    {
        this.blockEntityType = blockEntityType;
    }

    @Override
    public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }

    @Nullable
    public BlockEntityType<? extends BossSpawnerBlockEntity<?>> getType() { return blockEntityType != null ? blockEntityType.get() : null; }
}
