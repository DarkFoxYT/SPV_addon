package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class DeathTeleportComponent implements Component, AutoSyncedComponent {
    private static int globalDeathCount = 0;
    private static boolean hasTeleported = false;
    private final ServerPlayerEntity player;

    public DeathTeleportComponent(ServerPlayerEntity player) {
        this.player = player;
    }

    public void onDeath() {
        if (hasTeleported) return;
        globalDeathCount++;
        if (globalDeathCount >= 3) {
            hasTeleported = true;
            var server = player.getWorld().getServer();
            if (server != null) {
                ServerWorld level207 = server.getWorld(BackroomsLevels.LEVEL207_WORLD_KEY);
                if (level207 != null) {
                    Vec3d spawn = new Vec3d(16, 66, 16);
                    for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
                        p.teleport(level207, spawn.x, spawn.y, spawn.z, p.getYaw(), p.getPitch());
                    }
                }
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        globalDeathCount = nbtCompound.getInt("globalDeathCount");
        hasTeleported = nbtCompound.getBoolean("hasTeleported");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putInt("globalDeathCount", globalDeathCount);
        nbtCompound.putBoolean("hasTeleported", hasTeleported);
    }
}