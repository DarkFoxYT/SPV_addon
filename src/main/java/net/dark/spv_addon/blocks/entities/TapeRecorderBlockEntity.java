package net.dark.spv_addon.blocks.entities;

import net.dark.spv_addon.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class TapeRecorderBlockEntity extends BlockEntity {
    private ItemStack tape = ItemStack.EMPTY;
    private boolean isPlaying = false;
    private int playingTimer = 0;
    private int totalPlayTime = 0;

    public TapeRecorderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TAPE_RECORDER, pos, state);
    }

    public boolean hasTape() {
        return !tape.isEmpty();
    }

    public void insertTape(ItemStack stack) {
        this.tape = stack;

        // Set play time based on tape type
        if (stack.getItem() instanceof net.dark.spv_addon.items.custom.TapeItem tapeItem) {
            // Get tape duration - different tapes have different lengths
            this.totalPlayTime = getTapeDuration(tapeItem);
            this.playingTimer = 0;
        }

        markDirty();
    }

    public ItemStack removeTape() {
        // Check if tape can be removed (only if not playing or finished playing)
        if (isPlaying && playingTimer < totalPlayTime) {
            return ItemStack.EMPTY; // Cannot remove tape while still playing
        }

        ItemStack out = tape;
        tape = ItemStack.EMPTY;

        // Stop any playing sounds when tape is removed
        if (isPlaying) {
            stopPlayingSound();
            isPlaying = false;
        }

        // Reset timers
        playingTimer = 0;
        totalPlayTime = 0;

        markDirty();
        return out;
    }

    /**
     * Check if the tape can be removed (finished playing or not playing)
     */
    public boolean canRemoveTape() {
        if (!hasTape()) {
            return false;
        }
        return !isPlaying || playingTimer >= totalPlayTime;
    }

    /**
     * Get remaining play time in ticks
     */
    public int getRemainingPlayTime() {
        if (!isPlaying || totalPlayTime == 0) {
            return 0;
        }
        return Math.max(0, totalPlayTime - playingTimer);
    }

    /**
     * Get remaining play time in seconds
     */
    public int getRemainingPlayTimeSeconds() {
        return getRemainingPlayTime() / 20; // Convert ticks to seconds
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
        if (playing) {
            playingTimer = 0; // Reset timer when starting to play
        }
        markDirty();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Get tape duration in ticks based on tape type
     */
    private int getTapeDuration(net.dark.spv_addon.items.custom.TapeItem tapeItem) {
        // Get the sound event from the tape
        net.minecraft.sound.SoundEvent soundEvent = tapeItem.getSound();

        // Set durations based on tape type/sound
        // You can customize these based on your actual sound lengths
        if (soundEvent == net.dark.spv_addon.init.ModSounds.TAPE1) {
            return 20 * 45; // 45 seconds for TAPE1
        }

        // Add more tape types here as you create them:
        /*
        else if (soundEvent == ModSounds.TAPE2) {
            return 20 * 90; // 90 seconds for TAPE2
        }
        else if (soundEvent == ModSounds.TAPE3) {
            return 20 * 120; // 2 minutes for TAPE3
        }
        */

        // Default duration for unknown tapes
        return 20 * 60; // Default: 60 seconds (1200 ticks)
    }

    /**
     * Tick method to update playing timer
     */
    public void tick() {
        if (isPlaying && totalPlayTime > 0) {
            playingTimer++;

            // Check if tape finished playing
            if (playingTimer >= totalPlayTime) {
                isPlaying = false;
                // Tape finished, can now be removed
                markDirty();
            }
        }
    }

    /**
     * Stop all sounds at this position
     */
    private void stopPlayingSound() {
        if (world != null && world.isClient) {
            try {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.getSoundManager() != null) {
                    // Stop all sounds at this block position
                    client.getSoundManager().stopSounds(null, SoundCategory.BLOCKS);
                }
            } catch (Exception e) {
                // Silently handle any sound stopping errors
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        tape = ItemStack.fromNbt(nbt.getCompound("Tape"));
        isPlaying = nbt.getBoolean("IsPlaying");
        playingTimer = nbt.getInt("PlayingTimer");
        totalPlayTime = nbt.getInt("TotalPlayTime");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!tape.isEmpty()) nbt.put("Tape", tape.writeNbt(new NbtCompound()));
        nbt.putBoolean("IsPlaying", isPlaying);
        nbt.putInt("PlayingTimer", playingTimer);
        nbt.putInt("TotalPlayTime", totalPlayTime);
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        // Stop sounds when block entity is removed
        if (isPlaying) {
            stopPlayingSound();
        }
    }
}