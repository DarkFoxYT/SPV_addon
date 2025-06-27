package net.dark.spv_addon.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class ShelfBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0 / 16.0, 1.0),
            VoxelShapes.cuboid(0.0, 1.0 / 16.0, 7.0 / 16.0, 1.0, 1.0, 9.0 / 16.0)
    );

    private static final Map<Direction, VoxelShape> ROTATED_SHAPES = new EnumMap<>(Direction.class);

    static {
        ROTATED_SHAPES.put(Direction.NORTH, SHAPE);
        ROTATED_SHAPES.put(Direction.SOUTH, rotateShape180(SHAPE));
        ROTATED_SHAPES.put(Direction.WEST, rotateShape90(SHAPE));
        ROTATED_SHAPES.put(Direction.EAST, rotateShape270(SHAPE));
    }

    public ShelfBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    private static VoxelShape rotateShape180(VoxelShape shape) {
        VoxelShape[] buffer = {VoxelShapes.empty()};
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            buffer[0] = VoxelShapes.union(buffer[0], VoxelShapes.cuboid(
                    1 - maxX, minY, 1 - maxZ, 1 - minX, maxY, 1 - minZ
            ));
        });
        return buffer[0];
    }

    private static VoxelShape rotateShape90(VoxelShape shape) {
        VoxelShape[] buffer = {VoxelShapes.empty()};
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            buffer[0] = VoxelShapes.union(buffer[0], VoxelShapes.cuboid(
                    minZ, minY, 1 - maxX, maxZ, maxY, 1 - minX
            ));
        });
        return buffer[0];
    }

    private static VoxelShape rotateShape270(VoxelShape shape) {
        VoxelShape[] buffer = {VoxelShapes.empty()};
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            buffer[0] = VoxelShapes.union(buffer[0], VoxelShapes.cuboid(
                    1 - maxZ, minY, minX, 1 - minZ, maxY, maxX
            ));
        });
        return buffer[0];
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, net.minecraft.util.math.BlockPos pos, ShapeContext context) {
        return ROTATED_SHAPES.get(state.get(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, net.minecraft.util.math.BlockPos pos, ShapeContext context) {
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