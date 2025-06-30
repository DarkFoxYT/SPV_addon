//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.dark.spv_addon.blocks.level188;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SlopeWallBlock extends Block implements Waterloggable {
    public static final DirectionProperty FACING;
    public static final EnumProperty<BlockHalf> HALF;
    public static final BooleanProperty WATERLOGGED;
    private static final VoxelShape SHAPE_NORTH;
    private static final VoxelShape SHAPE_SOUTH;
    private static final VoxelShape SHAPE_WEST;
    private static final VoxelShape SHAPE_EAST;
    public SlopeWallBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_SOUTH;
        };

    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos();
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(HALF, direction == Direction.DOWN || direction != Direction.UP && ctx.getHitPos().y - (double)blockPos.getY() > (double)0.5F ? BlockHalf.TOP : BlockHalf.BOTTOM).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, WATERLOGGED);
    }

    static {
        FACING = HorizontalFacingBlock.FACING;
        HALF = Properties.BLOCK_HALF;
        WATERLOGGED = Properties.WATERLOGGED;
        SHAPE_NORTH = VoxelShapes.union(
                Block.createCuboidShape(0, 0, 12, 4, 16, 16),
                Block.createCuboidShape(0, 0, 8, 8, 16, 12),
                Block.createCuboidShape(0, 0, 4, 12, 16, 8),
                Block.createCuboidShape(0, 0, 0, 16, 16, 4)
        );

        SHAPE_EAST = VoxelShapes.union(
                Block.createCuboidShape(0, 0, 0, 4, 16, 4),
                Block.createCuboidShape(4, 0, 0, 8, 16, 8),
                Block.createCuboidShape(8, 0, 0, 12, 16, 12),
                Block.createCuboidShape(12, 0, 0, 16, 16, 16)
        );

        SHAPE_SOUTH = VoxelShapes.union(
                Block.createCuboidShape(12, 0, 0, 16, 16, 4),
                Block.createCuboidShape(8, 0, 0, 12, 16, 8),
                Block.createCuboidShape(4, 0, 0, 8, 16, 12),
                Block.createCuboidShape(0, 0, 0, 4, 16, 16)
        );

        SHAPE_WEST = VoxelShapes.union(
                Block.createCuboidShape(12, 0, 12, 16, 16, 16),
                Block.createCuboidShape(8, 0, 8, 12, 16, 16),
                Block.createCuboidShape(4, 0, 4, 8, 16, 16),
                Block.createCuboidShape(0, 0, 0, 4, 16, 16)
        );




    }
}
