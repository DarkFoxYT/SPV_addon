package net.dark.spv_addon.blocks.entities;

import net.dark.spv_addon.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class TapeRecorderBlockEntity extends BlockEntity {
    private ItemStack tape = ItemStack.EMPTY;

    public TapeRecorderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TAPE_RECORDER, pos, state);
    }

    public boolean hasTape() {
        return !tape.isEmpty();
    }

    public void insertTape(ItemStack stack) {
        this.tape = stack;
        markDirty();
    }

    public ItemStack removeTape() {
        ItemStack out = tape;
        tape = ItemStack.EMPTY;
        markDirty();
        return out;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        tape = ItemStack.fromNbt(nbt.getCompound("Tape"));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!tape.isEmpty()) nbt.put("Tape", tape.writeNbt(new NbtCompound()));
    }
}