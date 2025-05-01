package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;

public class SanityComponent implements ComponentV3, AutoSyncedComponent {
    private static final int MAX = 100;
    private int sanity = MAX;
    private final PlayerEntity player;

    public SanityComponent(PlayerEntity player) {
        this.player = player;
    }

    public int getSanity() {
        return sanity;
    }

    public void setSanity(int s) {
        this.sanity = Math.max(0, Math.min(MAX, s));
    }

    public void addSanity(int amount) {
        setSanity(sanity + amount);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        sanity = tag.getInt("sanity");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("sanity", sanity);
    }
}
