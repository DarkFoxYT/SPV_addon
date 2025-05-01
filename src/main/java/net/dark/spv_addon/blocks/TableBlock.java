package net.dark.spv_addon.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;

public class TableBlock extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty SIDES = IntProperty.of("sides", 1, 4);

    // four legs
    private static final VoxelShape LEG_NW = Block.createCuboidShape(0, 0, 0,   2, 12, 2);
    private static final VoxelShape LEG_NE = Block.createCuboidShape(14,0, 0,  16,12, 2);
    private static final VoxelShape LEG_SW = Block.createCuboidShape(0, 0, 14,  2, 12,16);
    private static final VoxelShape LEG_SE = Block.createCuboidShape(14,0,14, 16,12,16);
    // tabletop (thin slab 2 units tall)
    private static final VoxelShape TOP    = Block.createCuboidShape(0, 12, 0, 16,14,16);
    // union into one shape
    private static final VoxelShape SHAPE  = VoxelShapes.union(LEG_NW, LEG_NE, LEG_SW, LEG_SE, TOP);

    public TableBlock(Settings settings) {
        super(settings);
        // initialize default state
        this.setDefaultState(this.getStateManager()
                .getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(SIDES, 1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block,BlockState> builder) {
        builder.add(FACING, SIDES);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState here = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (here.isOf(this)) {
            // increment sides
            return here.with(SIDES, Math.min(4, here.get(SIDES) + 1))
                    .with(FACING, here.get(FACING));
        } else {
            return this.getDefaultState()
                    .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                    .with(SIDES, 1);
        }
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
        return !ctx.shouldCancelInteraction()
                && ctx.getStack().isOf(this.asItem())
                && state.get(SIDES) < 4
                || super.canReplace(state, ctx);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    // collision shape
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, CollisionView ctx) {
        return SHAPE;
    }

    // outline (what the wireframe shows)
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, CollisionView ctx) {
        return SHAPE;
    }
}
