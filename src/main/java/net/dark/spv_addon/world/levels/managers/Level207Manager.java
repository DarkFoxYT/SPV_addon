package net.dark.spv_addon.world.levels.managers;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.dark.spv_addon.cca.Level207Component;
import net.dark.spv_addon.init.BackroomsLevels;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Manages Level 207 mechanics including time-based exits, death transitions, and glitching effects
 */
public class Level207Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(Level207Manager.class);
    
    // Time-based exit configuration (3-4 minutes)
    private static final int MIN_EXIT_TIME = 3600; // 3 minutes in ticks (3 * 60 * 20)
    private static final int MAX_EXIT_TIME = 4800; // 4 minutes in ticks (4 * 60 * 20)
    
    // Death tracking for multiplayer
    private static final Map<UUID, Integer> playerDeathCounts = new HashMap<>();
    private static final int DEATH_THRESHOLD_MULTIPLAYER = 2;
    
    // Transition destinations - using safe initialization
    private static List<TransitionDestination> SINGLEPLAYER_DESTINATIONS = null;
    private static List<TransitionDestination> MULTIPLAYER_DESTINATIONS = null;
    
    private static final Random RANDOM = new Random();
    private static final Set<UUID> playersInTransition = new HashSet<>();

    /**
     * Schedule a delayed task using server execute (replacement for getTickManager)
     */
    private static void scheduleDelayedTask(MinecraftServer server, int delayTicks, Runnable task) {
        new Thread(() -> {
            try {
                Thread.sleep(delayTicks * 50); // 50ms per tick
                server.execute(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(Level207Manager::onServerTick);
        initializeDestinations();
        LOGGER.info("Level 207 Manager initialized");
    }

    private static void initializeDestinations() {
        try {
            // Initialize singleplayer destinations
            SINGLEPLAYER_DESTINATIONS = new ArrayList<>();

            // Try to add Level Kitty if it exists
            try {
                if (BackroomsLevels.LEVEL_KITTY_WORLD_KEY != null) {
                    SINGLEPLAYER_DESTINATIONS.add(new TransitionDestination(
                        BackroomsLevels.LEVEL_KITTY_WORLD_KEY, "Level Kitty", new Vec3d(0, 1, 0)
                    ));
                }
            } catch (Exception e) {
                LOGGER.warn("Level Kitty world key not available: {}", e.getMessage());
            }

            // Try to add Poolrooms
            try {
                if (com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY != null) {
                    SINGLEPLAYER_DESTINATIONS.add(new TransitionDestination(
                        com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY, "Poolrooms", new Vec3d(0, 1, 0)
                    ));
                }
            } catch (Exception e) {
                LOGGER.warn("Poolrooms world key not available: {}", e.getMessage());
            }

            // Initialize multiplayer destinations
            MULTIPLAYER_DESTINATIONS = new ArrayList<>();

            // Try to add Poolrooms
            try {
                if (com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY != null) {
                    MULTIPLAYER_DESTINATIONS.add(new TransitionDestination(
                        com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY, "Poolrooms", new Vec3d(0, 1, 0)
                    ));
                }
            } catch (Exception e) {
                LOGGER.warn("Poolrooms world key not available for multiplayer: {}", e.getMessage());
            }

            // Try to add Level 0
            try {
                if (com.sp.init.BackroomsLevels.LEVEL0_WORLD_KEY != null) {
                    MULTIPLAYER_DESTINATIONS.add(new TransitionDestination(
                        com.sp.init.BackroomsLevels.LEVEL0_WORLD_KEY, "Level 0", new Vec3d(0, 1, 0)
                    ));
                }
            } catch (Exception e) {
                LOGGER.warn("Level 0 world key not available: {}", e.getMessage());
            }

            // Try to add Level 1
            try {
                if (com.sp.init.BackroomsLevels.LEVEL1_WORLD_KEY != null) {
                    MULTIPLAYER_DESTINATIONS.add(new TransitionDestination(
                        com.sp.init.BackroomsLevels.LEVEL1_WORLD_KEY, "Level 1", new Vec3d(0, 1, 0)
                    ));
                }
            } catch (Exception e) {
                LOGGER.warn("Level 1 world key not available: {}", e.getMessage());
            }

            LOGGER.info("Initialized {} singleplayer destinations and {} multiplayer destinations",
                SINGLEPLAYER_DESTINATIONS.size(), MULTIPLAYER_DESTINATIONS.size());

        } catch (Exception e) {
            LOGGER.error("Error initializing Level 207 destinations: {}", e.getMessage());
            // Create empty lists as fallback
            SINGLEPLAYER_DESTINATIONS = new ArrayList<>();
            MULTIPLAYER_DESTINATIONS = new ArrayList<>();
        }
    }
    
    private static void onServerTick(MinecraftServer server) {
        // Process all players in Level 207
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (isPlayerInLevel207(player)) {
                processLevel207Player(player);
            }
        }
    }
    
    private static boolean isPlayerInLevel207(ServerPlayerEntity player) {
        return player.getWorld().getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY);
    }
    
    private static void processLevel207Player(ServerPlayerEntity player) {
        Level207Component component = net.dark.spv_addon.cca.InitializeComponents.LEVEL207.get(player);
        
        // Initialize if player just entered
        if (!component.isInLevel207()) {
            component.enterLevel207();
            sendWelcomeMessage(player);
        }
        
        // Tick the component (handles timing and effects)
        component.tick();
        
        // Check for time-based exit
        if (component.shouldTriggerTimeBasedExit() && !component.hasTriggeredExit()) {
            triggerTimeBasedExit(player);
        }
        
        // Send progress updates periodically
        if (player.age % 400 == 0) { // Every 20 seconds
            sendProgressUpdate(player, component);
        }
    }
    
    private static void sendWelcomeMessage(ServerPlayerEntity player) {
        // Welcome messages removed as requested
    }
    
    private static void sendProgressUpdate(ServerPlayerEntity player, Level207Component component) {
        // Progress updates removed as requested
    }
    
    /**
     * Trigger time-based exit with glitching effects
     */
    public static void triggerTimeBasedExit(ServerPlayerEntity player) {
        if (playersInTransition.contains(player.getUuid())) {
            return; // Already in transition
        }
        
        playersInTransition.add(player.getUuid());
        
        // Mark as triggered
        Level207Component component = net.dark.spv_addon.cca.InitializeComponents.LEVEL207.get(player);
        component.setTriggeredExit(true);
        
        // Glitching messages removed as requested
        
        // Start glitching sequence
        startGlitchingSequence(player);
    }
    
    private static void startGlitchingSequence(ServerPlayerEntity player) {
        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
        
        // Phase 1: Start glitching effects (similar to SPB Level 2 system)
        player.getServer().execute(() -> {
            // Enable noclip for glitching effect
            playerComponent.setShouldNoClip(true);
            playerComponent.sync();
            
            // Send visual glitch effect
            SPBRevamped.sendBlackScreenPacket(player, 10, true, false);
            
            // Schedule phase 2 using server execute with delay
            scheduleDelayedTask(player.getServer(), 40, () -> {
                continueGlitchingSequence(player, playerComponent);
            });
        });
    }
    
    private static void continueGlitchingSequence(ServerPlayerEntity player, PlayerComponent playerComponent) {
        // Phase 2: More intense glitching (messages removed)

        // More visual effects
        SPBRevamped.sendBlackScreenPacket(player, 20, true, false);

        // Schedule final transition using server execute with delay
        scheduleDelayedTask(player.getServer(), 60, () -> {
            completeGlitchingTransition(player, playerComponent);
        });
    }
    
    private static void completeGlitchingTransition(ServerPlayerEntity player, PlayerComponent playerComponent) {
        try {
            boolean isSingleplayer = !player.getServer().isDedicated();
            TransitionDestination destination;

            // Ensure destinations are initialized
            if (SINGLEPLAYER_DESTINATIONS == null || MULTIPLAYER_DESTINATIONS == null) {
                initializeDestinations();
            }

            if (isSingleplayer) {
                destination = selectRandomDestination(SINGLEPLAYER_DESTINATIONS, player.getServer());
            } else {
                destination = selectRandomDestination(MULTIPLAYER_DESTINATIONS, player.getServer());
            }

            // Final black screen
            SPBRevamped.sendBlackScreenPacket(player, 40, true, false);

            // Teleport player
            teleportPlayerToDestination(player, destination);

            // Disable noclip
            playerComponent.setShouldNoClip(false);
            playerComponent.sync();

            // Clean up
            Level207Component component = net.dark.spv_addon.cca.InitializeComponents.LEVEL207.get(player);
            component.exitLevel207();

            playersInTransition.remove(player.getUuid());

            LOGGER.info("Player {} successfully escaped Level 207 to {}",
                player.getName().getString(), destination != null ? destination.name : "unknown");

        } catch (Exception e) {
            LOGGER.error("Error transitioning player {} out of Level 207: {}",
                player.getName().getString(), e.getMessage());

            // Emergency cleanup
            try {
                playerComponent.setShouldNoClip(false);
                playerComponent.sync();
            } catch (Exception cleanupError) {
                LOGGER.error("Error during emergency cleanup: {}", cleanupError.getMessage());
            }

            playersInTransition.remove(player.getUuid());

            // Emergency teleport to overworld
            teleportToOverworldSafely(player);
        }
    }
    
    /**
     * Handle player death in any level for Level 207 transition logic
     */
    public static void handlePlayerDeath(ServerPlayerEntity player) {
        if (player == null || player.getServer() == null) {
            LOGGER.warn("Invalid player or server in handlePlayerDeath");
            return;
        }

        try {
            boolean isSingleplayer = !player.getServer().isDedicated();

            if (isSingleplayer) {
                // In singleplayer, death transitions to Level 207 (between Kitty and Poolrooms)
                if (!isPlayerInLevel207(player)) {
                    triggerSingleplayerDeathTransition(player);
                }
            } else {
                // In multiplayer, track deaths and trigger group transition after 2 deaths
                UUID playerId = player.getUuid();
                int deathCount = playerDeathCounts.getOrDefault(playerId, 0) + 1;
                playerDeathCounts.put(playerId, deathCount);

                LOGGER.info("Player {} death count: {}/{}",
                    player.getName().getString(), deathCount, DEATH_THRESHOLD_MULTIPLAYER);

                if (deathCount >= DEATH_THRESHOLD_MULTIPLAYER) {
                    triggerMultiplayerGroupTransition(player.getServer());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error handling player death for {}: {}",
                player.getName().getString(), e.getMessage());
        }
    }
    
    private static void triggerSingleplayerDeathTransition(ServerPlayerEntity player) {
        try {
            // Death message removed as requested

            // Teleport to Level 207
            if (BackroomsLevels.LEVEL207_WORLD_KEY != null) {
                ServerWorld level207 = player.getServer().getWorld(BackroomsLevels.LEVEL207_WORLD_KEY);
                if (level207 != null) {
                    Vec3d spawnPos = new Vec3d(7, 66, 7);
                    player.teleport(level207, spawnPos.x, spawnPos.y, spawnPos.z, player.getYaw(), player.getPitch());
                    LOGGER.info("Teleported player {} to Level 207 due to death", player.getName().getString());
                } else {
                    LOGGER.error("Level 207 world not found for death transition");
                    teleportToOverworldSafely(player);
                }
            } else {
                LOGGER.error("Level 207 world key is null");
                teleportToOverworldSafely(player);
            }
        } catch (Exception e) {
            LOGGER.error("Error in singleplayer death transition for player {}: {}",
                player.getName().getString(), e.getMessage());
            teleportToOverworldSafely(player);
        }
    }
    
    private static void triggerMultiplayerGroupTransition(MinecraftServer server) {
        try {
            // Teleport all players to Level 207
            if (BackroomsLevels.LEVEL207_WORLD_KEY != null) {
                ServerWorld level207 = server.getWorld(BackroomsLevels.LEVEL207_WORLD_KEY);
                if (level207 != null) {
                    Vec3d spawnPos = new Vec3d(7, 66, 7);

                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        try {
                            // Death threshold messages removed as requested

                            player.teleport(level207, spawnPos.x, spawnPos.y, spawnPos.z, player.getYaw(), player.getPitch());
                            LOGGER.info("Teleported player {} to Level 207 due to group death threshold", player.getName().getString());
                        } catch (Exception e) {
                            LOGGER.error("Error teleporting player {} in group transition: {}",
                                player.getName().getString(), e.getMessage());
                        }
                    }

                    // Reset death counts
                    playerDeathCounts.clear();
                    LOGGER.info("Triggered multiplayer group transition to Level 207");
                } else {
                    LOGGER.error("Level 207 world not found for group transition");
                }
            } else {
                LOGGER.error("Level 207 world key is null for group transition");
            }
        } catch (Exception e) {
            LOGGER.error("Error in multiplayer group transition: {}", e.getMessage());
        }
    }
    
    private static TransitionDestination selectRandomDestination(List<TransitionDestination> destinations, MinecraftServer server) {
        if (destinations == null || destinations.isEmpty()) {
            LOGGER.warn("No destinations provided, using Overworld fallback");
            return createOverworldDestination(server);
        }

        // Filter available destinations
        List<TransitionDestination> availableDestinations = new ArrayList<>();

        for (TransitionDestination dest : destinations) {
            try {
                if (dest != null && dest.worldKey != null) {
                    ServerWorld world = server.getWorld(dest.worldKey);
                    if (world != null) {
                        availableDestinations.add(dest);
                        LOGGER.debug("Available destination: {}", dest.name);
                    } else {
                        LOGGER.debug("World not found for destination: {}", dest.name);
                    }
                } else {
                    LOGGER.warn("Invalid destination found: {}", dest);
                }
            } catch (Exception e) {
                LOGGER.error("Error checking destination {}: {}", dest != null ? dest.name : "null", e.getMessage());
            }
        }

        if (availableDestinations.isEmpty()) {
            LOGGER.warn("No available Backrooms destinations found, using Overworld fallback");
            return createOverworldDestination(server);
        }

        TransitionDestination selected = availableDestinations.get(RANDOM.nextInt(availableDestinations.size()));
        LOGGER.info("Selected destination: {}", selected.name);
        return selected;
    }

    private static TransitionDestination createOverworldDestination(MinecraftServer server) {
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
    
    private static void teleportPlayerToDestination(ServerPlayerEntity player, TransitionDestination destination) {
        if (destination == null) {
            LOGGER.error("Destination is null for player {}", player.getName().getString());
            // Fallback to overworld spawn
            teleportToOverworldSafely(player);
            return;
        }

        ServerWorld targetWorld = player.getServer().getWorld(destination.worldKey);
        if (targetWorld == null) {
            LOGGER.error("Target world {} not found for player {}",
                destination.worldKey.getValue(), player.getName().getString());
            // Fallback to overworld spawn
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

            LOGGER.info("Successfully teleported player {} to {}",
                player.getName().getString(), destination.name);

        } catch (Exception e) {
            LOGGER.error("Error teleporting player {} to {}: {}",
                player.getName().getString(), destination.name, e.getMessage());
            // Fallback to overworld spawn
            teleportToOverworldSafely(player);
        }
    }

    private static void teleportToOverworldSafely(ServerPlayerEntity player) {
        try {
            ServerWorld overworld = player.getServer().getOverworld();
            if (overworld != null) {
                // Get world spawn position
                var spawnPos = overworld.getSpawnPos();
                double x = spawnPos.getX();
                double y = spawnPos.getY() + 1; // Add 1 to avoid spawning in ground
                double z = spawnPos.getZ();

                player.teleport(overworld, x, y, z, 0.0f, 0.0f);
                // Emergency teleport message removed as requested

                LOGGER.info("Emergency teleported player {} to Overworld", player.getName().getString());
            } else {
                LOGGER.error("Cannot find Overworld for emergency teleport of player {}",
                    player.getName().getString());
            }
        } catch (Exception e) {
            LOGGER.error("Critical error in emergency teleport for player {}: {}",
                player.getName().getString(), e.getMessage());
        }
    }
    
    /**
     * Handle player leaving Level 207 (disconnect, etc.)
     */
    public static void handlePlayerLeaveLevel207(ServerPlayerEntity player) {
        Level207Component component = net.dark.spv_addon.cca.InitializeComponents.LEVEL207.get(player);
        if (component.isInLevel207()) {
            component.exitLevel207();
        }
        playersInTransition.remove(player.getUuid());
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
