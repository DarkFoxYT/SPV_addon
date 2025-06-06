package net.dark.spv_addon.world.levels.custom;

import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.kitty.KittyChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class LevelKittyBackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();

    public LevelKittyBackroomsLevel() {
        super("level_kitty", KittyChunkGenerator.CODEC, new Vec3d(20, 1, 15), BackroomsLevels.LEVEL_KITTY_WORLD_KEY, "spv_addon");

        this.registerTransition((world, playerComponent, from) -> {
            List<CrossDimensionTeleport> playerList = new ArrayList();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties)((MinecraftDedicatedServer)world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof LevelKittyBackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius && playerComponent.player.getWorld().getRegistryKey() == BackroomsLevels.LEVEL_KITTY_WORLD_KEY) {
                playerList.add(new BackroomsLevel.CrossDimensionTeleport(playerComponent.player.getWorld(), playerComponent, this.getSpawnPos(), BackroomsLevels.LEVEL_KITTY_BACKROOMS_LEVEL, com.sp.init.BackroomsLevels.LEVEL324_BACKROOMS_LEVEL));
            }

            return playerList;
        }, "kitty -> level324");
    }



    @Override
    public void register() {

        events.add(HaHvavCustomEvent::new);
    }

    public void tick(net.minecraft.server.world.ServerWorld world) {
        ensureSingleKitty(world);
    }
    /**
     * Indicates if the flashlight (torch) is allowed in this level.
     * @return BoolTextPair containing the permission and a message.
     */
    @Override
    public BoolTextPair allowsTorch() {
        return new BoolTextPair(false, Text.translatable("Flashlight twitchin in this level."));
    }

    @Override
    public int nextEventDelay() {
        return this.random.nextBetween(100000, 100000);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
    }
    /**
     * Ensure that there is only one Kitty entity in the world.
     * If no Kitty exists, it spawns one at the specified coordinates.
     *
     * @param world The server world where the Kitty should be checked/spawned.
     */
    public static void ensureSingleKitty(net.minecraft.server.world.ServerWorld world) {
        // Vérifie s'il y a déjà un Kitty
        boolean kittyExists = world.getEntitiesByClass(
                net.dark.spv_addon.entities.custom.KittyEntity.class,
                new net.minecraft.util.math.Box(0, 0, 0, 1000, 256, 1000), // Large zone
                e -> true
        ).size() > 0;

        if (!kittyExists) {
            // Spawn Kitty à 15 1 15
            var kitty = new net.dark.spv_addon.entities.custom.KittyEntity(
                    net.dark.spv_addon.init.ModEntities.KITTY, world
            );
            kitty.refreshPositionAndAngles(15, 1, 15, 0, 0);
            world.spawnEntity(kitty);
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
    }

    @Override
    public boolean transitionOut(CrossDimensionTeleport teleport) {
        return teleport.playerComponent().player.isSneaking();
    }

    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        teleport.playerComponent().loadPlayerSavedInventory();
    }

    @Override
    public int getTransitionDuration() {
        return 40;
    }

}
