package net.dark.spv_addon.blocks;

import net.dark.spv_addon.init.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Kittylamp extends BlockWithEntity {
    public static final BooleanProperty STOPPED = BooleanProperty.of("stopped");

    public Kittylamp(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(STOPPED, false));
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KittyLampEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.KITTY_LAMP, (world1, pos, state1, blockEntity) -> blockEntity.tick(world, pos, state));
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{STOPPED});
    }
}
