package net.dark.spv_addon.world.levels.custom;

import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.world.events.levelkitty.KittyMeowEvent;
import net.dark.spv_addon.world.generation.kitty.KittyChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class LevelKittyBackroomsLevel extends BackroomsLevel {
    private static boolean kittySpawned = false;
    private final Random random = Random.create();

    public LevelKittyBackroomsLevel() {
        super("kitty", KittyChunkGenerator.CODEC, new Vec3d(21, 2, 13), BackroomsLevels.LEVEL_KITTY_WORLD_KEY, "spv_addon");

        this.registerTransition(new BackroomsLevel.LevelTransition(110, (world, playerComponent, from) -> {
            List<BackroomsLevel.CrossDimensionTeleport> playerList = new ArrayList<>();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties) ((MinecraftDedicatedServer) world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof LevelKittyBackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= (double) exitRadius && playerComponent.player.getWorld().getRegistryKey() == BackroomsLevels.LEVEL_KITTY_WORLD_KEY) {
                playerList.add(new BackroomsLevel.CrossDimensionTeleport(playerComponent.player.getWorld(), playerComponent, this.getSpawnPos(), BackroomsLevels.LEVEL_KITTY_BACKROOMS_LEVEL, com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL));
            }

            return playerList;
        }), "kitty -> poolrooms");
    }

    public static void ensureSingleKitty(ServerWorld world) {
        if (kittySpawned) return;

        BlockPos spawnPos = new BlockPos(15, 2, 18);
        if (!world.isChunkLoaded(spawnPos)) return;

        KittyEntity kitty = new KittyEntity(ModEntities.KITTY, world);
        kitty.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
        world.spawnEntity(kitty);
        kittySpawned = true;

        System.out.println("Spawned kitty at " + spawnPos);
    }

    @Override
    public void register() {
        this.registerEvents("empty", HaHvavCustomEvent::new);

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getRegistryKey().equals(BackroomsLevels.LEVEL_KITTY_WORLD_KEY)) {
                ensureSingleKitty(world);
            }
        });
    }

    public void tick(net.minecraft.server.world.ServerWorld world) {
        ensureSingleKitty(world);
    }

    @Override
    public int nextEventDelay() {
        return this.random.nextBetween(100, 1000);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
    }

    @Override
    public boolean transitionOut(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {
        return true;
    }

    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        teleport.playerComponent().loadPlayerSavedInventory();
    }

}
