package net.dark.spv_addon.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.HorizontalFacingBlock;
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

public class PlushieBlock extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    // Combine all cuboids from your Blockbench JSON
    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(3.975, 4.06727, 6, 11.975, 12.06727, 14),
            Block.createCuboidShape(3.775, 3.86727, 5.8, 12.175, 12.26727, 14.2),
            Block.createCuboidShape(5.375, 0.26727, 7.4, 10.575, 4.46727, 12.6),
            Block.createCuboidShape(5.175, 0.26727, 7.2, 10.775, 4.26727, 12.8),
            Block.createCuboidShape(4.975, -0.13273, 3.4, 6.975, 1.86727, 8.4),
            Block.createCuboidShape(8.975, -0.13273, 3.4, 10.975, 1.86727, 8.4),
            Block.createCuboidShape(9.975, -0.13273, 9, 11.975, 4.86727, 11),
            Block.createCuboidShape(9.895, -0.45273, 8.92, 12.095, 5.34727, 11.12),
            Block.createCuboidShape(4.17901, -0.40526, 8.92, 6.37901, 5.39474, 11.12),
            Block.createCuboidShape(4.25901, -0.08526, 9, 6.25901, 4.91474, 11)
    );

    public PlushieBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Block faces opposite the player (so "front" faces player)
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityShapeContext context) {
        return getRotatedShape(state);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, EntityShapeContext context) {
        return getRotatedShape(state);
    }

    private VoxelShape getRotatedShape(BlockState state) {
        Direction facing = state.get(FACING);
        // VoxelShapes.rotate not available, so use manual rotation
        switch (facing) {
            case SOUTH:
                return SHAPE;
            case WEST:
                return rotateShapeY(SHAPE, 1);
            case NORTH:
                return rotateShapeY(SHAPE, 2);
            case EAST:
                return rotateShapeY(SHAPE, 3);
            default:
                return SHAPE;
        }
    }

    // Rotates the shape around the Y axis 90° * times (clockwise)
    public static VoxelShape rotateShapeY(VoxelShape shape, int times) {
        VoxelShape rotated = shape;
        for (int i = 0; i < times; i++) {
            VoxelShape temp = VoxelShapes.empty();
            for (var box : rotated.getBoundingBoxes()) {
                temp = VoxelShapes.union(temp, Block.createCuboidShape(
                        16 - box.minZ, box.minY, box.minX,
                        16 - box.maxZ, box.maxY, box.maxX
                ));
            }
            rotated = temp;
        }
        return rotated;
    }
}
