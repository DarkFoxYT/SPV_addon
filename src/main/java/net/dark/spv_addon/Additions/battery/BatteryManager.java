package net.dark.spv_addon.Additions.battery;

import com.sp.init.BackroomsLevels;
import net.dark.spv_addon.cca.FlashlightBatteryComponent;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.init.config.ServerConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BatteryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("BatteryManager");
    private static final Map<UUID, Integer> BATTERY_CACHE = new HashMap<>();
    private static final Map<UUID, Integer> BATTERY_HEALTH_CACHE = new HashMap<>();
    private static final Map<UUID, Long> BATTERY_CHANGING_CACHE = new HashMap<>();
    private static final Map<UUID, Long> LAST_DRAIN_TICK = new HashMap<>();
    private static final Random RANDOM = Random.create();

    private static final int MAX_BATTERY_HEALTH = 100;
    private static final int DRAIN_INTERVAL_TICKS = 20;
    private static final int BATTERY_CHANGING_DURATION_TICKS = 3 * 20;
    private static final float HEALTH_DEGRADATION_CHANCE = 0.001f;

    private static boolean batteryEnabled = true;

    private BatteryManager() {
    }

    public static int getBattery(UUID uuid) {
        return BATTERY_CACHE.getOrDefault(uuid, 100);
    }

    public static int getBattery(PlayerEntity player) {
        if (player == null) {
            return 100;
        }
        FlashlightBatteryComponent component = getComponent(player);
        if (component != null) {
            cache(player.getUuid(), component);
            return component.getBatteryLevel();
        }
        return getBattery(player.getUuid());
    }

    public static void setBattery(UUID uuid, int value) {
        BATTERY_CACHE.put(uuid, clampPercentage(value));
    }

    public static void setBattery(PlayerEntity player, int value) {
        if (player == null) {
            return;
        }
        int clamped = clampPercentage(value);
        FlashlightBatteryComponent component = getComponent(player);
        if (component != null) {
            component.setBatteryLevel(clamped);
        }
        setBattery(player.getUuid(), clamped);
    }

    public static int getBatteryHealth(UUID uuid) {
        return BATTERY_HEALTH_CACHE.getOrDefault(uuid, MAX_BATTERY_HEALTH);
    }

    public static int getBatteryHealth(PlayerEntity player) {
        if (player == null) {
            return MAX_BATTERY_HEALTH;
        }
        FlashlightBatteryComponent component = getComponent(player);
        if (component != null) {
            cache(player.getUuid(), component);
            return component.getBatteryHealth();
        }
        return getBatteryHealth(player.getUuid());
    }

    public static void setBatteryHealth(UUID uuid, int health) {
        BATTERY_HEALTH_CACHE.put(uuid, clampPercentage(health));
    }

    public static void setBatteryHealth(PlayerEntity player, int health) {
        if (player == null) {
            return;
        }
        int clamped = clampPercentage(health);
        FlashlightBatteryComponent component = getComponent(player);
        if (component != null) {
            component.setBatteryHealth(clamped);
        }
        setBatteryHealth(player.getUuid(), clamped);
    }

    public static boolean isBatteryEnabledForPlayer(PlayerEntity player, MinecraftServer server) {
        if (!batteryEnabled || !ServerConfig.isBatterySystemEnabled(server)) {
            return false;
        }

        if (player instanceof ServerPlayerEntity serverPlayer
                && serverPlayer.getServerWorld().getRegistryKey().getValue().equals(BackroomsLevels.POOLROOMS_WORLD_KEY.getValue())) {
            return false;
        }

        return true;
    }

    public static boolean isBatteryEnabledForPlayer(PlayerEntity player) {
        return isBatteryEnabledForPlayer(player, null);
    }

    public static void drainBattery(UUID uuid, int amount) {
        int effectiveDrain = Math.max(1, amount);
        setBattery(uuid, Math.max(0, getBattery(uuid) - effectiveDrain));
    }

    public static void drainBattery(PlayerEntity player, int amount) {
        drainBattery(player, amount, player.getWorld().getServer());
    }

    public static void drainBattery(PlayerEntity player, int amount, MinecraftServer server) {
        if (player == null || !batteryEnabled || !isBatteryEnabledForPlayer(player, server)) {
            return;
        }

        long currentTick = player.getWorld().getTime();
        Long lastDrain = LAST_DRAIN_TICK.get(player.getUuid());
        if (lastDrain != null && currentTick - lastDrain < DRAIN_INTERVAL_TICKS) {
            return;
        }
        LAST_DRAIN_TICK.put(player.getUuid(), currentTick);

        int health = getBatteryHealth(player);
        float healthMultiplier = Math.max(0.2f, health / (float) MAX_BATTERY_HEALTH);
        float drainRateMultiplier = server != null ? ServerConfig.getBatteryDrainRate(server) : 1.0f;
        int effectiveDrain = Math.max(1, Math.round(amount * drainRateMultiplier / healthMultiplier));
        int newValue = Math.max(0, getBattery(player) - effectiveDrain);
        setBattery(player, newValue);

        if (RANDOM.nextFloat() < HEALTH_DEGRADATION_CHANCE) {
            setBatteryHealth(player, Math.max(20, health - 1));
        }

        applyBatteryEffects(player.getUuid(), newValue, player);
    }

    private static void applyBatteryEffects(UUID uuid, int batteryLevel, PlayerEntity player) {
        if (player == null) {
            return;
        }

        try {
            if (batteryLevel <= 15) {
                cache(uuid, getComponent(player));
            }
            if (batteryLevel <= 5 && batteryLevel > 0) {
                cache(uuid, getComponent(player));
            }
            if (batteryLevel <= 0) {
                cache(uuid, getComponent(player));
            }
        } catch (Exception e) {
            LOGGER.warn("Error applying battery effects: {}", e.getMessage());
        }
    }

    public static void rechargeBattery(UUID uuid, int amount) {
        int currentBattery = getBattery(uuid);
        int health = getBatteryHealth(uuid);
        int maxCapacity = (int) (100 * (health / (float) MAX_BATTERY_HEALTH));
        setBattery(uuid, Math.min(maxCapacity, currentBattery + amount));
    }

    public static void rechargeBattery(PlayerEntity player, int amount) {
        int currentBattery = getBattery(player);
        int health = getBatteryHealth(player);
        int maxCapacity = (int) (100 * (health / (float) MAX_BATTERY_HEALTH));
        setBattery(player, Math.min(maxCapacity, currentBattery + amount));
    }

    public static void repairBattery(UUID uuid, int healthAmount) {
        setBatteryHealth(uuid, Math.min(MAX_BATTERY_HEALTH, getBatteryHealth(uuid) + healthAmount));
    }

    public static void repairBattery(PlayerEntity player, int healthAmount) {
        setBatteryHealth(player, Math.min(MAX_BATTERY_HEALTH, getBatteryHealth(player) + healthAmount));
    }

    public static int getEffectiveBatteryCapacity(UUID uuid) {
        return (int) (100 * (getBatteryHealth(uuid) / (float) MAX_BATTERY_HEALTH));
    }

    public static int getEffectiveBatteryCapacity(PlayerEntity player) {
        return (int) (100 * (getBatteryHealth(player) / (float) MAX_BATTERY_HEALTH));
    }

    public static boolean isBatteryCritical(UUID uuid) {
        return getBattery(uuid) <= 15 || getBatteryHealth(uuid) <= 30;
    }

    public static boolean isBatteryCritical(PlayerEntity player) {
        return getBattery(player) <= 15 || getBatteryHealth(player) <= 30;
    }

    public static void startBatteryChanging(UUID uuid) {
        BATTERY_CHANGING_CACHE.put(uuid, (long) BATTERY_CHANGING_DURATION_TICKS);
    }

    public static void startBatteryChanging(PlayerEntity player) {
        if (player == null) {
            return;
        }
        long untilTick = player.getWorld().getTime() + BATTERY_CHANGING_DURATION_TICKS;
        FlashlightBatteryComponent component = getComponent(player);
        if (component != null) {
            component.setBatteryChangingUntilTick(untilTick);
        }
        BATTERY_CHANGING_CACHE.put(player.getUuid(), untilTick);
    }

    public static boolean isBatteryChanging(UUID uuid) {
        Long untilTick = BATTERY_CHANGING_CACHE.get(uuid);
        return untilTick != null && untilTick > 0;
    }

    public static boolean isBatteryChanging(PlayerEntity player) {
        FlashlightBatteryComponent component = getComponent(player);
        if (component != null) {
            cache(player.getUuid(), component);
            return component.getBatteryChangingUntilTick() > player.getWorld().getTime();
        }
        return false;
    }

    public static String getBatteryStatusText(UUID uuid) {
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

    public static String getBatteryStatusText(PlayerEntity player) {
        if (isBatteryChanging(player)) {
            return "CHANGING";
        }

        int battery = getBattery(player);
        int health = getBatteryHealth(player);
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

    private static FlashlightBatteryComponent getComponent(PlayerEntity player) {
        return player == null ? null : InitializeComponents.FLASHLIGHT_BATTERY.get(player);
    }

    private static void cache(UUID uuid, FlashlightBatteryComponent component) {
        if (component == null) {
            return;
        }
        BATTERY_CACHE.put(uuid, component.getBatteryLevel());
        BATTERY_HEALTH_CACHE.put(uuid, component.getBatteryHealth());
        BATTERY_CHANGING_CACHE.put(uuid, component.getBatteryChangingUntilTick());
    }

    private static int clampPercentage(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
