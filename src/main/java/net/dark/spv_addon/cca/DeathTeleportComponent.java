package net.dark.spv_addon.cca;

import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

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
            ServerWorld level207 = player.getServer().getWorld(BackroomsLevels.LEVEL207_WORLD_KEY);
        if (level207 != null) {
            Vec3d spawn = new Vec3d(16, 66, 16);
                for (ServerPlayerEntity p : player.getServer().getPlayerManager().getPlayerList()) {
                    p.teleport(level207, spawn.x, spawn.y, spawn.z, p.getYaw(), p.getPitch());
                }
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        // Pas de persistance pour le compteur global dans ce composant
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        // Pas de persistance pour le compteur global dans ce composant
    }
}