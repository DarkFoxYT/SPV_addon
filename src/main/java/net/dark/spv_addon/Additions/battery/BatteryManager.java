package net.dark.spv_addon.Additions.battery;

import com.sp.init.BackroomsLevels;
import net.dark.spv_addon.init.config.ServerConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class BatteryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("BatteryManager");
    private static final HashMap<UUID, Integer> batteryLevels = new HashMap<>();
    private static final HashMap<UUID, Long> lastDrainTime = new HashMap<>();
    private static final HashMap<UUID, Integer> batteryHealth = new HashMap<>(); // Battery degradation
    private static final HashMap<UUID, Long> batteryChangingTime = new HashMap<>(); // Track when battery is being changed
    private static final Random random = Random.create();

    private static boolean batteryEnabled = true;

    // Battery changing duration (3 seconds)
    private static final long BATTERY_CHANGING_DURATION = 3000; // 3 seconds in milliseconds

    // Enhanced battery configuration
    private static final int MAX_BATTERY_HEALTH = 100;
    private static final int DRAIN_INTERVAL_MS = 1000; // 1 second
    private static final float HEALTH_DEGRADATION_CHANCE = 0.001f; // 0.1% chance per drain

    public static int getBattery(UUID uuid) {
        return batteryLevels.getOrDefault(uuid, 100);
    }

    public static void setBattery(UUID uuid, int value) {
        batteryLevels.put(uuid, Math.min(100, Math.max(0, value)));
    }

    /**
     * Get battery health (affects maximum capacity)
     */
    public static int getBatteryHealth(UUID uuid) {
        return batteryHealth.getOrDefault(uuid, MAX_BATTERY_HEALTH);
    }

    /**
     * Set battery health
     */
    public static void setBatteryHealth(UUID uuid, int health) {
        batteryHealth.put(uuid, Math.min(MAX_BATTERY_HEALTH, Math.max(0, health)));
    }

    /**
     * Check if battery is enabled for the player's current dimension
     */
    public static boolean isBatteryEnabledForPlayer(PlayerEntity player, MinecraftServer server) {
        if (!batteryEnabled || !ServerConfig.isBatterySystemEnabled(server)) return false;

        // Disable battery in poolrooms
        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (serverPlayer.getServerWorld().getRegistryKey().getValue().equals(BackroomsLevels.POOLROOMS_WORLD_KEY.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Legacy method for backward compatibility
     */
    public static boolean isBatteryEnabledForPlayer(PlayerEntity player) {
        return isBatteryEnabledForPlayer(player, null);
    }

    public static void drainBattery(UUID uuid, int amount) {
        drainBattery(uuid, amount, null, null);
    }

    public static void drainBattery(UUID uuid, int amount, PlayerEntity player) {
        drainBattery(uuid, amount, player, null);
    }

    /**
     * Enhanced battery drain with health degradation and poolrooms check
     */
    public static void drainBattery(UUID uuid, int amount, PlayerEntity player, MinecraftServer server) {
        if (!batteryEnabled) return;

        // Check if battery should be disabled for this player
        if (player != null && !isBatteryEnabledForPlayer(player, server)) {
            return;
        }

        // Throttle drain rate
        long currentTime = System.currentTimeMillis();
        Long lastDrain = lastDrainTime.get(uuid);
        if (lastDrain != null && currentTime - lastDrain < DRAIN_INTERVAL_MS) {
            return;
        }
        lastDrainTime.put(uuid, currentTime);

        // Calculate effective drain based on battery health and server settings
        int health = getBatteryHealth(uuid);
        float healthMultiplier = health / (float)MAX_BATTERY_HEALTH;
        float drainRateMultiplier = server != null ? ServerConfig.getBatteryDrainRate(server) : 1.0f;
        int effectiveDrain = Math.round(amount * drainRateMultiplier / healthMultiplier);

        // Apply drain
        int newValue = Math.max(0, getBattery(uuid) - effectiveDrain);
        batteryLevels.put(uuid, newValue);

        // Chance to degrade battery health
        if (random.nextFloat() < HEALTH_DEGRADATION_CHANCE) {
            int newHealth = Math.max(20, health - 1); // Minimum 20% health
            setBatteryHealth(uuid, newHealth);
            // Removed debug logging for production
        }

        // Special effects based on battery level
        applyBatteryEffects(uuid, newValue, health, player);
    }

    /**
     * Apply special effects based on battery level
     */
    private static void applyBatteryEffects(UUID uuid, int batteryLevel, int health, PlayerEntity player) {
        if (player == null) return;

        try {
            // Flickering at low battery
            if (batteryLevel <= 15) {
                // Handled by flashlight renderer
            }

            // Warning at very low battery
            if (batteryLevel <= 5 && batteryLevel > 0) {
                // Could add warning sounds here
            }

            // Battery death effects
            if (batteryLevel <= 0) {
                // Could add special effects when battery dies
            }

        } catch (Exception e) {
            LOGGER.warn("Error applying battery effects: " + e.getMessage());
        }
    }

    /**
     * Recharge battery (for future battery items)
     */
    public static void rechargeBattery(UUID uuid, int amount) {
        int currentBattery = getBattery(uuid);
        int health = getBatteryHealth(uuid);
        int maxCapacity = (int)(100 * (health / (float)MAX_BATTERY_HEALTH));

        int newValue = Math.min(maxCapacity, currentBattery + amount);
        batteryLevels.put(uuid, newValue);

        // Removed debug logging for production
    }

    /**
     * Repair battery health (for future repair items)
     */
    public static void repairBattery(UUID uuid, int healthAmount) {
        int currentHealth = getBatteryHealth(uuid);
        int newHealth = Math.min(MAX_BATTERY_HEALTH, currentHealth + healthAmount);
        setBatteryHealth(uuid, newHealth);

        // Removed debug logging for production
    }

    /**
     * Get effective battery capacity based on health
     */
    public static int getEffectiveBatteryCapacity(UUID uuid) {
        int health = getBatteryHealth(uuid);
        return (int)(100 * (health / (float)MAX_BATTERY_HEALTH));
    }

    /**
     * Check if battery is in critical condition
     */
    public static boolean isBatteryCritical(UUID uuid) {
        return getBattery(uuid) <= 15 || getBatteryHealth(uuid) <= 30;
    }

    /**
     * Start battery changing process
     */
    public static void startBatteryChanging(UUID uuid) {
        batteryChangingTime.put(uuid, System.currentTimeMillis());
        // Removed debug logging for production
    }

    /**
     * Check if battery is currently being changed
     */
    public static boolean isBatteryChanging(UUID uuid) {
        Long changingStartTime = batteryChangingTime.get(uuid);
        if (changingStartTime == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - changingStartTime > BATTERY_CHANGING_DURATION) {
            // Changing process finished
            batteryChangingTime.remove(uuid);
            return false;
        }

        return true;
    }

    /**
     * Get battery status text for UI
     */
    public static String getBatteryStatusText(UUID uuid) {
        // Check if battery is being changed first
        if (isBatteryChanging(uuid)) {
            return "CHANGING";
        }

        int battery = getBattery(uuid);
        int health = getBatteryHealth(uuid);

        if (battery <= 0) {
            return "DEAD";
        } else if (battery <= 5) {
            return "CRITICAL";
        } else if (battery <= 15) {
            return "LOW";
        } else if (health <= 30) {
            return "DEGRADED";
        } else {
            return "GOOD";
        }
    }

    public static boolean isBatteryEnabled() {
        return batteryEnabled;
    }

    public static void setBatteryEnabled(boolean enabled) {
        batteryEnabled = enabled;
    }
}
