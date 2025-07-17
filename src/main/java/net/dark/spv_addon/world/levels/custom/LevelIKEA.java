package net.dark.spv_addon.world.levels.custom;

import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.ikea.LevelIKEAChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class LevelIKEA extends BackroomsLevel {
    public LevelIKEA() {
        super("level_ikea", LevelIKEAChunkGenerator.CODEC, new Vec3d(16, 2, 16), BackroomsLevels.LEVEL_IKEA_WORLD_KEY, "spv_addon");

        com.sp.init.BackroomsLevels.LEVEL2_BACKROOMS_LEVEL.unregisterTransition("level2 -> poolrooms");

        com.sp.init.BackroomsLevels.LEVEL2_BACKROOMS_LEVEL.registerTransition(new BackroomsLevel.LevelTransition(110, (world, playerComponent, from) -> {
            List<BackroomsLevel.CrossDimensionTeleport> playerList = new ArrayList<>();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties) ((MinecraftDedicatedServer) world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof Level2BackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius && playerComponent.player.getWorld().getRegistryKey() == com.sp.init.BackroomsLevels.LEVEL2_WORLD_KEY) {
                playerList.add(new BackroomsLevel.CrossDimensionTeleport(playerComponent.player.getWorld(), playerComponent, this.getSpawnPos(), com.sp.init.BackroomsLevels.LEVEL2_BACKROOMS_LEVEL, BackroomsLevels.LEVEL207_BACKROOMS_LEVEL));
            }

            return playerList;
        }), "level2 -> level207");
    }

    @Override
    public void register() {

        this.registerEvents("empty_ikea", HaHvavCustomEvent::new);

    }


    @Override
    public int nextEventDelay() {
        return 100;
    }

    @Override
    public BoolTextPair allowsTorch() {
        return new BoolTextPair(true, Text.translatable("Flash.on"));
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
    }

    @Override
    public boolean transitionOut(CrossDimensionTeleport teleport) {
        return false;
    }

    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {

    }

    @Override
    public int getTransitionDuration() {
        return 30;
    }
}
