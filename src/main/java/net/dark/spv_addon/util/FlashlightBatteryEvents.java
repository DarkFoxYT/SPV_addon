package net.dark.spv_addon.util;

import com.sp.render.FlashlightRenderer;
import net.dark.spv_addon.battery.BatteryManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class FlashlightBatteryEvents {

    public static void register() {
        final HashMap<UUID, Integer> tickCounter = new HashMap<>();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID id = player.getUuid();
                int ticks = tickCounter.getOrDefault(id, 0) + 1;

                if (ticks >= 1200){
                        if (BatteryManager.isBatteryEnabled()) {
                            BatteryManager.drainBattery(id, 1);
                        }
                        tickCounter.put(id, 0);
                    } else {
                        tickCounter.put(id, ticks);
                    }
            }
        })
    ;}
}
