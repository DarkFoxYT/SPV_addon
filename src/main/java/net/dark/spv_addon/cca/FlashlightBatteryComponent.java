// File: net/dark/spv_addon/cca/FlashlightBatteryComponent.java

package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class FlashlightBatteryComponent implements ComponentV3, AutoSyncedComponent {
    public static final ComponentKey<FlashlightBatteryComponent> KEY =
            ComponentRegistry.getOrCreate(new Identifier("spv_addon", "flashlight_battery"), FlashlightBatteryComponent.class);

    private int batteryLevel = 100;

    public static void register(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(KEY, player -> new FlashlightBatteryComponent(), RespawnCopyStrategy.ALWAYS_COPY);
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int level) {
        this.batteryLevel = Math.max(0, Math.min(level, 100));
    }

    public void drain(int amount) {
        setBatteryLevel(batteryLevel - amount);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        batteryLevel = tag.getInt("battery");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("battery", batteryLevel);
    }
}
