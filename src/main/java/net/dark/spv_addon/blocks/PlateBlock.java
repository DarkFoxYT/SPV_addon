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

            // Item frame behavior: place item if holding one and plate is empty
            if (plate.getItem().isEmpty() && !heldItem.isEmpty()) {
                // Take one item from the stack and place it in the plate
                ItemStack itemToPlace = heldItem.copy();
                itemToPlace.setCount(1);
                plate.setItem(itemToPlace);

                // Remove one item from player's hand
                heldItem.decrement(1);

                // Play placement sound (similar to item frame)
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0f, 1.0f);
                plate.markDirty();
                return ActionResult.SUCCESS;

            // Item frame behavior: retrieve item if plate has one and hand is empty or same item
            } else if (!plate.getItem().isEmpty()) {
                ItemStack storedItem = plate.removeItem(player);

                // Give the stored item back to the player
                if (!player.giveItemStack(storedItem)) {
                    // If inventory is full, drop the item
                    player.dropItem(storedItem, false);
                }

                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.SUCCESS;
    }
}