package net.dark.spv_addon.world.events.misc;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

import static com.sp.init.BackroomsLevels.*;
import static net.dark.spv_addon.init.BackroomsLevels.LEVELRUN_WORLD_KEY;

public class LevelRunGlobalTicker {
    private static final boolean IS_DEV = false; // Disabled for production
    private static final Random RANDOM = new Random();
    private static boolean levelRunUsed = false;
    private static final int LEVEL_RUN_CHANCE = IS_DEV ? 1000 : 50000;

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(LevelRunGlobalTicker::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        // Skip if Level Run has already been used
        if (levelRunUsed) {
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
        if (RANDOM.nextInt(LEVEL_RUN_CHANCE) == 0) {
            if (IS_DEV) {
                System.out.println("[SPV_ADDON] Level Run triggered by " + triggerPlayer.getEntityName());
            }
            levelRunUsed = true;
            teleportAllPlayersToLevelRun(server, triggerPlayer);
        }
    }


    private static void teleportAllPlayersToLevelRun(MinecraftServer server, ServerPlayerEntity triggerPlayer) {
        if (IS_DEV) {
            System.out.println("[SPV_ADDON] Group teleporting all players to Level Run");
        }

        ServerWorld runWorld = server.getWorld(LEVELRUN_WORLD_KEY);
        if (runWorld == null) {
            System.err.println("[SPV_ADDON] Level Run world not found!");
            return;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            com.sp.SPBRevamped.sendBlackScreenPacket(player, 60, true, false);

            server.execute(() -> {
                double x = 7.5 + (RANDOM.nextDouble() - 0.5) * 20;
                double z = 7.5 + (RANDOM.nextDouble() - 0.5) * 20;
                player.teleport(runWorld, x, 1, z, player.getYaw(), player.getPitch());

                if (IS_DEV) {
                    System.out.println("[SPV_ADDON] Teleported " + player.getEntityName() + " to Level Run");
                }
            });
        }
    }

    public static void teleportAllPlayersToLevel(MinecraftServer server, RegistryKey<World> targetWorldKey, Vec3d spawnPos, String levelName) {
        if (IS_DEV) {
            System.out.println("[SPV_ADDON] Group teleporting all players to " + levelName);
        }

        ServerWorld targetWorld = server.getWorld(targetWorldKey);
        if (targetWorld == null) {
            System.err.println("[SPV_ADDON] " + levelName + " world not found!");
            return;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            com.sp.SPBRevamped.sendBlackScreenPacket(player, 60, true, false);

            server.execute(() -> {
                double x = spawnPos.x + (RANDOM.nextDouble() - 0.5) * 4;
                double y = spawnPos.y;
                double z = spawnPos.z + (RANDOM.nextDouble() - 0.5) * 4;

                player.teleport(targetWorld, x, y, z, player.getYaw(), player.getPitch());

                if (IS_DEV) {
                    System.out.println("[SPV_ADDON] Teleported " + player.getEntityName() + " to " + levelName);
                }
            });
        }
    }

    public static boolean isLevelRunUsed() {
        return levelRunUsed;
    }

    public static void resetLevelRunUsage() {
        levelRunUsed = false;
        if (IS_DEV) {
            System.out.println("[SPV_ADDON] Level Run usage reset");
        }
    }
}