package net.dark.spv_addon.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.PoolroomsBackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.transitions.SpbTransitionDirector;
import net.dark.spv_addon.world.generation.level188.Level188ChunkGenerator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Level188BackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();
    private boolean registered = false;

    public Level188BackroomsLevel() {
        super("level188", Level188ChunkGenerator.CODEC, new Vec3d(16, 60, 16), BackroomsLevels.LEVEL188_WORLD_KEY, "spv_addon");

        // Register transition from Poolrooms to Level 105
        com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.LevelTransition> playerList = new ArrayList<>();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties)((MinecraftDedicatedServer)world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof PoolroomsBackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius && playerComponent.player.getWorld().getRegistryKey().equals(com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY)) {
                playerList.add(this.getLevel105Transition(playerComponent));
            }

            return playerList;
        }, "poolrooms -> level188");

        // Register transition from Level 105 to Infinite Fields
        this.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.LevelTransition> playerList = new ArrayList<>();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties)((MinecraftDedicatedServer)world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof Level188BackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius && playerComponent.player.getWorld().getRegistryKey().equals(BackroomsLevels.LEVEL188_WORLD_KEY)) {
                playerList.add(this.getInfiniteFieldsTransition(playerComponent));
            }

            return playerList;
        }, "level188 -> infinite_fields");

        // Register transition from Infinite Fields back to Level 105
        com.sp.init.BackroomsLevels.INFINITE_FIELD_BACKROOMS_LEVEL.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.LevelTransition> playerList = new ArrayList<>();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties)((MinecraftDedicatedServer)world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius && playerComponent.player.getWorld().getRegistryKey().equals(com.sp.init.BackroomsLevels.INFINITE_FIELD_WORLD_KEY)) {
                playerList.add(this.getLevel105FromInfiniteFieldsTransition(playerComponent));
            }

            return playerList;
        }, "infinite_fields -> level188");
    }

    private BackroomsLevel.LevelTransition getLevel105Transition(PlayerComponent playerComponent) {
        return SpbTransitionDirector.createTransition(
                playerComponent,
                this.getSpawnPos(),
                com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL,
                BackroomsLevels.LEVEL188_BACKROOMS_LEVEL,
                SpbTransitionDirector.TransitionProfile.cinematicDefault()
        );
    }

    private BackroomsLevel.LevelTransition getInfiniteFieldsTransition(PlayerComponent playerComponent) {
        return SpbTransitionDirector.createTransition(
                playerComponent,
                com.sp.init.BackroomsLevels.INFINITE_FIELD_BACKROOMS_LEVEL.getSpawnPos(),
                this,
                com.sp.init.BackroomsLevels.INFINITE_FIELD_BACKROOMS_LEVEL,
                SpbTransitionDirector.TransitionProfile.cinematicDefault()
        );
    }

    private BackroomsLevel.LevelTransition getLevel105FromInfiniteFieldsTransition(PlayerComponent playerComponent) {
        return SpbTransitionDirector.createTransition(
                playerComponent,
                this.getSpawnPos(),
                com.sp.init.BackroomsLevels.INFINITE_FIELD_BACKROOMS_LEVEL,
                BackroomsLevels.LEVEL188_BACKROOMS_LEVEL,
                SpbTransitionDirector.TransitionProfile.cinematicDefault()
        );

    }

    @Override
    public boolean hasVanillaLighting() {
        return true;
    }

    @Override
    public void register() {
        if (registered) {
            return;
        }
        registered = true;
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
    public void transitionOut(CrossDimensionTeleport teleport) {
    }

    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        teleport.playerComponent().loadPlayerSavedInventory();
    }

}
