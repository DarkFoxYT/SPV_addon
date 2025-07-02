package net.dark.spv_addon.blocks;

import net.dark.spv_addon.blocks.entities.PlateBlockEntity;
import net.dark.spv_addon.init.helper.CollisionShapeHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PlateBlock extends Block implements BlockEntityProvider {
    private static final VoxelShape SHAPE = CollisionShapeHelper.loadUnrotatedCollisionFromModelJson("spv_addon", "plate1");

    public PlateBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlateBlockEntity(pos, state);
    }

    public boolean hasBlockEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                              Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            PlateBlockEntity plate = (PlateBlockEntity) world.getBlockEntity(pos);
            if (plate == null) return ActionResult.PASS;

            ItemStack heldItem = player.getStackInHand(hand);
            if (plate.getItem().isEmpty() && !heldItem.isEmpty()) {
                plate.setItem(heldItem.split(1));
                world.playSound(null, pos, SoundEvents.BLOCK_AZALEA_LEAVES_PLACE, SoundCategory.BLOCKS, 0.8f, 1f);
                plate.markDirty();
                return ActionResult.SUCCESS;
            } else if (!plate.getItem().isEmpty()) {
                player.giveItemStack(plate.getItem());
                plate.setItem(ItemStack.EMPTY);
                world.playSound(null, pos, SoundEvents.BLOCK_AZALEA_LEAVES_BREAK, SoundCategory.BLOCKS, 0.8f, 1f);
                plate.markDirty();
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.SUCCESS;
    }
}