package net.dark.spv_addon.world.levels.custom;

import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.level188.Level188ChunkGenerator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class Level105BackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();

    public Level105BackroomsLevel() {
        super("level105", Level188ChunkGenerator.CODEC, new Vec3d(16, 60, 16), BackroomsLevels.LEVEL105_WORLD_KEY, "spv_addon");
    }

    @Override
    public void register() {
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
    public boolean transitionOut(CrossDimensionTeleport teleport) {
        return teleport.playerComponent().player.isSneaking();
    }

    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        teleport.playerComponent().loadPlayerSavedInventory();
    }

}
