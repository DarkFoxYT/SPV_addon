package net.dark.spv_addon.world.levels.custom;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.run.RunChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class LevelRUNBackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();

    public LevelRUNBackroomsLevel() {
        super("run", RunChunkGenerator.CODEC, new Vec3d(7.5, 1, 7.5), BackroomsLevels.LEVELRUN_WORLD_KEY, "spv_addon");
    }

    @Override
    public void register() {

        // Ajoute des events custom ici si besoin
        events.add(HaHvavCustomEvent::new);

    }

    @Override
    public int nextEventDelay() {
        return this.random.nextBetween(2000, 3000);
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
