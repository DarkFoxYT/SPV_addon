package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;

public class RunTimerComponent implements AutoSyncedComponent {
    private int ticksSpentInBackrooms = 0;
    private int noclipThreshold = 12000; // 10 min (12000 ticks, 20 ticks = 1 sec)
    private boolean active = false;

    @Override
    public void readFromNbt(NbtCompound tag) {
        ticksSpentInBackrooms = tag.getInt("TicksSpentInBackrooms");
        active = tag.getBoolean("Active");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("TicksSpentInBackrooms", ticksSpentInBackrooms);
        tag.putBoolean("Active", active);
    }

    public boolean isActive() {
        return active;
    }
    public void tick() {
        if (!active) return;
        ticksSpentInBackrooms++;
    }

    public boolean shouldNoclip() {
        return active && ticksSpentInBackrooms >= noclipThreshold;
    }

    public void reset() {
        ticksSpentInBackrooms = 0;
        active = false;
    }

    public void activate(int minTicks, int maxTicks) {
        noclipThreshold = minTicks + (int)(Math.random() * (maxTicks - minTicks));
        active = true;
    }
}
