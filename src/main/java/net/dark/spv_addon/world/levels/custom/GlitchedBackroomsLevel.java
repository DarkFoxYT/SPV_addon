package net.dark.spv_addon.world.levels.custom;

import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.glitched.GlitchedChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.dark.spv_addon.world.transitions.SpbTransitionDirector;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class GlitchedBackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();
    private boolean registered = false;

    public GlitchedBackroomsLevel() {
        super("glitched", GlitchedChunkGenerator.CODEC, new Vec3d(8, 30, 8), BackroomsLevels.GLITCHED_WORLD_KEY, "spv_addon");

        // Level 207 -> Glitched
        BackroomsLevels.LEVEL207_BACKROOMS_LEVEL.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.LevelTransition> transitions = new ArrayList<>();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties) ((MinecraftDedicatedServer) world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof Level207BackroomsLevel
                    && Math.abs(playerComponent.player.getPos().getX()) >= (double) exitRadius
                    && playerComponent.player.getWorld().getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY)) {
                transitions.add(SpbTransitionDirector.createTransition(
                        playerComponent,
                        this.getSpawnPos(),
                        BackroomsLevels.LEVEL207_BACKROOMS_LEVEL,
                        BackroomsLevels.GLITCHED_BACKROOMS_LEVEL,
                        SpbTransitionDirector.TransitionProfile.unstableGlitch()
                ));
            }
            return transitions;
        }, "level207 -> glitched");

        // Glitched -> Poolrooms
        this.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.LevelTransition> transitions = new ArrayList<>();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties) ((MinecraftDedicatedServer) world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof GlitchedBackroomsLevel
                    && Math.abs(playerComponent.player.getPos().getZ()) >= (double) exitRadius
                    && playerComponent.player.getWorld().getRegistryKey().equals(BackroomsLevels.GLITCHED_WORLD_KEY)) {
                transitions.add(SpbTransitionDirector.createTransition(
                        playerComponent,
                        com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL.getSpawnPos(),
                        this,
                        com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL,
                        SpbTransitionDirector.TransitionProfile.unstableGlitch()
                ));
            }
            return transitions;
        }, "glitched -> poolrooms");
    }

    @Override
    public boolean hasVanillaLighting() {
        return false;
    }

    @Override
    public void register() {
        if (registered) {
            return;
        }
        registered = true;

        this.registerEvent("glitched_static", HaHvavCustomEvent::new);
    }

    @Override
    public int nextEventDelay() {
        return random.nextBetween(140, 420);
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
    public void transitionOut(CrossDimensionTeleport teleport) {
    }

    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        teleport.playerComponent().loadPlayerSavedInventory();
    }

}

