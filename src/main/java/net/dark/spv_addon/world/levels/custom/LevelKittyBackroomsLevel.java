package net.dark.spv_addon.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.world.events.levelkitty.KittyMeowEvent;
import net.dark.spv_addon.world.generation.kitty.KittyChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.dark.spv_addon.world.transitions.SpbTransitionDirector;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class LevelKittyBackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();
    private boolean registered = false;

    public LevelKittyBackroomsLevel() {
        super("kitty", KittyChunkGenerator.CODEC, new Vec3d(21, 2, 13), BackroomsLevels.LEVEL_KITTY_WORLD_KEY, "spv_addon");

        // Register transition from Level 2 to Kitty Level
        com.sp.init.BackroomsLevels.LEVEL2_BACKROOMS_LEVEL.unregisterTransition("level2 -> poolrooms");
        com.sp.init.BackroomsLevels.LEVEL2_BACKROOMS_LEVEL.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.LevelTransition> playerList = new ArrayList<>();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties)((MinecraftDedicatedServer)world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof Level2BackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius && playerComponent.player.getWorld().getRegistryKey().equals(com.sp.init.BackroomsLevels.LEVEL2_WORLD_KEY)) {
                playerList.add(this.getKittyTransition(playerComponent));
            }

            return playerList;
        }, "level2 -> kitty");

        // Register transition from Kitty Level to Poolrooms
        this.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.LevelTransition> playerList = new ArrayList<>();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties)((MinecraftDedicatedServer)world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof LevelKittyBackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius && playerComponent.player.getWorld().getRegistryKey().equals(BackroomsLevels.LEVEL_KITTY_WORLD_KEY)) {
                playerList.add(this.getPoolRoomsTransition(playerComponent));
            }

            return playerList;
        }, "kitty -> poolrooms");
    }

    private BackroomsLevel.LevelTransition getKittyTransition(PlayerComponent playerComponent) {
        return SpbTransitionDirector.createTransition(
                playerComponent,
                this.getSpawnPos(),
                com.sp.init.BackroomsLevels.LEVEL2_BACKROOMS_LEVEL,
                BackroomsLevels.LEVEL_KITTY_BACKROOMS_LEVEL,
                SpbTransitionDirector.TransitionProfile.cinematicDefault()
        );
    }

    public BackroomsLevel.LevelTransition getPoolRoomsTransition(PlayerComponent playerComponent) {
        return SpbTransitionDirector.createTransition(
                playerComponent,
                com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL.getSpawnPos(),
                this,
                com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL,
                SpbTransitionDirector.TransitionProfile.cinematicDefault()
        );
    }

    public static void ensureSingleKitty(ServerWorld world) {
        boolean existingKitty = !world.getEntitiesByType(
                ModEntities.KITTY,
                new net.minecraft.util.math.Box(-64.0, -16.0, -64.0, 96.0, 48.0, 96.0),
                kitty -> kitty.isAlive() && !kitty.isRemoved()
        ).isEmpty();
        if (existingKitty) return;

        BlockPos spawnPos = new BlockPos(15, 2, 18);
        if (!world.isChunkLoaded(spawnPos)) return;

        KittyEntity kitty = new KittyEntity(ModEntities.KITTY, world);
        kitty.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
        world.spawnEntity(kitty);
    }

    @Override
    public void register() {
        if (registered) {
            return;
        }
        registered = true;

        // Register enhanced kitty events
        this.registerEvent("kitty_drops", KittyMeowEvent::new);
        this.registerEvent("phantom_kitty", net.dark.spv_addon.world.events.levelkitty.PhantomKittyEvent::new);
        this.registerEvent("empty", HaHvavCustomEvent::new);

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
        // More frequent events since we have more variety now
        return this.random.nextBetween(200, 800); // Slightly more frequent than before
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
    }

    @Override
    public void transitionOut(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {

    }

    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        teleport.playerComponent().loadPlayerSavedInventory();
    }

    /**
     * Execute a transition manually (used by KittyEntity)
     */
    public void executeTransition(BackroomsLevel.LevelTransition transition) {
        if (transition == null || transition.teleport() == null) {
            return;
        }

        var playerComponent = transition.teleport().playerComponent();
        if (!(playerComponent.player instanceof ServerPlayerEntity) || playerComponent.currentTransition != null) {
            return;
        }

        playerComponent.currentTransition = transition;
        playerComponent.setTeleportingTimer(-1);
        playerComponent.sync();
    }
}
