// net.dark.spv_addon.client.ClientSanityDrain.java

package net.dark.spv_addon.client;

import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.minecraft.client.MinecraftClient;

public class ClientSanityDrain {
    private static int tickCounter = 0;

    public static void tick() {
        if (++tickCounter >= 200) { // 10s @ 20tps
            tickCounter = 0;
            var client = MinecraftClient.getInstance();
            if (client.player != null && client.world != null) {
                boolean near = SanityLightTracker.isNearAnyLight(client.player.getPos(), 8.0);
                if (!near) {
                    SanityComponent sanity = InitializeComponents.SANITY.get(client.player);
                    sanity.drain(1);
                }
            }
        }
    }
}
