package net.dark.spv_addon.world.events.misc;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.dark.spv_addon.init.config.ServerConfig;
import net.dark.spv_addon.util.ServerTickScheduler;
import net.dark.spv_addon.world.transitions.SpbTransitionDirector;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sp.init.BackroomsLevels.*;
import static net.dark.spv_addon.init.BackroomsLevels.LEVELRUN_WORLD_KEY;

public class LevelRunGlobalTicker {
    private static final Logger LOGGER = LoggerFactory.getLogger(LevelRunGlobalTicker.class);
    private static final Random RANDOM = new Random();
    private static boolean levelRunUsed = false;
    private static boolean initialized = false;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        ServerTickEvents.END_SERVER_TICK.register(LevelRunGlobalTicker::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        // Skip if Level Run has already been used
        if (levelRunUsed) {
            return;
        }
        if (!ServerConfig.areLevelRunRandomTransitionsEnabled(server)) {
            return;
        }

        // Check all players for rare Level Run transition chance
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            RegistryKey<World> worldKey = player.getWorld().getRegistryKey();

            // Check if player is in a level eligible for Level Run transition
            if (isEligibleForLevelRunTransition(worldKey)) {
                checkForRareLevelRunTransition(server, player);
            }
        }
    }

    private static boolean isEligibleForLevelRunTransition(RegistryKey<World> worldKey) {
        return worldKey.equals(LEVEL1_WORLD_KEY) || worldKey.equals(LEVEL2_WORLD_KEY) ||
               worldKey.equals(com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY) || worldKey.equals(INFINITE_FIELD_WORLD_KEY);
    }

    private static void checkForRareLevelRunTransition(MinecraftServer server, ServerPlayerEntity triggerPlayer) {
        int chance = Math.max(100, ServerConfig.getLevelRunChance(server));
        if (RANDOM.nextInt(chance) == 0) {
            levelRunUsed = true;
            teleportAllPlayersToLevelRun(server, triggerPlayer);
        }
    }


    private static void teleportAllPlayersToLevelRun(MinecraftServer server, ServerPlayerEntity triggerPlayer) {
        ServerWorld runWorld = server.getWorld(LEVELRUN_WORLD_KEY);
        if (runWorld == null) {
            LOGGER.warn("Level RUN world not found after trigger from {}", triggerPlayer.getEntityName());
            return;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            int teleportDelay = SpbTransitionDirector.beginDirectTransition(
                    player,
                    SpbTransitionDirector.TransitionProfile.runEscape()
            );

            ServerTickScheduler.schedule(teleportDelay, () -> {
                double x = 7.5 + (RANDOM.nextDouble() - 0.5) * 20;
                double z = 7.5 + (RANDOM.nextDouble() - 0.5) * 20;
                player.teleport(runWorld, x, 1, z, player.getYaw(), player.getPitch());
                SpbTransitionDirector.completeDirectTransition(player);
            });
        }
    }

    public static void teleportAllPlayersToLevel(MinecraftServer server, RegistryKey<World> targetWorldKey, Vec3d spawnPos, String levelName) {
        ServerWorld targetWorld = server.getWorld(targetWorldKey);
        if (targetWorld == null) {
            LOGGER.warn("{} world not found for group teleport", levelName);
            return;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            int teleportDelay = SpbTransitionDirector.beginDirectTransition(
                    player,
                    SpbTransitionDirector.TransitionProfile.cinematicDefault()
            );

            ServerTickScheduler.schedule(teleportDelay, () -> {
                double x = spawnPos.x + (RANDOM.nextDouble() - 0.5) * 4;
                double y = spawnPos.y;
                double z = spawnPos.z + (RANDOM.nextDouble() - 0.5) * 4;

                player.teleport(targetWorld, x, y, z, player.getYaw(), player.getPitch());
                SpbTransitionDirector.completeDirectTransition(player);
            });
        }
    }

    public static boolean isLevelRunUsed() {
        return levelRunUsed;
    }

    public static void resetLevelRunUsage() {
        levelRunUsed = false;
    }
}
