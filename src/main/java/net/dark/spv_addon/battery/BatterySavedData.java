package net.dark.spv_addon.battery;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BatterySavedData extends PersistentState {
    private static final String BATTERY_TAG = "BatteryLevels";
    private final Map<String, Integer> levels = new HashMap<>();

    public BatterySavedData(NbtCompound nbt) {
        NbtCompound map = nbt.getCompound(BATTERY_TAG);
        for (String key : map.getKeys()) {
            levels.put(key, map.getInt(key));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound map = new NbtCompound();
        levels.forEach((uuid, lvl) -> map.putInt(uuid, lvl));
        nbt.put(BATTERY_TAG, map);
        return nbt;
    }

    public int get(UUID id) {
        return levels.getOrDefault(id.toString(), 100);
    }

    public void set(UUID id, int level) {
        levels.put(id.toString(), Math.min(100, Math.max(0, level)));
        markDirty();
    }
}
