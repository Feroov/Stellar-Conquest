package com.feroov.frv.entity.spawner;

import com.feroov.frv.entity.BlockEntitiesSTLCON;
import com.feroov.frv.entity.BossSpawnerBlockEntity;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.monster.Mekkron;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class MekkronSpawnerBlockEntity extends BossSpawnerBlockEntity<Mekkron>
{
    public MekkronSpawnerBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntitiesSTLCON.MEKKRON_SPAWNER.get(), EntitiesSTLCON.MEKKRON.get(), pos, state);
    }

    @Override
    protected int getRange() { return SHORT_RANGE; }

    @Override
    public ParticleOptions getSpawnerParticle() { return ParticleTypes.CRIT; }
}
