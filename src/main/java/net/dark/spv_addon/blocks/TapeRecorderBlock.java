package net.dark.spv_addon.blocks;

import net.dark.spv_addon.items.custom.TapeItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.Map;

public class TapeRecorderBlock extends Block implements BlockEntityProvider {
    public static final BooleanProperty PLAYING = BooleanProperty.of("playing");
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    // Construction de la forme de collision à partir du modèle Blockbench
    private static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(3/16.0, -0.2/16.0, 2.8/16.0, 4/16.0, 2.8/16.0, 15/16.0),
            VoxelShapes.cuboid(12/16.0, -0.2/16.0, 2.8/16.0, 13/16.0, 2.8/16.0, 15/16.0),
            VoxelShapes.cuboid(4/16.0, -0.2/16.0, 2.9/16.0, 12/16.0, 0.8/16.0, 15/16.0),
            VoxelShapes.cuboid(4/16.0, 0.8/16.0, 10.9/16.0, 12/16.0, 2.9/16.0, 15/16.0),
            VoxelShapes.cuboid(4/16.0, 0.8/16.0, 2.8/16.0, 12/16.0, 2.9/16.0, 5/16.0),
            VoxelShapes.cuboid(3/16.0, -0.2/16.0, 2/16.0, 13/16.0, 2/16.0, 3/16.0),
            VoxelShapes.cuboid(3/16.0, 1.2/16.0, 1.8/16.0, 13/16.0, 2.4/16.0, 2.8/16.0),
            VoxelShapes.cuboid(11/16.0, 1/16.0, 2.1/16.0, 12/16.0, 3/16.0, 3.9/16.0),
            VoxelShapes.cuboid(9/16.0, 1/16.0, 2.1/16.0, 10/16.0, 3/16.0, 3.9/16.0),
            VoxelShapes.cuboid(6/16.0, 1/16.0, 2.1/16.0, 7/16.0, 3/16.0, 3.9/16.0),
            VoxelShapes.cuboid(4/16.0, 1/16.0, 2.1/16.0, 5/16.0, 3/16.0, 3.9/16.0),
            VoxelShapes.cuboid(3.5/16.0, 2.6/16.0, 5/16.0, 12.5/16.0, 3.1/16.0, 11.4/16.0),
            VoxelShapes.cuboid(4.05/16.0, -0.1/16.0, 5.25/16.0, 11.95/16.0, 0.7/16.0, 10.75/16.0)
    );

    // Gestion de la rotation comme CrossBlock
    private static final Map<Direction, VoxelShape> ROTATED_SHAPES = new EnumMap<>(Direction.class);
    static {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            ROTATED_SHAPES.put(dir, rotateShape(SHAPE, dir));
        }
    }

    public TapeRecorderBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(PLAYING, false)
                .with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PLAYING, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(PLAYING, false);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TapeRecorderBlockEntity(pos, state);
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction dir) {

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

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return ROTATED_SHAPES.get(state.get(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return ROTATED_SHAPES.get(state.get(FACING));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof TapeRecorderBlockEntity recorder)) return ActionResult.PASS;

        ItemStack held = player.getStackInHand(hand);
        if (recorder.hasTape()) {
            player.giveItemStack(recorder.removeTape());
            world.setBlockState(pos, state.with(PLAYING, false));
            return ActionResult.SUCCESS;
        }

        if (held.getItem() instanceof TapeItem tape) {
            ItemStack inserted = held.split(1);
            recorder.insertTape(inserted);
            SoundEvent sound = tape.getSound();
            world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.setBlockState(pos, state.with(PLAYING, true));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}