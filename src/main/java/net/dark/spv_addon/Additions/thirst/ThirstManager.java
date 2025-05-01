// File: net/dark/spv_addon/thirst/ThirstManager.java
package net.dark.spv_addon.Additions.thirst;

import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.ThirstComponent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class ThirstManager {
    /** global on/off switch (can be flipped later via your command or ModMenu) */
    public static boolean enabled = true;

    private static final int INTERVAL_TICKS = 20 * 10; // every 10s
    private static int tickCounter = 0;

    /** Call once in your ModInitializer */
    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(ThirstManager::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        if (!enabled) return;
        if (++tickCounter < INTERVAL_TICKS) return;
        tickCounter = 0;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            tickPlayer(player);
        }
    }

    private static void tickPlayer(ServerPlayerEntity player) {
        ThirstComponent comp = InitializeComponents.THIRST.get(player);
        int thirst = comp.getThirst();

        // drain 2 if sprinting, else 1
        int drain = player.isSprinting() ? 2 : 1;
        thirst = MathHelper.clamp(thirst - drain, 0, 100);
        comp.setThirst(thirst);

        // apply Slowness I + Saturation I for next 10s if low
        if (thirst <= 25) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SLOWNESS, INTERVAL_TICKS, 0, true, false));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SATURATION, INTERVAL_TICKS, 0, true, false));
        }
    }

    /** Manual accessors (for your `/thirst set` command) */
    public static int getThirst(ServerPlayerEntity player) {
        return InitializeComponents.THIRST.get(player).getThirst();
    }
    public static void setThirst(ServerPlayerEntity player, int value) {
        InitializeComponents.THIRST.get(player).setThirst(value);
    }
}
