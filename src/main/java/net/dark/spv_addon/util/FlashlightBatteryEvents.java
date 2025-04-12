package net.dark.spv_addon.util;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.dark.spv_addon.battery.BatteryManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class FlashlightBatteryEvents {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PlayerComponent component = InitializeComponents.PLAYER.get(player);

                if (component.isFlashLightOn()) {
                    BatteryManager.drainBattery(player.getUuid(), 1);
                    if (BatteryManager.getBattery(player.getUuid()) <= 0) {
                        component.setFlashLightOn(false);
                    }
                }
            }
        });
    }
}
