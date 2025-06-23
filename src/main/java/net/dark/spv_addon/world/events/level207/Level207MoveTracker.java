package net.dark.spv_addon.world.events.level207;


import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.levels.custom.Level207BackroomsLevel;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Classe utilitaire pour tracker les déplacements des joueurs dans le Level 207
public class Level207MoveTracker {
    private static final Map<UUID, Vec3d> lastPositions = new HashMap<>();

    public static void register(Level207BackroomsLevel level207) {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (!player.getWorld().getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY)) continue;
                Vec3d current = player.getPos();
                Vec3d last = lastPositions.get(player.getUuid());
                if (last == null) {
                    lastPositions.put(player.getUuid(), current);
                    continue;
                }
                if (!current.equals(last)) {
                    level207.onPlayerMove(player, last, current);
                    lastPositions.put(player.getUuid(), current);
                }
            }
        });
    }
}