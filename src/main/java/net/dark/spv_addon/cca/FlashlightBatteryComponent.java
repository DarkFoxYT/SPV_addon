package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class FlashlightBatteryComponent implements ComponentV3, AutoSyncedComponent {
    public static final ComponentKey<FlashlightBatteryComponent> KEY =
            ComponentRegistry.getOrCreate(new Identifier("spv_addon", "flashlight_battery"), FlashlightBatteryComponent.class);

    private final PlayerEntity player;
    private int batteryLevel = 100;
    private int batteryHealth = 100;
    private long batteryChangingUntilTick = 0L;

    public FlashlightBatteryComponent(PlayerEntity player) {
        this.player = player;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int level) {
        int clamped = Math.max(0, Math.min(level, 100));
        if (this.batteryLevel != clamped) {
            this.batteryLevel = clamped;
            InitializeComponents.FLASHLIGHT_BATTERY.sync(player);
        }
    }

    public void drain(int amount) {
        setBatteryLevel(batteryLevel - amount);
    }

    public int getBatteryHealth() {
        return batteryHealth;
    }

    public void setBatteryHealth(int health) {
        int clamped = Math.max(0, Math.min(health, 100));
        if (this.batteryHealth != clamped) {
            this.batteryHealth = clamped;
            InitializeComponents.FLASHLIGHT_BATTERY.sync(player);
        }
    }

    public long getBatteryChangingUntilTick() {
        return batteryChangingUntilTick;
    }

    public void setBatteryChangingUntilTick(long batteryChangingUntilTick) {
        if (this.batteryChangingUntilTick != batteryChangingUntilTick) {
            this.batteryChangingUntilTick = batteryChangingUntilTick;
            InitializeComponents.FLASHLIGHT_BATTERY.sync(player);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        batteryLevel = tag.getInt("battery");
        batteryHealth = tag.contains("battery_health") ? tag.getInt("battery_health") : 100;
        batteryChangingUntilTick = tag.getLong("battery_changing_until");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("battery", batteryLevel);
        tag.putInt("battery_health", batteryHealth);
        tag.putLong("battery_changing_until", batteryChangingUntilTick);
    }
}
