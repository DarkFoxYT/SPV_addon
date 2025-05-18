package net.dark.spv_addon.world.levels.custom;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.Level5ChunkGenerator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class Level5BackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();

    public Level5BackroomsLevel() {
        super("level5", Level5ChunkGenerator.CODEC, new Vec3d(0, 20.0, 0), BackroomsLevels.LEVEL5_WORLD_KEY, "spv_addon");
    }

    @Override
    public void register() {
    }

    @Override
    public int nextEventDelay() {
        return this.random.nextBetween(1200, 2400);
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
