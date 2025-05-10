package net.dark.spv_addon.Additions.Sanity;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.dark.spv_addon.cca.SanityComponent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3dc;

import java.util.*;

public class SanityDrainEvents {

    private static final int TICK_INTERVAL = 200; // 10 seconds at 20 ticks/sec
    private static final double MAX_DISTANCE = 10.0; // max distance to light

    public static void register() {
        Map<UUID, Integer> tickCounter = new HashMap<>();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PlayerComponent pComp = InitializeComponents.PLAYER.get(player);
                SanityComponent sComp = net.dark.spv_addon.cca.InitializeComponents.SANITY.get(player);
                UUID id = player.getUuid();

                if (pComp == null || sComp == null) continue;

                boolean isFlashlightOn = pComp.isFlashLightOn();
                Vec3d playerPos = player.getPos();
                boolean nearLight = isNearPointLight(playerPos);

                if (!isFlashlightOn && !nearLight) {
                    int ticks = tickCounter.getOrDefault(id, 0) + 1;
                    if (ticks >= TICK_INTERVAL) {
                        sComp.drain(1);
                        tickCounter.put(id, 0);
                    } else {
                        tickCounter.put(id, ticks);
                    }
                } else {
                    tickCounter.remove(id);
                }
            }
        });
    }

    private static boolean isNearPointLight(Vec3d playerPos) {
        List<PointLight> pointLights = SanityLightStore.getActiveLights(); // your mixin-accessible store
        for (PointLight light : pointLights) {
            if (light.getPosition().distance((Vector3dc) playerPos) <= MAX_DISTANCE) {
                return true;
            }
        }
        return false;
    }
}
