package net.dark.spv_addon.blocks;

import com.sp.init.BackroomsLevels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKey;

import java.util.ArrayList;
import java.util.List;

public class Level_Transfer_Block extends Block {

    public Level_Transfer_Block(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof ServerPlayerEntity player) {
            List<RegistryKey<World>> worlds = new ArrayList<>();
            // Ajoute tous les niveaux du mod de base
            worlds.add(BackroomsLevels.LEVEL0_WORLD_KEY);
            worlds.add(BackroomsLevels.LEVEL1_WORLD_KEY);
            worlds.add(BackroomsLevels.LEVEL2_WORLD_KEY);
            worlds.add(BackroomsLevels.POOLROOMS_WORLD_KEY);
            worlds.add(BackroomsLevels.INFINITE_FIELD_WORLD_KEY);
            // Ajoute tous tes niveaux custom
            worlds.add(net.dark.spv_addon.init.BackroomsLevels.LEVELRUN_WORLD_KEY);

            RegistryKey<World> current = player.getWorld().getRegistryKey();
            if (worlds.size() <= 1) return;

            // Tire un niveau différent de celui où il est
            Random random = Random.create();
            RegistryKey<World> selected = current;
            int tries = 0;
            while (selected == current && tries < 10) {
                selected = worlds.get(random.nextBetween(0, worlds.size() - 1));
                tries++;
            }

            MinecraftServer server = player.getServer();
            if (server != null && selected != current) {
                ServerWorld targetWorld = server.getWorld(selected);
                BlockPos spawn = getLevelSpawn(targetWorld, selected);
                if (targetWorld != null && spawn != null) {
                    player.moveToWorld(targetWorld);
                    player.teleport(targetWorld, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());
                    player.sendMessage(net.minecraft.text.Text.literal("§eShifted to another level..."), false);
                }
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    private BlockPos getLevelSpawn(ServerWorld world, RegistryKey<World> key) {
        if (world == null) return null;
        // Essaie de chopper la true spawn pos (BackroomsLevel si dispo)
        try {
            var levelObj = com.sp.init.BackroomsLevels.getLevel(world);
            if (levelObj != null && levelObj.getSpawnPos() != null) {
                var spawn = levelObj.getSpawnPos();
                return new BlockPos((int) spawn.x, (int) spawn.y, (int) spawn.z);
            }
        } catch (Exception ignored) {}
        // Sinon fallback sur spawn vanilla
        return world.getSpawnPos();
    }
}
