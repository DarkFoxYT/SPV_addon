package net.dark.spv_addon.world.levels.custom;

import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.kitty.KittyChunkGenerator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class LevelKittyBackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();

    public LevelKittyBackroomsLevel() {
        super("level_kitty", KittyChunkGenerator.CODEC, new Vec3d(7, 1, 7), BackroomsLevels.LEVEL_KITTY_WORLD_KEY, "spv_addon");
    }

    @Override
    public void register() {

    }

    @Override
    public int nextEventDelay() {
        return this.random.nextBetween(100000, 100000);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
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
