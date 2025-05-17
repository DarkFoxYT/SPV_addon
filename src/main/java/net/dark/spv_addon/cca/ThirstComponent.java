package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class ThirstComponent implements ComponentV3, AutoSyncedComponent {
    private static final int MAX_THIRST = 100;
    private int thirst = MAX_THIRST;
    private final PlayerEntity player;

    public ThirstComponent(PlayerEntity player) {
        this.player = player;
    }

    public int getThirst() {
        return thirst;
    }

    public void setThirst(int thirst) {
        this.thirst = Math.max(0, Math.min(MAX_THIRST, thirst));
        InitializeComponents.THIRST.sync(player);
    }

    public void addThirst(int amount) {
        setThirst(thirst + amount);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        thirst = tag.getInt("thirst");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("thirst", thirst);
    }
}
