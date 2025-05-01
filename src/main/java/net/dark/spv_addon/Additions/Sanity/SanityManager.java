package net.dark.spv_addon.Additions.Sanity;

import net.dark.spv_addon.Additions.Sanity.AggroTracker;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.cca.ThirstComponent;
import net.dark.spv_addon.battery.BatteryManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

import java.util.List;
import java.util.stream.Collectors;

public class SanityManager {
    public static boolean enabled = true;

    private static final int INTERVAL = 20; // every second
    private static int tick = 0;

    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(SanityManager::onTick);
    }

    private static void onTick(MinecraftServer server) {
        if (!enabled) return;
        if (++tick < INTERVAL) return;
        tick = 0;

        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            tickPlayer(server, p);
        }
    }

    private static void tickPlayer(MinecraftServer server, ServerPlayerEntity p) {
        SanityComponent sc = InitializeComponents.SANITY.get(p);
        ThirstComponent tc = InitializeComponents.THIRST.get(p);
        int s = sc.getSanity();
        int drain = 0;

        // 1) darkness
        BlockPos pos = p.getBlockPos();
        int light = p.getWorld().getLightLevel(LightType.BLOCK, pos);
        if (light == 0)       drain += 2;
        else if (light <= 3)  drain += 1;

        // 2) low thirst
        if (tc.getThirst() <= 25) drain += 1;

        // 3) low battery
        if (BatteryManager.getBattery(p.getUuid()) <= 10) drain += 1;

        // 4) mob aggro
        if (AggroTracker.isAgroed(p)) {
            drain += 3;
            AggroTracker.clear(p);
        }

        s = MathHelper.clamp(s - drain, 0, 100);

        // 5) regenerate up to 50 when near other players
        List<ServerPlayerEntity> nearby = server.getPlayerManager().getPlayerList().stream()
                .filter(o -> o != p && o.squaredDistanceTo(p) < 100)
                .collect(Collectors.toList());
        if (!nearby.isEmpty() && s < 50) {
            s = MathHelper.clamp(s + nearby.size(), 0, 50);
        }

        sc.setSanity(s);

        // 6) if zero, apply damage
        if (s == 0) {
            return;
        }
    }

    /** Call from your mob-target Mixin hook. */
    public static void onPlayerAggro(ServerPlayerEntity p) {
        AggroTracker.mark(p);
    }
}
