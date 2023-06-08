package com.feroov.frv.block;


import com.feroov.frv.STLCON;
import com.feroov.frv.events.ModParticles;
import com.feroov.frv.world.dimension.DimensionsSTLCON;
import com.feroov.frv.world.dimension.POIRegistry;
import com.feroov.frv.world.teleporters.STLCONTeleporter;
import net.minecraft.core.*;
import net.minecraft.resources.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class STLCONPortalBlocks extends Block
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
    private static Block frame;

    public STLCONPortalBlocks(Block frame)
    {
        super(BlockBehaviour.Properties.of()
                .strength(-1F)
                .noCollission()
                .lightLevel((state) -> 11)
                .sound(new
                        ForgeSoundType(1f,1f,
                        () -> SoundEvents.GLASS_BREAK,
                        () -> SoundEvents.GLASS_STEP, //step
                        () -> SoundEvents.GLASS_PLACE, //place
                        () -> SoundEvents.GLASS_HIT, //hit
                        () -> SoundEvents.GLASS_FALL) //fall
                )
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(AXIS))
        {
            case Z -> Z_AXIS_AABB;
            default -> X_AXIS_AABB;
        };
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        Direction.Axis direction$axis = facing.getAxis();
        Direction.Axis direction$axis1 = stateIn.getValue(AXIS);
        boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
        return !flag && facingState.getBlock() != this &&
                !(new STLCONPortalBlocks.Size(worldIn, currentPos, direction$axis1, this, frame))
                        .validatePortal() ? Blocks.AIR.defaultBlockState() : stateIn;
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions() && !entity.level().isClientSide
                && world != null && world.dimension() != null) {
            if (entity.isOnPortalCooldown()) {
                entity.setPortalCooldown();
            }
            if (!entity.isOnPortalCooldown() && entity instanceof LivingEntity) {
                entity.level().getProfiler().push(world.dimension().location().getPath());
                if (this == ForgeRegistries.BLOCKS.getValue(new ResourceLocation(STLCON.MOD_ID, "xenosphere_portal"))) {
                    ResourceKey<Level> key = world.dimension() == DimensionsSTLCON.XENOSPHERE_KEY ? Level.OVERWORLD : DimensionsSTLCON.XENOSPHERE_KEY;
                    if (world.getServer().getLevel(key) != null) {
                        entity.changeDimension(world.getServer().getLevel(key), new STLCONTeleporter(world.getServer().getLevel(key),
                                ForgeRegistries.BLOCKS.getValue(new ResourceLocation(STLCON.MOD_ID, "xenosphere_portal")),
                                ForgeRegistries.BLOCKS.getValue(new ResourceLocation(STLCON.MOD_ID, "xenoflux")),
                                true, POIRegistry.XENOSPHERE_PORTAL.getKey()));
                    }
                }
                entity.level().getProfiler().pop();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        if (rand.nextInt(100) == 0) {
            worldIn.playLocalSound((double)pos.getX() + 0.5D, 
                    (double)pos.getY() + 0.5D, 
                    (double)pos.getZ() + 0.5D, 
                    SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F,
                    rand.nextFloat() * 0.4F + 0.8F, false);
        }

        for(int i = 0; i < 4; ++i) {
            double d0 = (double)pos.getX() + rand.nextDouble();
            double d1 = (double)pos.getY() + rand.nextDouble();
            double d2 = (double)pos.getZ() + rand.nextDouble();
            double d3 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double d4 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double d5 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            int j = rand.nextInt(2) * 2 - 1;
            if (!worldIn.getBlockState(pos.west()).is(this) && !worldIn.getBlockState(pos.east()).is(this)) {
                d0 = (double)pos.getX() + 0.5D + 0.25D * (double)j;
                d3 = (double)(rand.nextFloat() * 2.0F * (float)j);
            } else {
                d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)j;
                d5 = (double)(rand.nextFloat() * 2.0F * (float)j);
            }

            worldIn.addParticle(ModParticles.XENOSPHERE_PORTAL_PARTICLES.get(), d0, d1, d2, d3, d4, d5);
        }
    }


    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) { return ItemStack.EMPTY; }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return switch (rot)
        {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS))
            {
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                default -> state;
            };
            default -> state;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    public boolean createPortal(LevelAccessor worldIn, BlockPos pos)
    {
        STLCONPortalBlocks.Size portal = isPortal(worldIn, pos);
        if (worldIn.getBlockState(pos.below()) == Blocks.BEDROCK.defaultBlockState())
        {
            pos = pos.below(10);
        }
        if (portal != null && !onTrySpawnPortal(worldIn, pos, portal))
        {
            portal.createPortalBlocks();
            return true;
        } else { return false;}
    }


    public static boolean onTrySpawnPortal(LevelAccessor world, BlockPos pos, STLCONPortalBlocks.Size size)
    {
        return MinecraftForge.EVENT_BUS.post(new PortalSpawnEvent(world, pos, world.getBlockState(pos), size));
    }

    public static class PortalSpawnEvent extends BlockEvent
    {
        private final STLCONPortalBlocks.Size size;

        public PortalSpawnEvent(LevelAccessor world, BlockPos pos, BlockState state, STLCONPortalBlocks.Size size)
        {
            super(world, pos, state);
            this.size = size;
        }

        public STLCONPortalBlocks.Size getPortalSize() { return size; }
    }


    @Nullable
    public STLCONPortalBlocks.Size isPortal(LevelAccessor worldIn, BlockPos pos)
    {
        STLCONPortalBlocks.Size portalX = createPortalSize(worldIn, pos, Direction.Axis.X);
        if (portalX.isValid() && portalX.portalBlockCount == 0) { return portalX;}

        STLCONPortalBlocks.Size portalZ = createPortalSize(worldIn, pos, Direction.Axis.Z);
        return (portalZ.isValid() && portalZ.portalBlockCount == 0) ? portalZ : null;
    }

    private STLCONPortalBlocks.Size createPortalSize(LevelAccessor worldIn, BlockPos pos, Direction.Axis axis)
    {
        return new STLCONPortalBlocks.Size(worldIn, pos, axis, this, worldIn.getBlockState(pos.below()).getBlock());
    }

    public static class Size
    {
        private final LevelAccessor world;
        private final Direction.Axis axis;
        private final Direction rightDir;
        private final Direction leftDir;
        private int portalBlockCount;
        @Nullable
        private BlockPos bottomLeft;
        private int height;
        private int width;
        private Block portal;
        private Block frame;

        public Size(LevelAccessor worldIn, BlockPos pos, Direction.Axis axisIn, Block portal, Block frame) {
            this.world = worldIn;
            this.axis = axisIn;
            this.portal = portal;
            this.frame = frame;
            if (axisIn == Direction.Axis.X)
            {
                this.leftDir = Direction.EAST;
                this.rightDir = Direction.WEST;
            } else { this.leftDir = Direction.NORTH; this.rightDir = Direction.SOUTH; }

            for (BlockPos blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > 0 &&
                    this.canConnect(worldIn.getBlockState(pos.below())); pos = pos.below()) {;}

            int i = this.getDistanceUntilEdge(pos, this.leftDir) - 1;
            if (i >= 0)
            {
                this.bottomLeft = pos.relative(this.leftDir, i);
                this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
                if (this.width < 2 || this.width > 21)
                {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }
            if (this.bottomLeft != null) { this.height = this.calculatePortalHeight(); }
        }

        protected int getDistanceUntilEdge(BlockPos pos, Direction directionIn) {
            int i;
            for (i = 0; i < 22; ++i)
            {
                BlockPos blockpos = pos.relative(directionIn, i);
                if (!this.canConnect(this.world.getBlockState(blockpos)) ||
                        !(this.world.getBlockState(blockpos.below()).getBlock()
                                .equals(this.world.getBlockState(pos.below()).getBlock()))) { break; }
            }

            BlockPos framePos = pos.relative(directionIn, i);
            return this.world.getBlockState(framePos).getBlock().equals(this.world.getBlockState(pos.below()).getBlock()) ? i : 0;
        }

        public int getHeight() { return this.height; }
        public int getWidth() { return this.width; }

        protected int calculatePortalHeight()
        {
            exitNestedLoops:
            for (this.height = 0; this.height < 21; ++this.height)
            {
                for (int i = 0; i < this.width; ++i)
                {
                    BlockPos blockpos = this.bottomLeft.relative(this.rightDir, i).above(this.height);
                    BlockState blockstate = world.getBlockState(blockpos);
                    if (!this.canConnect(blockstate)) { break exitNestedLoops; }

                    Block block = blockstate.getBlock();
                    if (block == portal) { ++this.portalBlockCount; }

                    if (i == 0)
                    {
                        BlockPos framePos = blockpos.relative(this.leftDir);
                        if (!(world.getBlockState(framePos).getBlock().equals(frame))) { break exitNestedLoops; }
                        if (!(world.getBlockState(framePos).getBlock() == frame)) { break exitNestedLoops; }
                    }
                    else if (i == this.width - 1)
                    {
                        BlockPos framePos = blockpos.relative(this.rightDir);
                        if (!(world.getBlockState(framePos).getBlock().equals(frame))) { break exitNestedLoops; }
                        if (!(world.getBlockState(framePos).getBlock() == frame)) { break exitNestedLoops; }
                    }
                }
            }

            for (int j = 0; j < this.width; ++j)
            {
                BlockPos framePos = this.bottomLeft.relative(this.rightDir, j).above(this.height);
                if (!(world.getBlockState(framePos).getBlock().equals(frame))) { this.height = 0; break; }
                if (!(world.getBlockState(framePos).getBlock() == frame)) { this.height = 0; break; }
            }

            if (this.height <= 21 && this.height >= 3)
            {
                return this.height;
            } else { this.bottomLeft = null; this.width = 0; this.height = 0; return 0; }
        }

        protected boolean canConnect(BlockState pos)
        {
            Block block = pos.getBlock();
            return pos.isAir() || block == this.portal;
        }

        public boolean isValid()
        {
            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void createPortalBlocks()
        {
            BlockState blockstate = this.portal.defaultBlockState().setValue(STLCONPortalBlocks.AXIS, this.axis);
            BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1)
                    .relative(this.rightDir, this.width - 1)).forEach((pos) -> {
                this.world.setBlock(pos, blockstate, 18);
            });
        }

        private boolean isPortalCountValidForSize() { return this.portalBlockCount >= this.width * this.height; }
        public boolean validatePortal() { return this.isValid() && this.isPortalCountValidForSize(); }
    }
}