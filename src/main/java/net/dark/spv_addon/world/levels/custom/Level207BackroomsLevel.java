package net.dark.spv_addon.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.level207.Level207ChunkGenerator;
import net.dark.spv_addon.world.events.level207.Level207AmbienceEvent;
import net.dark.spv_addon.world.events.level207.Level207BellWalkerEvent;
import net.dark.spv_addon.world.events.level207.Level207MoveTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.*;

public class Level207BackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();

    private int STEPS_BEFORE_WARP = 300 + random.nextInt(701); // nombre de blocs à parcourir avant warp (300 à 1000)
    private final Map<UUID, Integer> stepsWalked = new HashMap<>();
    private static final int STEPS_BEFORE_BELLWALKER = 100;


    public Level207BackroomsLevel() {
        super("level207", Level207ChunkGenerator.CODEC, new Vec3d(7, 66, 7), BackroomsLevels.LEVEL207_WORLD_KEY, "spv_addon");
        this.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.CrossDimensionTeleport> playerList = new ArrayList();
            if (from instanceof Level1BackroomsLevel && playerComponent.player.getPos().getY() <= 12.0F && playerComponent.player.isOnGround()) {
                for(PlayerEntity player : playerComponent.player.getWorld().getPlayers()) {
                    PlayerComponent otherPlayerComponent = InitializeComponents.PLAYER.get(player);
                    double playerY = player.getPos().getY();
                    if (player.getWorld().getRegistryKey() == BackroomsLevels.LEVEL207_WORLD_KEY && playerY == 60.0 && player.isOnGround()) {
                        playerList.add(new BackroomsLevel.CrossDimensionTeleport(
                            player.getWorld(),
                            otherPlayerComponent,
                            this.calculateLevel2TeleportCoords(player, playerComponent.player.getChunkPos()),
                            BackroomsLevels.LEVEL207_BACKROOMS_LEVEL,
                            com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL
                        ));
                    }
                }
            }

            return playerList;
        }, "level207 -> poolrooms");
    }

    private Vec3d calculateLevel2TeleportCoords(PlayerEntity player, ChunkPos chunkPos) {
        if (chunkPos.x == player.getChunkPos().x && chunkPos.z == player.getChunkPos().z) {
            int chunkX = chunkPos.getStartX();
            int chunkZ = chunkPos.getStartZ();
            double playerX = player.getPos().x;
            double playerZ = player.getPos().z;
            return new Vec3d(playerX - (double)chunkX - (double)1.0F, player.getPos().y + (double)8.0F, playerZ - (double)chunkZ);
        } else {
            return this.getSpawnPos();
        }
    }

    public void onPlayerMove(PlayerEntity player, Vec3d oldPos, Vec3d newPos) {
        if (!player.isOnGround()) return;
        int steps = (int) oldPos.distanceTo(newPos);
        if (steps <= 0) return;
        UUID uuid = player.getUuid();
        stepsWalked.put(uuid, stepsWalked.getOrDefault(uuid, 0) + steps);


        if (stepsWalked.get(uuid) >= STEPS_BEFORE_WARP) {
            stepsWalked.put(uuid, 0);
            PlayerComponent pc = InitializeComponents.PLAYER.get(player);

            pc.setShouldNoClip(true);
            pc.sync();

            BackroomsLevel.CrossDimensionTeleport teleport = new BackroomsLevel.CrossDimensionTeleport(
                    player.getWorld(),
                    pc,
                    this.getSpawnPos(),
                    BackroomsLevels.LEVEL207_BACKROOMS_LEVEL,
                    com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL
            );
            this.transitionOut(teleport);

            player.getServer().execute(() -> {
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                pc.setShouldNoClip(false);
                pc.sync();
            });

            // Redéfinir un nouveau seuil aléatoire pour le prochain warp
            STEPS_BEFORE_WARP = 300 + random.nextInt(701);
        }
    }




    @Override
    public void register() {
        Level207MoveTracker.register(this);
        events.add(Level207BellWalkerEvent::new);

    }




    @Override
    public int nextEventDelay() {
        return 100;
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
    }


    @Override
    public void readFromNbt(NbtCompound nbt) {
    }


    public boolean transitionOut(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {
        if (!crossDimensionTeleport.world().isClient() && !crossDimensionTeleport.playerComponent().isTeleporting()) {
            SPBRevamped.sendLevelTransitionLightsOutPacket((ServerPlayerEntity)crossDimensionTeleport.playerComponent().player, 80);
        }

        return crossDimensionTeleport.playerComponent().player.isOnGround();
    }

    @Override
        public void transitionIn (BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport){
            // Démarre l'événement d'ambiance dès l'entrée du joueur dans le niveau
            if (!crossDimensionTeleport.world().isClient()) {
                Level207AmbienceEvent ambienceEvent = new Level207AmbienceEvent();
                ambienceEvent.init(crossDimensionTeleport.world());
            }
    }

    @Override
        public int getTransitionDuration () {
            return 30;
        }
    }
