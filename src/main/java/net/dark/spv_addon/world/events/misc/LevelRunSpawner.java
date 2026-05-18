package net.dark.spv_addon.world.events.misc;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import com.sp.entity.custom.SmilerEntity;
import com.sp.init.ModEntities;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public final class LevelRunSpawner {
    private static final Identifier LEVEL_RUN_ID = new Identifier("spv_addon", "run");
    private static final int SPAWN_INTERVAL_TICKS = 70;
    private static final int MAX_SMILERS_NEAR_PLAYER = 4;
    private static final double COUNT_RADIUS = 44.0;
    private static final double SPAWN_MIN_DISTANCE = 18.0;
    private static final double SPAWN_RANDOM_SPREAD = 8.0;
    private static int cooldown = 0;
    private static boolean initialized = false;

    private LevelRunSpawner() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if (!world.getRegistryKey().getValue().equals(LEVEL_RUN_ID)) {
                return;
            }
            if (cooldown > 0) {
                cooldown--;
                return;
            }

            List<ServerPlayerEntity> players = world.getPlayers();
            if (players.isEmpty()) {
                return;
            }

            for (ServerPlayerEntity player : players) {
                int nearby = world.getEntitiesByType(
                        ModEntities.SMILER_ENTITY,
                        player.getBoundingBox().expand(COUNT_RADIUS),
                        entity -> entity.isAlive() && !entity.isRemoved()
                ).size();
                if (nearby >= MAX_SMILERS_NEAR_PLAYER) {
                    continue;
                }

                SmilerEntity spawned = new SmilerEntity(ModEntities.SMILER_ENTITY, world);
                Vec3d look = player.getRotationVec(1.0F).multiply(-SPAWN_MIN_DISTANCE);
                double x = player.getX() + look.x + (world.random.nextDouble() - 0.5) * SPAWN_RANDOM_SPREAD;
                double z = player.getZ() + look.z + (world.random.nextDouble() - 0.5) * SPAWN_RANDOM_SPREAD;
                double y = player.getY();

                spawned.refreshPositionAndAngles(x, y, z, player.getYaw(), 0.0F);
                Box spawnBox = spawned.getBoundingBox().expand(0.25);
                if (world.isSpaceEmpty(spawned, spawnBox)) {
                    world.spawnEntity(spawned);
                    cooldown = SPAWN_INTERVAL_TICKS;
                    break;
                }
            }
        });
    }
}
