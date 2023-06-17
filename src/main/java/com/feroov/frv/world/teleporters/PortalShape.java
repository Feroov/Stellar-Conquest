package com.feroov.frv.world.teleporters;

import net.minecraft.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.tags.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.portal.*;
import net.minecraft.world.phys.*;

import javax.annotation.*;
import java.util.*;
import java.util.function.*;

public class PortalShape
{
    private static final BlockBehaviour.StatePredicate FRAME = (blockState, blockGetter, blockPos) ->
    {
        return blockState.isPortalFrame(blockGetter, blockPos);
    };

    private static Block portal, frame;
    private final LevelAccessor level;
    private final Direction.Axis axis;
    private final Direction rightDir;
    private int numPortalBlocks;
    @Nullable
    private BlockPos bottomLeft;
    private int height;
    private final int width;

    public PortalShape(LevelAccessor world, BlockPos pos, Direction.Axis axis, Block portal, Block frame)
    {
        this.level = world;
        this.axis = axis;
        this.rightDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.bottomLeft = this.calculateBottomLeft(pos);
        if (this.bottomLeft == null)
        {
            this.bottomLeft = pos;
            this.width = 1;
            this.height = 1;
        }
        else
        {
            this.width = this.calculateWidth();
            if (this.width > 0) { this.height = this.calculateHeight(); }
        }
        PortalShape.portal = portal;
        PortalShape.frame = frame;
    }

    private static boolean isEmpty(BlockState blockState)
    {
        return blockState.isAir() || blockState.is(BlockTags.FIRE) || blockState.is(portal);
    }

    public static Vec3 getRelativePosition(BlockUtil.FoundRectangle foundRectangle, Direction.Axis axis, Vec3 vec3, EntityDimensions entityDimensions)
    {
        double d0 = (double) foundRectangle.axis1Size - (double) entityDimensions.width;
        double d1 = (double) foundRectangle.axis2Size - (double) entityDimensions.height;
        BlockPos blockpos = foundRectangle.minCorner;
        double d2;
        if (d0 > 0.0D)
        {
            float f = (float) blockpos.get(axis) + entityDimensions.width / 2.0F;
            d2 = Mth.clamp(Mth.inverseLerp(vec3.get(axis) - (double) f, 0.0D, d0), 0.0D, 1.0D);
        }
        else { d2 = 0.5D; }

        double d4;
        if (d1 > 0.0D)
        {
            Direction.Axis direction$axis = Direction.Axis.Y;
            d4 = Mth.clamp(Mth.inverseLerp(vec3.get(direction$axis) - (double) blockpos.get(direction$axis), 0.0D, d1), 0.0D, 1.0D);
        }
        else { d4 = 0.0D; }

        Direction.Axis direction$axis1 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        double d3 = vec3.get(direction$axis1) - ((double) blockpos.get(direction$axis1) + 0.5D);
        return new Vec3(d2, d4, d3);
    }

    public static PortalInfo createPortalInfo(ServerLevel serverLevel, BlockUtil.FoundRectangle foundRectangle, Direction.Axis axis, Vec3 vec3, EntityDimensions entityDimensions, Vec3 vec, float f1, float f2)
    {
        BlockPos blockpos = foundRectangle.minCorner;
        BlockState blockstate = serverLevel.getBlockState(blockpos);
        Direction.Axis direction$axis = blockstate.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        double d0 = foundRectangle.axis1Size;
        double d1 = foundRectangle.axis2Size;
        int i = axis == direction$axis ? 0 : 90;
        Vec3 vector3d = axis == direction$axis ? vec : new Vec3(vec.z, vec.y, -vec.x);
        double d2 = (double) entityDimensions.width / 2.0D + (d0 - (double) entityDimensions.width) * vec3.x();
        double d3 = (d1 - (double) entityDimensions.height) * vec3.y();
        double d4 = 0.5D + vec3.z();
        boolean flag = direction$axis == Direction.Axis.X;
        Vec3 vector3d1 = new Vec3((double) blockpos.getX() + (flag ? d2 : d4), (double) blockpos.getY() + d3, (double) blockpos.getZ() + (flag ? d4 : d2));
        return new PortalInfo(vector3d1, vector3d, f1 + (float) i, f2);
    }

