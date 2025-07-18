package net.dark.spv_addon.world.levels.custom;

import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.level188.Level188ChunkGenerator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class Level188BackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();

    public Level188BackroomsLevel() {
        super("level188", Level188ChunkGenerator.CODEC, new Vec3d(16, 50, 16), BackroomsLevels.LEVEL188_WORLD_KEY, "spv_addon");
    }

    @Override
    public boolean hasVanillaLighting() {
        return true;
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
