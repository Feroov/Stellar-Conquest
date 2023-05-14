package com.feroov.frv.world.teleporters;

import com.feroov.frv.block.BlocksSTLCON;
import com.feroov.frv.block.custom.XenosphereTeleporterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class XenosphereTeleporter implements ITeleporter
{
    public static BlockPos thisPos = BlockPos.ZERO;
    public static boolean insideDimension = true;

    public XenosphereTeleporter(BlockPos pos, boolean insideDim)
    {
        thisPos = pos;
        insideDimension = insideDim;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destinationWorld,
                              float yaw, Function<Boolean, Entity> repositionEntity)
    {
        entity = repositionEntity.apply(false);
        int y = 69;

        if (!insideDimension)
        {
            y = thisPos.getY();
        }

        BlockPos destinationPos = new BlockPos(thisPos.getX(), y, thisPos.getZ());

        int tries = 0;
        while ((destinationWorld.getBlockState(destinationPos).getMaterial() != Material.AIR) &&
                !destinationWorld.getBlockState(destinationPos).canBeReplaced(Fluids.WATER) &&
                destinationWorld.getBlockState(destinationPos.above()).getMaterial() != Material.AIR &&
                !destinationWorld.getBlockState(destinationPos.above()).canBeReplaced(Fluids.WATER) && tries < 25)
        {
            destinationPos = destinationPos.above(2);
            tries++;
        }

        entity.teleportTo(destinationPos.getX(), destinationPos.getY(), destinationPos.getZ());

        if (insideDimension)
        {
            boolean doSetBlock = true;
            for (BlockPos checkPos : BlockPos.betweenClosed(destinationPos.below(10).west(10).south(10), destinationPos.above(10).east(10).north(10)))
            {
                if (destinationWorld.getBlockState(checkPos).getBlock() instanceof XenosphereTeleporterBlock)
                {
                    doSetBlock = false;
                    break;
                }
            }
            if (doSetBlock)
            {
                destinationWorld.setBlock(destinationPos, BlocksSTLCON.XENOSPHERE_PORTAL.get().defaultBlockState(), 3);
            }
        }

        return entity;
    }

    @Override
    public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld)
    {
        return true;
    }
}
