package net.dark.spv_addon.world.events.misc;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public final class LevelRunSpawner {
    private static final Identifier LEVEL_RUN_ID = new Identifier("spv_addon", "run");
    private static int cooldown = 0;

    private LevelRunSpawner() {
    }

    public static void init() {
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
                        EntityType.ZOMBIE,
                        player.getBoundingBox().expand(48.0),
                        entity -> true
                ).size();
                if (nearby >= 6) {
                    continue;
                }

                Entity spawned = EntityType.ZOMBIE.create(world);
                if (spawned != null) {
                    spawned.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    world.spawnEntity(spawned);
                    cooldown = 40;
                    break;
                }
            }
        });
    }
}
