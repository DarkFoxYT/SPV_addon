package net.dark.spv_addon.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.events.level0.Level0Blackout;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.DirectionalLight;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.init.ModSounds;
import net.dark.spv_addon.world.events.level207.Level207AmbienceEvent;
import net.dark.spv_addon.world.events.level207.Level207BellWalkerEvent;
import net.dark.spv_addon.world.generation.level207.Level207ChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;

public class Level207BackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();
    DirectionalLight light;
    float brightness;

    // Distance tracking for auto-exit
    private static final Map<UUID, Vec3d> playerStartPositions = new HashMap<>();
    private static final Map<UUID, Double> playerDistancesTraveled = new HashMap<>();
    private static final double EXIT_DISTANCE = 300.0;
    private static boolean groupExitTriggered = false;

    public Level207BackroomsLevel() {
        super("level207", Level207ChunkGenerator.CODEC, new Vec3d(7, 66, 7), BackroomsLevels.LEVEL207_WORLD_KEY, "spv_addon");
        this.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.LevelTransition> playerList = new ArrayList();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            var server = world.getServer();
            if (server != null && server.isDedicated()) {
                exitRadius = ((NewServerProperties)((MinecraftDedicatedServer)server).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof Level207BackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius) {
                playerList.add(this.getPoolRoomsTransition(playerComponent));
            }

            return playerList;
        }, "level207 -> poolrooms");
    }

    private BackroomsLevel.LevelTransition getPoolRoomsTransition(PlayerComponent playerComponent) {
        return new BackroomsLevel.LevelTransition(110, (teleport, tick) -> {
            World world = teleport.playerComponent().player.getWorld();
            if (!world.isClient()) {
                if (tick == 20) {
                    teleport.playerComponent().setShouldNoClip(true);
                    teleport.playerComponent().sync();
                }

                if (tick == 14) {
                    SPBRevamped.sendBlackScreenPacket((ServerPlayerEntity)teleport.playerComponent().player, 20, true, false);
                }

                if (tick == 1) {
                    teleport.playerComponent().setShouldNoClip(false);
                    teleport.playerComponent().sync();
                }

            }
        }, new BackroomsLevel.CrossDimensionTeleport(playerComponent, com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL.getSpawnPos(), this, com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL), (teleport, tick) -> {
            teleport.playerComponent().setShouldNoClip(false);
            teleport.playerComponent().sync();
        });
    }


    @Override
    public void register() {
        this.registerEvents("ambience", net.dark.spv_addon.world.events.level207.Level207AmbienceEvent::new);
        this.registerEvents("bellwalker_spawn", net.dark.spv_addon.world.events.level207.Level207BellWalkerEvent::new);
        this.registerEvents("empty", HaHvavCustomEvent::new);

        // Register distance tracking system
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY)) {
                trackPlayerDistances(world);
            }
        });
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) {
            if (this.light == null) {
                this.brightness = 1F;
                this.light = new DirectionalLight();
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.light.setBrightness(this.brightness).setColor(0.28F, 0.28F, 0.28F));
            }
        }
    }


    @Override
    public int nextEventDelay() {
        // Variable delay for more dynamic events
        // Shorter delays for common events, longer for rare ones
        return this.random.nextBetween(300, 1200); // 15 seconds to 1 minute
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
    }


    @Override
    public void readFromNbt(NbtCompound nbt) {
    }


    public void transitionOut(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {

    }

    @Override
    public void transitionIn(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {

    }

    @Override
    public int getTransitionDuration() {
        return 30;
    }

    private void trackPlayerDistances(ServerWorld world) {
        if (groupExitTriggered) return;

        for (ServerPlayerEntity player : world.getPlayers()) {
            UUID playerId = player.getUuid();
            Vec3d currentPos = player.getPos();

            if (!playerStartPositions.containsKey(playerId)) {
                playerStartPositions.put(playerId, currentPos);
                playerDistancesTraveled.put(playerId, 0.0);
                continue;
            }

            Vec3d startPos = playerStartPositions.get(playerId);
            double totalDistance = startPos.distanceTo(currentPos);
            playerDistancesTraveled.put(playerId, totalDistance);

            if (totalDistance >= EXIT_DISTANCE) {
                triggerGroupExit(world);
                break;
            }
        }

        cleanupDisconnectedPlayers(world);
    }

    private void triggerGroupExit(ServerWorld world) {
        groupExitTriggered = true;

        List<ServerPlayerEntity> playersToTransition = new ArrayList<>();
        for (ServerPlayerEntity player : world.getPlayers()) {
            playersToTransition.add(player);
        }

        for (ServerPlayerEntity player : playersToTransition) {
            try {
                var playerComponent = com.sp.cca_stuff.InitializeComponents.PLAYER.get(player);
                var transition = this.getPoolRoomsTransition(playerComponent);

                if (transition != null) {
                    this.getPoolRoomsTransition(playerComponent);
                }
            } catch (Exception e) {
                System.err.println("Failed to transition player " + player.getName().getString() + ": " + e.getMessage());
            }
        }

        playerStartPositions.clear();
        playerDistancesTraveled.clear();

        world.getServer().execute(() -> {
            try {
                Thread.sleep(5000);
                groupExitTriggered = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Clean up tracking data for players who have disconnected
     */
    private void cleanupDisconnectedPlayers(ServerWorld world) {
        Set<UUID> connectedPlayerIds = new HashSet<>();
        for (ServerPlayerEntity player : world.getPlayers()) {
            connectedPlayerIds.add(player.getUuid());
        }

        // Remove data for disconnected players
        playerStartPositions.entrySet().removeIf(entry -> !connectedPlayerIds.contains(entry.getKey()));
        playerDistancesTraveled.entrySet().removeIf(entry -> !connectedPlayerIds.contains(entry.getKey()));
    }
}
