package net.dark.spv_addon.world.events;

import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.levels.custom.Level207BackroomsLevel;
import net.dark.spv_addon.world.levels.custom.LevelIKEA;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class LevelRunVoidDamageHandler {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                if (!world.getRegistryKey().equals(BackroomsLevels.LEVELRUN_WORLD_KEY)) continue;
                for (ServerPlayerEntity player : world.getPlayers()) {
                    if (player.getY() < -50) { // Si le joueur est en dessous de Y = -50
                        player.setVelocity(Vec3d.ZERO);
        BackroomsLevel.CrossDimensionTeleport teleport = new BackroomsLevel.CrossDimensionTeleport(
                            player.getWorld(),
                            null,
                            player.getPos(),
                            null,
                            null
        );
        Level207BackroomsLevel level207 = (Level207BackroomsLevel) BackroomsLevels.LEVEL207_BACKROOMS_LEVEL;
        if (level207 != null) {
            level207.transitionIn(teleport);
        }
                    }
                }
            }
        });
    }
}