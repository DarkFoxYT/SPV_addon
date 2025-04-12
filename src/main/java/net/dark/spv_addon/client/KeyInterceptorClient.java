// File: net/dark/spv_addon/client/KeyInterceptorClient.java

package net.dark.spv_addon.client;

import com.sp.Keybinds;
import net.dark.spv_addon.battery.BatteryManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.UUID;

public class KeyInterceptorClient implements ClientModInitializer {

    private final HashMap<UUID, Integer> lastSentLevel = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            UUID uuid = client.player.getUuid();
            int battery = BatteryManager.getBattery(uuid);

            if (battery <= 0 && Keybinds.toggleFlashlight.wasPressed()) {
                Keybinds.toggleFlashlight.setPressed(false);
            }

            int last = lastSentLevel.getOrDefault(uuid, -1);
            if (battery != last) {
                client.player.sendMessage(net.minecraft.text.Text.literal("Battery now at " + battery + "%"), true);
                lastSentLevel.put(uuid, battery);
            }
        });
    }
}
