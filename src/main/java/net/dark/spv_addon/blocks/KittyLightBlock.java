
package net.dark.spv_addon.blocks;

import net.dark.spv_addon.init.ModBlockEntities;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class KittyLightBlock extends BlockWithEntity {
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final BooleanProperty COPY = BooleanProperty.of("copy");
    public static final BooleanProperty BLACKOUT = BooleanProperty.of("blackout");

    public KittyLightBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.getDefaultState().with(BLACKOUT, false)).with(ON, true)).with(COPY, false));
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(BLACKOUT, false)).with(ON, true)).with(COPY, false);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return !(Boolean)state.get(BLACKOUT) && (Boolean)state.get(ON) ? BlockRenderType.INVISIBLE : BlockRenderType.MODEL;
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.KITTY_LIGHT_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KittyLightBlockEntity(pos, state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ON, COPY, BLACKOUT});
    }
}
