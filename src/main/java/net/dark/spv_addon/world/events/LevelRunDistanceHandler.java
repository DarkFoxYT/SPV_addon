package net.dark.spv_addon.world.events;

import net.dark.spv_addon.init.BackroomsLevels;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Random;

public class LevelRunDistanceHandler {
    private static Double globalStartX = null;
    private static Integer globalTargetDistance = null;
    private static final Random random = new Random();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(LevelRunDistanceHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        ServerWorld runWorld = server.getWorld(BackroomsLevels.LEVELRUN_WORLD_KEY);
        if (runWorld == null) return;

        if (!runWorld.getPlayers().isEmpty() && globalStartX == null) {
            globalStartX = runWorld.getPlayers().get(0).getX();
            globalTargetDistance = 100 + random.nextInt(401);
        }

        if (globalStartX == null || globalTargetDistance == null) return;

        boolean reached = false;
        for (ServerPlayerEntity player : runWorld.getPlayers()) {
            double walked = Math.abs(player.getX() - globalStartX);
            if (walked >= globalTargetDistance) {
                reached = true;
                break;
            }
        }

        if (reached) {
            ServerWorld nextWorld = server.getWorld(BackroomsLevels.LEVEL207_WORLD_KEY);
            if (nextWorld != null) {
                for (ServerPlayerEntity player : runWorld.getPlayers()) {
                    player.teleport(nextWorld, 16, 66, 16, player.getYaw(), player.getPitch());
                }
            }
            globalStartX = null;
            globalTargetDistance = null;
        }

        if (runWorld.getPlayers().isEmpty()) {
            globalStartX = null;
            globalTargetDistance = null;
        }
    }
}