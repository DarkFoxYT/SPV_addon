package net.dark.spv_addon.Additions.battery;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlashlightBatteryEvents {
    public static void register() {
        final Map<UUID, Integer> tickCounter = new HashMap<>();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PlayerComponent comp = InitializeComponents.PLAYER.get(player);
                UUID id = player.getUuid();

                if (comp == null || !comp.isFlashLightOn()) {
                    tickCounter.remove(id);
                    continue;
                }

                int ticks = tickCounter.getOrDefault(id, 0) + 1;
                if (ticks >= 200) { // every 5 seconds
                    if (BatteryManager.isBatteryEnabled()) {
                        // Use enhanced battery drain with player context
                        BatteryManager.drainBattery(id, 1, player);
                    }
                    tickCounter.put(id, 0);
                } else {
                    tickCounter.put(id, ticks);
                }
            }
        });
    }
}
