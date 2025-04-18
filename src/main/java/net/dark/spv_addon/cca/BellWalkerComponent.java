package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.minecraft.nbt.NbtCompound;

public class BellWalkerComponent implements AutoSyncedComponent {
    private final BellWalkerEntity entity;

    public BellWalkerComponent(BellWalkerEntity entity) {
        this.entity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag) { /* rien à persister */ }

    @Override
    public void writeToNbt(NbtCompound tag) { /* rien à persister */ }

    public void sync() {
        InitializeComponents.BELL_WALKER.sync(entity);
    }
}