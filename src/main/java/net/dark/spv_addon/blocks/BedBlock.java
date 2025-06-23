package net.dark.spv_addon.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class BedBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<BedPart> PART = EnumProperty.of("part", BedPart.class);

    private static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0/16.0, -3/16.0, 0/16.0, 2/16.0, 6/16.0, 2/16.0),
            VoxelShapes.cuboid(14/16.0, -3/16.0, 0/16.0, 16/16.0, 6/16.0, 2/16.0),
            VoxelShapes.cuboid(0/16.0, -3/16.0, 30/16.0, 2/16.0, 6/16.0, 32/16.0),
            VoxelShapes.cuboid(14/16.0, -3/16.0, 30/16.0, 16/16.0, 6/16.0, 32/16.0),
            VoxelShapes.cuboid(0/16.0, 6/16.0, 0/16.0, 16/16.0, 10/16.0, 32/16.0),
            VoxelShapes.cuboid(-0.5/16.0, 6.6/16.0, -0.9/16.0, 16.5/16.0, 12.1/16.0, 25.2/16.0),
            VoxelShapes.cuboid(4.3/16.0, 7.3/16.0, 25.9/16.0, 15.7/16.0, 11.3/16.0, 31.4/16.0)
    );

    private static final VoxelShape SHAPE_EMPTY = VoxelShapes.empty();

    private static final Map<Direction, VoxelShape> ROTATED_SHAPES = new EnumMap<>(Direction.class);

    static {
        ROTATED_SHAPES.put(Direction.NORTH, SHAPE);
        ROTATED_SHAPES.put(Direction.SOUTH, rotateShape180Foot(SHAPE));
        ROTATED_SHAPES.put(Direction.WEST, rotateShape90Foot(SHAPE));
        ROTATED_SHAPES.put(Direction.EAST, rotateShape270Foot(SHAPE));
    }

    public BedBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(PART, BedPart.FOOT));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getHorizontalPlayerFacing();
        BlockPos headPos = ctx.getBlockPos().offset(facing);
        if (!ctx.getWorld().getBlockState(headPos).canReplace(ctx)) return null;
        return getDefaultState().with(FACING, facing).with(PART, BedPart.FOOT);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable net.minecraft.entity.LivingEntity placer, net.minecraft.item.ItemStack itemStack) {
    }

    private static VoxelShape rotateShape180Foot(VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[] { VoxelShapes.empty() };
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double newMinX = 1 - maxX;
            double newMaxX = 1 - minX;
            double newMinZ = 1 - maxZ;
            double newMaxZ = 1 - minZ;
            buffer[0] = VoxelShapes.union(buffer[0], VoxelShapes.cuboid(
                    newMinX, minY, newMinZ, newMaxX, maxY, newMaxZ
            ));
        });
        return buffer[0];
    }

    private static VoxelShape rotateShape90Foot(VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[] { VoxelShapes.empty() };
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double newMinX = minZ;
            double newMaxX = maxZ;
            double newMinZ = 1 - maxX;
            double newMaxZ = 1 - minX;
            buffer[0] = VoxelShapes.union(buffer[0], VoxelShapes.cuboid(
                    newMinX, minY, newMinZ, newMaxX, maxY, newMaxZ
            ));
        });
        return buffer[0];
    }

    private static VoxelShape rotateShape270Foot(VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[] { VoxelShapes.empty() };
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double newMinX = 1 - maxZ;
            double newMaxX = 1 - minZ;
            double newMinZ = minX;
            double newMaxZ = maxX;
            buffer[0] = VoxelShapes.union(buffer[0], VoxelShapes.cuboid(
                    newMinX, minY, newMinZ, newMaxX, maxY, newMaxZ
            ));
        });
        return buffer[0];
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, net.minecraft.entity.player.PlayerEntity player) {
        BedPart part = state.get(PART);
        Direction facing = state.get(FACING);
        BlockPos otherPos = part == BedPart.FOOT ? pos.offset(facing) : pos.offset(facing.getOpposite());
        BlockState otherState = world.getBlockState(otherPos);
        if (otherState.getBlock() == this && otherState.get(PART) != part) {
            world.breakBlock(otherPos, false, player);
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        if (state.get(PART) == BedPart.HEAD) return SHAPE_EMPTY;
        return ROTATED_SHAPES.get(state.get(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        if (state.get(PART) == BedPart.HEAD) return SHAPE_EMPTY;
        return ROTATED_SHAPES.get(state.get(FACING));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}