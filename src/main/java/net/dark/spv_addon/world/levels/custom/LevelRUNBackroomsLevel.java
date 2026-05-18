package net.dark.spv_addon.world.levels.custom;

import com.sp.entity.custom.SmilerEntity;
import com.sp.init.ModEntities;
import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.run.RunChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.dark.spv_addon.world.levels.managers.LevelRunManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class LevelRUNBackroomsLevel extends BackroomsLevel {
    private static final int SMILER_SPAWN_INTERVAL = 100;
    private static final int MAX_SMILERS_NEAR_PLAYER = 4;
    private final Random random = Random.create();
    private int smilerSpawnTick = 0;
    private boolean registered = false;

    public LevelRUNBackroomsLevel() {
        super("run", RunChunkGenerator.CODEC, new Vec3d(-7.5, 1, 7.5), BackroomsLevels.LEVELRUN_WORLD_KEY, "spv_addon");

        // Initialize Level RUN manager
        LevelRunManager.initialize();
    }

    @Override
    public void register() {
        if (registered) {
            return;
        }
        registered = true;

        this.registerEvent("empty", HaHvavCustomEvent::new);

    }

    public void tick(ServerWorld world) {
        smilerSpawnTick++;
        if (smilerSpawnTick >= SMILER_SPAWN_INTERVAL) {
            smilerSpawnTick = 0;
            for (ServerPlayerEntity player : world.getPlayers()) {
                int nearby = world.getEntitiesByType(
                        ModEntities.SMILER_ENTITY,
                        player.getBoundingBox().expand(44.0),
                        entity -> entity.isAlive() && !entity.isRemoved()
                ).size();
                if (nearby >= MAX_SMILERS_NEAR_PLAYER) {
                    continue;
                }

                Vec3d offset = player.getRotationVec(1.0F)
                        .multiply(-20.0)
                        .add((world.random.nextDouble() - 0.5) * 7.0, 0.0, (world.random.nextDouble() - 0.5) * 7.0);
                SmilerEntity smiler = new SmilerEntity(ModEntities.SMILER_ENTITY, world);
                smiler.refreshPositionAndAngles(
                        player.getX() + offset.x,
                        player.getY(),
                        player.getZ() + offset.z,
                        world.random.nextFloat() * 360F,
                        0
                );
                if (world.isSpaceEmpty(smiler, smiler.getBoundingBox().expand(0.25))) {
                    world.spawnEntity(smiler);
                }
            }
        }
    }

    @Override
    public int nextEventDelay() {
        return this.random.nextBetween(2000, 3000);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
    }

    @Override
    public void transitionOut(CrossDimensionTeleport teleport) {
        // Handle player leaving Level RUN
        if (teleport.playerComponent().player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
            LevelRunManager.handlePlayerLeaveLevelRun(serverPlayer);
        }
    }

    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        teleport.playerComponent().loadPlayerSavedInventory();
        // Level RUN initialization is handled automatically by the manager
    }

}