    public Optional<PortalShape> findEmptyPortalShape(LevelAccessor levelAccessor, BlockPos blockPos, Direction.Axis axis)
    {
        return findPortalShape(levelAccessor, blockPos, (p_242968_0_) ->
        {
            return p_242968_0_.isValid() && p_242968_0_.numPortalBlocks == 0;
        }, axis);
    }

    public Optional<PortalShape> findPortalShape(LevelAccessor levelAccessor, BlockPos blockPos, Predicate<PortalShape> shapePredicate, Direction.Axis axis)
    {
        Optional<PortalShape> optional = Optional.of(new PortalShape(levelAccessor, blockPos, axis, portal, frame)).filter(shapePredicate);
        if (optional.isPresent()) { return optional; }
        else
        {
            Direction.Axis direction$axis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            return Optional.of(new PortalShape(levelAccessor, blockPos, direction$axis, portal, frame)).filter(shapePredicate);
        }
    }

    @Nullable
    private BlockPos calculateBottomLeft(BlockPos blockPos)
    {
        for (int i = Math.max(0, blockPos.getY() - 21); blockPos.getY() > i && isEmpty(this.level.getBlockState(blockPos.below())); blockPos = blockPos.below()) {}

        Direction direction = this.rightDir.getOpposite();
        int j = this.getDistanceUntilEdgeAboveFrame(blockPos, direction) - 1;
        return j < 0 ? null : blockPos.relative(direction, j);
    }

    private int calculateWidth()
    {
        int i = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
        return i >= 2 && i <= 21 ? i : 0;
    }

    private int getDistanceUntilEdgeAboveFrame(BlockPos blockPos, Direction direction)
    {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for (int i = 0; i <= 21; ++i)
        {
            blockpos$mutable.set(blockPos).move(direction, i);
            BlockState blockstate = this.level.getBlockState(blockpos$mutable);
            if (!isEmpty(blockstate))
            {
                if (FRAME.test(blockstate, this.level, blockpos$mutable)) { return i; }
                break;
            }

            BlockState blockstate1 = this.level.getBlockState(blockpos$mutable.move(Direction.DOWN));
            if (!FRAME.test(blockstate1, this.level, blockpos$mutable)) { break; }
        }
        return 0;
    }

    private int calculateHeight()
    {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        int i = this.getDistanceUntilTop(blockpos$mutable);
        return i >= 3 && i <= 21 && this.hasTopFrame(blockpos$mutable, i) ? i : 0;
    }

    private boolean hasTopFrame(BlockPos.MutableBlockPos mutableBlockPos, int i2)
    {
        for (int i = 0; i < this.width; ++i)
        {
            BlockPos.MutableBlockPos blockpos$mutable = mutableBlockPos.set(this.bottomLeft).move(Direction.UP, i2).move(this.rightDir, i);
            if (!FRAME.test(this.level.getBlockState(blockpos$mutable), this.level, blockpos$mutable)) { return false; }
        }
        return true;
    }

    private int getDistanceUntilTop(BlockPos.MutableBlockPos mutableBlockPos)
    {
        for (int i = 0; i < 21; ++i)
        {
            mutableBlockPos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
            if (!FRAME.test(this.level.getBlockState(mutableBlockPos), this.level, mutableBlockPos))  { return i; }

            mutableBlockPos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
            if (!FRAME.test(this.level.getBlockState(mutableBlockPos), this.level, mutableBlockPos)) { return i; }

            for (int j = 0; j < this.width; ++j)
            {
                mutableBlockPos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
                BlockState blockstate = this.level.getBlockState(mutableBlockPos);
                if (!isEmpty(blockstate)) { return i; }
                if (blockstate.is(portal)) { ++this.numPortalBlocks; }
            }
        }
        return 21;
    }

    public boolean isValid()
    {
        return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void createPortalBlocks()
    {
        BlockState blockstate = portal.defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis);
        BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((blockPos) -> {
            this.level.setBlock(blockPos, blockstate, 18);
        });
    }

    public boolean isComplete() { return this.isValid() && this.numPortalBlocks == this.width * this.height; }
}
