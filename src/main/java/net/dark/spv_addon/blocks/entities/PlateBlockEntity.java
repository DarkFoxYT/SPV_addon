package net.dark.spv_addon.blocks.entities;

import net.dark.spv_addon.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * PlateBlockEntity - Acts like an item frame but as a block
 * Stores items without displaying them visually
 */
public class PlateBlockEntity extends BlockEntity {

    private ItemStack item = ItemStack.EMPTY;

    public PlateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLATE_BLOCK_ENTITY, pos, state);
    }

    /**
     * Sets the item stored in this plate
     */
    public void setItem(ItemStack stack) {
        this.item = stack;
        markDirty();
        // Sync to client if in a world
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    /**
     * Gets the item stored in this plate
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Removes and returns the stored item
     */
    public ItemStack removeItem(PlayerEntity player) {
        ItemStack result = this.item;
        this.item = ItemStack.EMPTY;
        markDirty();

        // Play sound effect
        if (world != null && !world.isClient) {
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM,
                    SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }

        return result;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!item.isEmpty()) {
            NbtCompound itemNbt = new NbtCompound();
            item.writeNbt(itemNbt);
            nbt.put("Item", itemNbt);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Item")) {
            this.item = ItemStack.fromNbt(nbt.getCompound("Item"));
        } else {
            this.item = ItemStack.EMPTY;
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
