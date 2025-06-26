package net.dark.spv_addon.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.DirectionalLight;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.world.events.level207.Level207AmbienceEvent;
import net.dark.spv_addon.world.events.level207.Level207BellWalkerEvent;
import net.dark.spv_addon.world.events.level207.Level207MoveTracker;
import net.dark.spv_addon.world.generation.level207.Level207ChunkGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
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
    private int STEPS_BEFORE_WARP = 300 + random.nextInt(701);
    private final Map<UUID, Integer> stepsWalked = new HashMap<>();
    int tick;
    private boolean exitPlaced = false;

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

    private void spawnBellWalkersAround(PlayerEntity player) {
        if (!(player.getWorld() instanceof ServerWorld world)) return;

        for (int i = 0; i < 3; i++) {
            double offsetX = random.nextBetween(-50, 50);
            double offsetZ = random.nextBetween(-50, 50);
            double spawnX = player.getX() + offsetX;
            double spawnZ = player.getZ() + offsetZ;
            double spawnY = player.getY();

            BellWalkerEntity bellWalker = new BellWalkerEntity(ModEntities.SIX_LEG_ENTITY, world);
            bellWalker.refreshPositionAndAngles(spawnX, spawnY, spawnZ, 0, 0);
            world.spawnEntity(bellWalker);
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

            spawnBellWalkersAround(player);

            pc.setShouldNoClip(true);
            pc.sync();
            if (!exitPlaced && stepsWalked.getOrDefault(player.getUuid(), 0) >= 200) {
                tryPlaceExitNearPlayer(player);
                exitPlaced = true;
            }


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

            // Réinitialisation du seuil
            STEPS_BEFORE_WARP = 300 + random.nextInt(701);
        }

        // Téléportation vers un autre niveau si le joueur tombe sous Y=30
        if (player.getY() < 30 && player instanceof ServerPlayerEntity serverPlayer) {
            PlayerComponent pc = InitializeComponents.PLAYER.get(serverPlayer);

            pc.setShouldNoClip(true);
            pc.sync();

            BackroomsLevel.CrossDimensionTeleport teleport = new BackroomsLevel.CrossDimensionTeleport(
                    player.getWorld(),
                    pc,
                    new Vec3d(10, 66, 10),
                    BackroomsLevels.LEVEL207_BACKROOMS_LEVEL,
                    com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL
            );

            this.transitionOut(teleport);

            player.getServer().execute(() -> {
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                pc.setShouldNoClip(false);
                pc.sync();
            });
        }
    }

    private void tryPlaceExitNearPlayer(PlayerEntity player) {
        if (!(player.getWorld() instanceof ServerWorld world)) return;

        MinecraftServer server = world.getServer();
        if (server == null) return;

        StructureTemplateManager mgr = server.getStructureTemplateManager();
        Identifier exitId = new Identifier(Spv_addon.MOD_ID, "level207/exit");
        Optional<StructureTemplate> optTpl = mgr.getTemplate(exitId);
        if (optTpl.isEmpty()) return;

        StructureTemplate template = optTpl.get();
        int yOffset = 0;
        for (StructureTemplate.StructureBlockInfo info : template.getInfosForBlock(BlockPos.ORIGIN, new StructurePlacementData(), Blocks.LIME_WOOL)) {
            if (info.state().isOf(Blocks.LIME_WOOL)) {
                yOffset = info.pos().getY() + 1;
                break;
            }
        }

        int radius = 20;
        int px = (int) player.getX() + random.nextBetween(-radius, radius);
        int pz = (int) player.getZ() + random.nextBetween(-radius, radius);
        BlockPos basePos = new BlockPos(px, 65 - yOffset, pz);

        StructurePlacementData placeData = new StructurePlacementData()
                .setMirror(BlockMirror.NONE)
                .setRotation(BlockRotation.NONE)
                .setIgnoreEntities(true);

        template.place(world, basePos, basePos, placeData, random, 2);
    }




    @Override
    public void register() {
        Level207MoveTracker.register(this);
        events.add(Level207BellWalkerEvent::new);

    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) {
            if (this.light == null) {
                this.brightness = 1F;
                this.light = new DirectionalLight();
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(((DirectionalLight)this.light.setBrightness(this.brightness).setColor(0.28F,0.28F,0.28F)));
            }
        }
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
    public void transitionIn(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {
        if (!crossDimensionTeleport.world().isClient()) {
            ServerPlayerEntity player = (ServerPlayerEntity) crossDimensionTeleport.playerComponent().player;

            Vec3d spawn = this.getSpawnPos();
            float yaw = -90;
            float pitch = -50;
            player.teleport((ServerWorld) crossDimensionTeleport.world(), spawn.x, spawn.y, spawn.z, yaw, pitch);

            Level207AmbienceEvent ambienceEvent = new Level207AmbienceEvent();
            ambienceEvent.init(crossDimensionTeleport.world());
            if (!events.contains(Level207AmbienceEvent.class)) {
                this.events.add(Level207AmbienceEvent::new);
            }

        }
    }

    @Override
        public int getTransitionDuration () {
            return 30;
        }
    }
