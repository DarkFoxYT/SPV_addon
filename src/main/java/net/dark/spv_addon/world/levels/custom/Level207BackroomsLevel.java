package net.dark.spv_addon.world.levels.custom;

import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.kitty.KittyChunkGenerator;
import net.dark.spv_addon.world.generation.level207.Level207ChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.dark.spv_addon.world.levels.custom.events.Level207AmbienceEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents Backrooms Level 207 in the mod.
 * Handles generation, events, transitions, and specific rules for this level.
 */
public class Level207BackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();

    /**
     * Constructs Level 207.
     * Initializes the level with its chunk generator, spawn position, world key, and mod ID.
     */
    public Level207BackroomsLevel() {
        super("level207", Level207ChunkGenerator.CODEC, new Vec3d(7, 66, 7), BackroomsLevels.LEVEL207_WORLD_KEY, "spv_addon");
        this.registerTransition((world, playerComponent, from) -> {
            List<CrossDimensionTeleport> playerList = new ArrayList();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties)((MinecraftDedicatedServer)world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof Level207BackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius && playerComponent.player.getWorld().getRegistryKey() == BackroomsLevels.LEVEL207_WORLD_KEY) {
                playerList.add(new BackroomsLevel.CrossDimensionTeleport(playerComponent.player.getWorld(), playerComponent, this.getSpawnPos(), BackroomsLevels.LEVEL207_BACKROOMS_LEVEL, com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL));
            }

            return playerList;
        }, "level207 -> poolrooms");
    }

    /**
     * Indicates if vanilla lighting is enabled in this level.
     * @return true if vanilla lighting is used.
     */
    @Override
    public boolean hasVanillaLighting() {
        return true;
    }

    /**
     * Indicates if the flashlight (torch) is allowed in this level.
     * @return BoolTextPair containing the permission and a message.
     */
    @Override
    public BoolTextPair allowsTorch() {
        return new BoolTextPair(true, Text.translatable("Flashlight is allowed in this level."));
    }

    /**
     * Registers custom events for this level.
     */
    @Override
    public void register() {

        events.add(HaHvavCustomEvent::new);

        events.add(Level207AmbienceEvent::new);
    }

    /**
     * Determines the delay before the next event.
     * @return the delay in ticks.
     */
    @Override
    public int nextEventDelay() {
        return this.random.nextBetween(100000, 100000);
    }

    /**
     * Saves the level data to NBT.
     * @param nbt the NBT container to write data to.
     */
    @Override
    public void writeToNbt(NbtCompound nbt) {
    }

    /**
     * Loads the level data from NBT.
     * @param nbt the NBT container to read data from.
     */
    @Override
    public void readFromNbt(NbtCompound nbt) {
    }

    /**
     * Determines if the player can exit this level via a transition.
     * @param teleport the cross-dimension teleport object.
     * @return true if the transition is allowed (player is sneaking).
     */
    @Override
    public boolean transitionOut(CrossDimensionTeleport teleport) {
        return teleport.playerComponent().player.isSneaking();
    }

    /**
     * Actions to perform when entering this level.
     * @param teleport the cross-dimension teleport object.
     */
    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        teleport.playerComponent().loadPlayerSavedInventory();
    }

    /**
     * Duration of the level entry/exit transition.
     * @return the duration in ticks.
     */
    @Override
    public int getTransitionDuration() {
        return 40;
    }

}
