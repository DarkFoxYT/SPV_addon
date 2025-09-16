package net.dark.spv_addon.world.events.misc;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;

import java.util.List;

public final class LevelRunSpawner {
    private static final Identifier LEVEL_RUN_ID = new Identifier("spv_addon", "level_run"); // <-- change si besoin
    private static int cooldown = 0;

    public static void init() {
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if (!(world.getRegistryKey().getValue().equals(LEVEL_RUN_ID))) return;
            if (cooldown > 0) { cooldown--; return; }

            List<ServerPlayerEntity> players = world.getPlayers();
            if (players.isEmpty()) return;

            for (ServerPlayerEntity p : players) {
                // Limite de densité : si déjà ≥ 6 entités "runner" à 48 blocs, on skip
                int nearby = world.getEntitiesByType(
                        /* Ton type d'entité */ EntityType.ZOMBIE, // <-- remplace par ModEntities.RUNNER
                        p.getBoundingBox().expand(48.0),
                        e -> true
                ).size();
                if (nearby >= 6) continue;

                Entity spawned = EntityType.ZOMBIE.spawn(world, null, null);
                if (spawned != null) {
                    cooldown = 20; // 1s global entre spawns (tune à volonté)
                    break;
                }
            }
        });
    }
}
