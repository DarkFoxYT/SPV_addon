package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class SanityComponent implements ComponentV3, AutoSyncedComponent {

    private final PlayerEntity player;
    private int sanityLevel = 100;

    public SanityComponent(PlayerEntity player) {
        this.player = player;
    }

    public int getSanityLevel() {
        return sanityLevel;
    }

    public void setSanityLevel(int level) {
        this.sanityLevel = Math.max(0, Math.min(level, 100));
        InitializeComponents.SANITY.sync(player);  // ✅ FIXED
    }

    public void decreaseSanity(int amount) {
        setSanityLevel(this.sanityLevel - amount);
    }

    public void increaseSanity(int amount) {
        setSanityLevel(this.sanityLevel + amount);
    }

    public void resetSanity() {
        setSanityLevel(100);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.sanityLevel = tag.getInt("sanity");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("sanity", this.sanityLevel);
    }
}
