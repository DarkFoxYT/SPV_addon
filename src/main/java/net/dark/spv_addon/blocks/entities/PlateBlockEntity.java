package net.dark.spv_addon.blocks.entities;

import net.dark.spv_addon.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlateBlockEntity extends BlockEntity {

    private ItemStack item = ItemStack.EMPTY;

    public PlateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLATE_BLOCK_ENTITY, pos, state);
    }

    public void setItem(ItemStack stack) {
        this.item = stack;
        markDirty();
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtCompound itemNbt = new NbtCompound();
        item.writeNbt(itemNbt);
        nbt.put("Item", itemNbt);
    }


    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlateBlockEntity(pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.item = ItemStack.fromNbt(nbt.getCompound("Item"));
    }
}
