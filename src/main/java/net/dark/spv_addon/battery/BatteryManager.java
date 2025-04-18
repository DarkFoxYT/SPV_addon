package net.dark.spv_addon.battery;

import java.util.HashMap;
import java.util.UUID;

public class BatteryManager {
    private static final HashMap<UUID, Integer> batteryLevels = new HashMap<>();
    private static boolean batteryEnabled = true;

    public static int getBattery(UUID uuid) {
        return batteryLevels.getOrDefault(uuid, 100);
    }

    public static void setBattery(UUID uuid, int value) {
        batteryLevels.put(uuid, Math.min(100, Math.max(0, value)));
    }

    public static void drainBattery(UUID uuid, int amount) {
        if (!batteryEnabled) return;
        int newValue = Math.max(0, getBattery(uuid) - amount);
        batteryLevels.put(uuid, newValue);
    }

    public static boolean isBatteryEnabled() {
        return batteryEnabled;
    }

    public static void setBatteryEnabled(boolean enabled) {
        batteryEnabled = enabled;
    }

}
