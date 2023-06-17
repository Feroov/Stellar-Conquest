package com.feroov.frv.entity.spawner;

import com.feroov.frv.entity.BlockEntitiesSTLCON;
import com.feroov.frv.entity.BossSpawnerBlockEntity;
import com.feroov.frv.entity.EntitiesSTLCON;
import com.feroov.frv.entity.monster.Mothership;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;

public class MothershipSpawnerBlockEntity extends BossSpawnerBlockEntity<Mothership>
{

    public MothershipSpawnerBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntitiesSTLCON.MOTHERSHIP_SPAWNER.get(), EntitiesSTLCON.MOTHERSHIP.get(), pos, state);
    }

    @Override
    protected int getRange() { return LONG_RANGE; }

    @Override
    public ParticleOptions getSpawnerParticle() { return ParticleTypes.CRIT; }
}
