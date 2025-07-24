package net.dark.spv_addon.world.events.level207;

import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModSounds;
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
    private static final int AMBIANCE_LOOP_INTERVAL = 440;
    
    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            checkPlayerDimension(newPlayer);
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
            // Removed debug logging for production
        } else if (!isInLevel207 && wasInLevel207) {
            playersInLevel207.put(playerId, false);
            // Removed debug logging for production
        }
    }
    
    /**
     * Start ambiance for a player who just entered Level 207
     */
    private static void startAmbianceForPlayer(ServerPlayerEntity player) {
        try {
            player.getServerWorld().playSound(
                null,
                player.getBlockPos(),
                ModSounds.LEVEL_207_AMBIANCE,
                SoundCategory.AMBIENT,
                1.0F,
                1.0F
            );

            scheduleAmbianceLoop(player);
            
        } catch (Exception e) {
            LOGGER.warn("Failed to start ambiance for player {}: {}", player.getName().getString(), e.getMessage());
        }
    }
    
    /**
     * Schedule the ambiance to loop for the player
     */
    private static void scheduleAmbianceLoop(ServerPlayerEntity player) {
        player.getServer().execute(() -> {
            scheduleRepeatingAmbiance(player, 0);
        });
    }
    
    /**
     * Recursively schedule ambiance to play every interval
     */
    private static void scheduleRepeatingAmbiance(ServerPlayerEntity player, int iteration) {
        if (player.isRemoved() || !player.getServerWorld().getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY)) {
            return;
        }

        player.getServer().execute(() -> {
            try {
                Thread.sleep(AMBIANCE_LOOP_INTERVAL * 50);

                if (!player.isRemoved() && player.getServerWorld().getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY)) {
                    player.getServerWorld().playSound(
                        null,
                        player.getBlockPos(),
                        ModSounds.LEVEL_207_AMBIANCE,
                        SoundCategory.AMBIENT,
                        1.0F,
                        1.0F
                    );

                    scheduleRepeatingAmbiance(player, iteration + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                LOGGER.warn("Error in ambiance loop for player {}: {}", player.getName().getString(), e.getMessage());
            }
        });
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
    }
}
