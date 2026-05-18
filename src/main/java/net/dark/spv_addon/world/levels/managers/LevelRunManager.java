package net.dark.spv_addon.world.levels.managers;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.dark.spv_addon.cca.LevelRunComponent;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.transitions.SpbTransitionDirector;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.dark.spv_addon.util.ServerTickScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Manages Level RUN mechanics including damage over time and distance-based transitions
 */
public class LevelRunManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LevelRunManager.class);
    
    // Transition destinations (in order of preference)
    private static final List<TransitionDestination> TRANSITION_DESTINATIONS = Arrays.asList(
        new TransitionDestination(com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY, "Poolrooms", new Vec3d(0, 1, 0)),
        new TransitionDestination(com.sp.init.BackroomsLevels.LEVEL0_WORLD_KEY, "Level 0", new Vec3d(0, 1, 0)),
        new TransitionDestination(com.sp.init.BackroomsLevels.LEVEL1_WORLD_KEY, "Level 1", new Vec3d(0, 1, 0))
    );
    
    private static final Random RANDOM = new Random();
    private static final Set<UUID> playersInTransition = new HashSet<>();
    private static boolean initialized = false;
    
    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        ServerTickEvents.END_SERVER_TICK.register(LevelRunManager::onServerTick);
        LOGGER.info("Level RUN Manager initialized");
    }
    
    private static void onServerTick(MinecraftServer server) {
        // Process all players in Level RUN
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (isPlayerInLevelRun(player)) {
                processLevelRunPlayer(player);
            }
        }
    }
    
    private static boolean isPlayerInLevelRun(ServerPlayerEntity player) {
        try {
            if (player == null || player.getWorld() == null || BackroomsLevels.LEVELRUN_WORLD_KEY == null) {
                return false;
            }
            return player.getWorld().getRegistryKey().equals(BackroomsLevels.LEVELRUN_WORLD_KEY);
        } catch (Exception e) {
            LOGGER.warn("Error checking if player is in Level RUN: {}", e.getMessage());
            return false;
        }
    }
    
    private static void processLevelRunPlayer(ServerPlayerEntity player) {
        try {
            if (player == null) {
                return;
            }

            LevelRunComponent runComponent = net.dark.spv_addon.cca.InitializeComponents.LEVEL_RUN.get(player);
            if (runComponent == null) {
                LOGGER.warn("LevelRunComponent is null for player {}", player.getName().getString());
                return;
            }

            // Initialize if player just entered
            if (!runComponent.isInLevelRun()) {
                runComponent.enterLevelRun();
                sendWelcomeMessage(player);
            }

            // Tick the component (handles damage and distance tracking)
            runComponent.tick();

            // Send progress updates periodically (removed as requested)
            if (player.age % 200 == 0) { // Every 10 seconds
                sendProgressUpdate(player, runComponent);
            }
        } catch (Exception e) {
            LOGGER.error("Error processing Level RUN player {}: {}",
                player != null ? player.getName().getString() : "null", e.getMessage());
        }
    }
    
    private static void sendWelcomeMessage(ServerPlayerEntity player) {
        // Welcome messages removed as requested
    }
    
    private static void sendProgressUpdate(ServerPlayerEntity player, LevelRunComponent runComponent) {
        // Progress updates removed as requested
    }
    
    /**
     * Trigger transition out of Level RUN for a player
     */
    public static void triggerTransitionOut(ServerPlayerEntity player) {
        if (playersInTransition.contains(player.getUuid())) {
            return; // Already in transition
        }
        
        playersInTransition.add(player.getUuid());
        
        // Completion messages removed as requested
        
        int teleportDelay = SpbTransitionDirector.beginDirectTransition(
                player,
                SpbTransitionDirector.TransitionProfile.runEscape()
        );

        ServerTickScheduler.schedule(teleportDelay, () -> {
            try {
                TransitionDestination destination = selectRandomDestination(player.getServer());
                teleportPlayerToDestination(player, destination);
                
                // Clean up
                LevelRunComponent runComponent = net.dark.spv_addon.cca.InitializeComponents.LEVEL_RUN.get(player);
                runComponent.exitLevelRun();
                SpbTransitionDirector.completeDirectTransition(player);
                
                playersInTransition.remove(player.getUuid());
                
                LOGGER.info("Player {} successfully transitioned out of Level RUN to {}", 
                    player.getName().getString(), destination.name);
                
            } catch (Exception e) {
                LOGGER.error("Error transitioning player {} out of Level RUN: {}",
                    player.getName().getString(), e.getMessage());
                SpbTransitionDirector.completeDirectTransition(player);
                playersInTransition.remove(player.getUuid());
            }
        });
    }
    
    private static TransitionDestination selectRandomDestination(MinecraftServer server) {
        // Filter available destinations
        List<TransitionDestination> availableDestinations = new ArrayList<>();

        for (TransitionDestination dest : TRANSITION_DESTINATIONS) {
            try {
                if (dest != null && dest.worldKey != null) {
                    ServerWorld world = server.getWorld(dest.worldKey);
                    if (world != null) {
                        availableDestinations.add(dest);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error checking destination {}: {}", dest != null ? dest.name : "null", e.getMessage());
            }
        }

        if (availableDestinations.isEmpty()) {
            // Fallback to overworld if no Backrooms levels are available
            LOGGER.warn("No available destinations found, using Overworld fallback");
            try {
                ServerWorld overworld = server.getOverworld();
                if (overworld != null) {
                    var spawnPos = overworld.getSpawnPos();
                    return new TransitionDestination(
                        World.OVERWORLD,
                        "Overworld",
                        new Vec3d(spawnPos.getX(), spawnPos.getY() + 1, spawnPos.getZ())
                    );
                }
            } catch (Exception e) {
                LOGGER.error("Error creating Overworld destination: {}", e.getMessage());
            }

            // Ultimate fallback
            return new TransitionDestination(World.OVERWORLD, "Overworld", new Vec3d(0, 64, 0));
        }

        return availableDestinations.get(RANDOM.nextInt(availableDestinations.size()));
    }
    
    private static void teleportPlayerToDestination(ServerPlayerEntity player, TransitionDestination destination) {
        if (destination == null) {
            LOGGER.error("Destination is null for player {}", player.getName().getString());
            teleportToOverworldSafely(player);
            return;
        }

        ServerWorld targetWorld = player.getServer().getWorld(destination.worldKey);
        if (targetWorld == null) {
            LOGGER.error("Target world {} not found for player {}",
                destination.worldKey.getValue(), player.getName().getString());
            teleportToOverworldSafely(player);
            return;
        }

        try {
            // Add some randomization to spawn position
            double x = destination.spawnPos.x + (RANDOM.nextDouble() - 0.5) * 4;
            double y = destination.spawnPos.y;
            double z = destination.spawnPos.z + (RANDOM.nextDouble() - 0.5) * 4;

            // Teleport player
            player.teleport(targetWorld, x, y, z, player.getYaw(), player.getPitch());

            // Arrival message removed as requested

            // Reset Level Run usage flag so it can be triggered again
            net.dark.spv_addon.world.events.misc.LevelRunGlobalTicker.resetLevelRunUsage();

        } catch (Exception e) {
            LOGGER.error("Error teleporting player {} to {}: {}",
                player.getName().getString(), destination.name, e.getMessage());
            teleportToOverworldSafely(player);
        }
    }

    private static void teleportToOverworldSafely(ServerPlayerEntity player) {
        try {
            ServerWorld overworld = player.getServer().getOverworld();
            if (overworld != null) {
                var spawnPos = overworld.getSpawnPos();
                double x = spawnPos.getX();
                double y = spawnPos.getY() + 1;
                double z = spawnPos.getZ();

                player.teleport(overworld, x, y, z, 0.0f, 0.0f);
                // Emergency teleport message removed as requested

                LOGGER.info("Emergency teleported player {} to Overworld", player.getName().getString());
            }
        } catch (Exception e) {
            LOGGER.error("Critical error in emergency teleport for player {}: {}",
                player.getName().getString(), e.getMessage());
        }
    }
    
    /**
     * Handle player leaving Level RUN (disconnect, death, etc.)
     */
    public static void handlePlayerLeaveLevelRun(ServerPlayerEntity player) {
        LevelRunComponent runComponent = net.dark.spv_addon.cca.InitializeComponents.LEVEL_RUN.get(player);
        if (runComponent.isInLevelRun()) {
            runComponent.exitLevelRun();
        }
        playersInTransition.remove(player.getUuid());
    }
    
    /**
     * Get progress information for a player in Level RUN (for admin commands only)
     */
    public static String getProgressInfo(ServerPlayerEntity player) {
        if (!isPlayerInLevelRun(player)) {
            return "Not in Level RUN";
        }

        LevelRunComponent runComponent = net.dark.spv_addon.cca.InitializeComponents.LEVEL_RUN.get(player);
        double distance = runComponent.getTotalDistanceTraveled();
        double remaining = runComponent.getDistanceToTransition();
        int percentage = (int) ((distance / LevelRunComponent.getTransitionDistance()) * 100);

        return String.format("%.1f/%.0f blocks (%d%%)", distance, LevelRunComponent.getTransitionDistance(), percentage);
    }
    
    /**
     * Data class for transition destinations
     */
    private static class TransitionDestination {
        final RegistryKey<World> worldKey;
        final String name;
        final Vec3d spawnPos;
        
        TransitionDestination(RegistryKey<World> worldKey, String name, Vec3d spawnPos) {
            this.worldKey = worldKey;
            this.name = name;
            this.spawnPos = spawnPos;
        }
    }
}
