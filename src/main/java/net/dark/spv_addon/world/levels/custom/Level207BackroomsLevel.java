package net.dark.spv_addon.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.kitty.KittyChunkGenerator;
import net.dark.spv_addon.world.generation.level207.Level207ChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.dark.spv_addon.world.levels.custom.events.Level207AmbienceEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
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
            List<BackroomsLevel.CrossDimensionTeleport> playerList = new ArrayList();
            if (from instanceof Level1BackroomsLevel && playerComponent.player.getPos().getY() <= (double)12.0F && playerComponent.player.isOnGround()) {
                for(PlayerEntity player : playerComponent.player.getWorld().getPlayers()) {
                    PlayerComponent otherPlayerComponent = (PlayerComponent) InitializeComponents.PLAYER.get(player);
                    if (player.getWorld().getRegistryKey() == BackroomsLevels.LEVEL207_WORLD_KEY) {
                        playerList.add(new BackroomsLevel.CrossDimensionTeleport(player.getWorld(), otherPlayerComponent, this.calculateLevel2TeleportCoords(player, playerComponent.player.getChunkPos()), BackroomsLevels.LEVEL207_BACKROOMS_LEVEL, com.sp.init.BackroomsLevels.LEVEL2_BACKROOMS_LEVEL));
                    }
                }
            }

            return playerList;
        }, "level207 -> level2");
    }

    private Vec3d calculateLevel2TeleportCoords(PlayerEntity player, ChunkPos chunkPos) {
        if (chunkPos.x == player.getChunkPos().x && chunkPos.z == player.getChunkPos().z) {
            int chunkX = chunkPos.getStartX();
            int chunkZ = chunkPos.getStartZ();
            double playerX = player.getPos().x;
            double playerZ = player.getPos().z;
            return new Vec3d(playerX - (double)chunkX - (double)1.0F, player.getPos().y + (double)8.0F, playerZ - (double)chunkZ);
        } else {
            return this.getSpawnPos();
        }
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
        events.add(Level207AmbienceEvent::new);
    }

    /**
     * Determines the delay before the next event.
     * @return the delay in ticks.
     */
    @Override
    public int nextEventDelay() {
        return this.random.nextBetween(0, 0);
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


    public boolean transitionOut(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {
        if (!crossDimensionTeleport.world().isClient() && !crossDimensionTeleport.playerComponent().isTeleporting()) {
            SPBRevamped.sendLevelTransitionLightsOutPacket((ServerPlayerEntity)crossDimensionTeleport.playerComponent().player, 80);
        }

        return crossDimensionTeleport.playerComponent().player.isOnGround();
    }

    public void transitionIn(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {
    }

    public int getTransitionDuration() {
        return 30;
    }

}
