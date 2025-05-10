package net.dark.spv_addon.Additions.Sanity;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.dark.spv_addon.battery.BatteryManager;
import net.dark.spv_addon.cca.SanityComponent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SanityHandler {
    private static final Map<UUID, Integer> tickCounter = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID id = player.getUuid();
                PlayerComponent pc = InitializeComponents.PLAYER.get(player);
                SanityComponent sanity = net.dark.spv_addon.cca.InitializeComponents.SANITY.get(player);

                if (pc == null || sanity == null) continue;

                // Skip if flashlight is on and battery is above 10%
                if (pc.isFlashLightOn() && BatteryManager.getBattery(id) > 10) continue;

                // Drain sanity every 5 seconds
                int ticks = tickCounter.getOrDefault(id, 0) + 1;
                if (ticks >= 100) {
                    sanity.drain(1); // drain 1 sanity
                    tickCounter.put(id, 0);
                } else {
                    tickCounter.put(id, ticks);
                }
            }
        });
    }
}
