package net.dark.spv_addon.world.events.level207;

import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModSounds;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles Level 207 ambiance to ensure it starts immediately when players enter
 */
public class Level207AmbianceHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("Level207AmbianceHandler");
    private static final Map<UUID, Boolean> playersInLevel207 = new HashMap<>();
    private static final Map<UUID, Long> nextAmbienceTick = new HashMap<>();
    private static final int AMBIANCE_LOOP_INTERVAL = 440;
    private static long serverTick;

    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            checkPlayerDimension(newPlayer);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            serverTick++;
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkPlayerDimension(player);
            }
        });
        
        LOGGER.info("Level 207 ambiance handler registered");
    }
    
    /**
     * Check if player entered or left Level 207 and handle ambiance accordingly
     */
    public static void checkPlayerDimension(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        boolean isInLevel207 = player.getServerWorld().getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY);
        boolean wasInLevel207 = playersInLevel207.getOrDefault(playerId, false);
        
        if (isInLevel207 && !wasInLevel207) {
            startAmbianceForPlayer(player);
            playersInLevel207.put(playerId, true);
            nextAmbienceTick.put(playerId, serverTick + AMBIANCE_LOOP_INTERVAL);
        } else if (!isInLevel207 && wasInLevel207) {
            playersInLevel207.put(playerId, false);
            nextAmbienceTick.remove(playerId);
        } else if (isInLevel207) {
            long nextTick = nextAmbienceTick.getOrDefault(playerId, serverTick + AMBIANCE_LOOP_INTERVAL);
            if (serverTick >= nextTick) {
                playAmbience(player);
                nextAmbienceTick.put(playerId, serverTick + AMBIANCE_LOOP_INTERVAL);
            }
        }
    }
    
    /**
     * Start ambiance for a player who just entered Level 207
     */
    private static void startAmbianceForPlayer(ServerPlayerEntity player) {
        try {
            playAmbience(player);
            
        } catch (Exception e) {
            LOGGER.warn("Failed to start ambiance for player {}: {}", player.getName().getString(), e.getMessage());
        }
    }

    private static void playAmbience(ServerPlayerEntity player) {
        player.getServerWorld().playSound(
                null,
                player.getBlockPos(),
                ModSounds.LEVEL_207_AMBIANCE,
                SoundCategory.AMBIENT,
                1.0F,
                1.0F
        );
    }
    
    /**
     * Manually trigger ambiance check for a player (useful for teleportation)
     */
    public static void triggerAmbianceCheck(ServerPlayerEntity player) {
        checkPlayerDimension(player);
    }
    
    /**
     * Clean up player data when they disconnect
     */
    public static void onPlayerDisconnect(UUID playerId) {
        playersInLevel207.remove(playerId);
        nextAmbienceTick.remove(playerId);
    }
}
