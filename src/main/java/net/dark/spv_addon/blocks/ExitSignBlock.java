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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.EnumMap;
import java.util.Map;

public class ExitSignBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(
            5, 0, 6, // from (x, y, z) - 5, 0, 6
            11, 16, 10 // to (x, y, z) - 11, 16, 10
    );
    private static final Map<Direction, VoxelShape> ROTATED_SHAPES = new EnumMap<>(Direction.class);

    static {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            ROTATED_SHAPES.put(dir, rotateShape(BASE_SHAPE, dir));
        }
    }

    public ExitSignBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }
    // Rotates a VoxelShape for each direction
    private static VoxelShape rotateShape(VoxelShape shape, Direction dir) {
        // No rotation for NORTH
        if (dir == Direction.NORTH) return shape;

        VoxelShape[] buffer = new VoxelShape[] { shape, VoxelShapes.empty() };
        int times = (dir == Direction.SOUTH) ? 2 : (dir == Direction.WEST ? 1 : 3);

        for (int i = 0; i < times; ++i) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                // 90 deg Y rotation: (x, z) -> (1-z, x)
                buffer[1] = VoxelShapes.union(buffer[1], VoxelShapes.cuboid(
                        1 - maxZ, minY, minX, 1 - minZ, maxY, maxX
                ));
            });
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }
        return buffer[0];
    }
    // Placement
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    // Properties
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // Outline shape (what you see)
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return ROTATED_SHAPES.get(state.get(FACING));
    }

    // Collision shape (what you bump into)
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return ROTATED_SHAPES.get(state.get(FACING));
    }

    // Rotation and mirror for blockstates (for structure placing etc)
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
