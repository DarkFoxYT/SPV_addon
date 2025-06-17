package net.dark.spv_addon.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumMap;
import java.util.Map;

public class VentBlock extends Block {
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_CLOSED = VoxelShapes.union(
            VoxelShapes.cuboid(15/16.0, 1/16.0, 15/16.0, 16/16.0, 15/16.0, 16/16.0),
            VoxelShapes.cuboid(0/16.0, 0/16.0, 15/16.0, 16/16.0, 1/16.0, 16/16.0),
            VoxelShapes.cuboid(0/16.0, 15/16.0, 15/16.0, 16/16.0, 16/16.0, 16/16.0),
            VoxelShapes.cuboid(0/16.0, 1/16.0, 15/16.0, 1/16.0, 15/16.0, 16/16.0),
            VoxelShapes.cuboid(1/16.0, 1/16.0, 15.25/16.0, 15/16.0, 15/16.0, 15.75/16.0)
    );
    private static final VoxelShape SHAPE_OPEN = VoxelShapes.union(
            VoxelShapes.cuboid(15/16.0, 1/16.0, 15/16.0, 16/16.0, 15/16.0, 16/16.0),
            VoxelShapes.cuboid(0/16.0, 0/16.0, 15/16.0, 16/16.0, 1/16.0, 16/16.0),
            VoxelShapes.cuboid(0/16.0, 15/16.0, 15/16.0, 16/16.0, 16/16.0, 16/16.0),
        VoxelShapes.cuboid(0/16.0, 1/16.0, 15/16.0, 1/16.0, 15/16.0, 16/16.0)
    );

    private static final Map<Direction, VoxelShape> ROTATED_CLOSED = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> ROTATED_OPEN = new EnumMap<>(Direction.class);

    static {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            ROTATED_CLOSED.put(dir, rotateShape(SHAPE_CLOSED, dir));
            ROTATED_OPEN.put(dir, rotateShape(SHAPE_OPEN, dir));
        }
    }

    public VentBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(OPEN, false)
                .with(FACING, Direction.SOUTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(OPEN, false);
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction dir) {
        switch (dir) {
            case NORTH:
                return shape;
            case SOUTH: {
                VoxelShape[] buffer = new VoxelShape[] { VoxelShapes.empty() };
                shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                    buffer[0] = VoxelShapes.union(buffer[0], VoxelShapes.cuboid(
                            1 - maxX, minY, 1 - maxZ, 1 - minX, maxY, 1 - minZ
                    ));
                });
                return buffer[0];
            }
            case WEST: {
                VoxelShape[] buffer = new VoxelShape[] { VoxelShapes.empty() };
                shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                    buffer[0] = VoxelShapes.union(buffer[0], VoxelShapes.cuboid(
                            minZ, minY, 1 - maxX, maxZ, maxY, 1 - minX
                    ));
                });
                return buffer[0];
            }
            case EAST: {
                VoxelShape[] buffer = new VoxelShape[] { VoxelShapes.empty() };
                shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                    buffer[0] = VoxelShapes.union(buffer[0], VoxelShapes.cuboid(
                            1 - maxZ, minY, minX, 1 - minZ, maxY, maxX
                    ));
                });
                return buffer[0];
            }
            default:
                return shape;
        }
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return state.get(OPEN) ? ROTATED_OPEN.get(state.get(FACING)) : ROTATED_CLOSED.get(state.get(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return state.get(OPEN) ? ROTATED_OPEN.get(state.get(FACING)) : ROTATED_CLOSED.get(state.get(FACING));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            boolean open = !state.get(OPEN);
            world.setBlockState(pos, state.with(OPEN, open));
            world.playSound(null, pos, open ? SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN : SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return ActionResult.SUCCESS;
    }
}