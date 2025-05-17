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
    private static final BlockPos EXIT_POS = new BlockPos(48, 11, 48); // matching stairwell2_1 placement in chunkgen
    private static final double EXIT_RADIUS = 2.5;

    public Level5BackroomsLevel() {
        super("level5", Level5ChunkGenerator.CODEC, new Vec3d(0, 20.0, 0), BackroomsLevels.LEVEL5_WORLD_KEY, "spv_addon");
    }

    @Override
    public void register() {
        super.register();

        this.registerTransition((world, playerComponent, from) -> {
            List<CrossDimensionTeleport> transitions = new ArrayList<>();
            ServerPlayerEntity player = (ServerPlayerEntity) playerComponent.player;

            if (player.getWorld().getRegistryKey() == BackroomsLevels.LEVEL5_WORLD_KEY &&
                    player.squaredDistanceTo(EXIT_POS.getX(), EXIT_POS.getY(), EXIT_POS.getZ()) < EXIT_RADIUS * EXIT_RADIUS &&
                    player.isSneaking()) {

                for (ServerPlayerEntity p : player.getServerWorld().getPlayers()) {
                    PlayerComponent pc = InitializeComponents.PLAYER.get(p);
                    transitions.add(new CrossDimensionTeleport(
                            p.getServerWorld(),
                            pc,
                            com.sp.init.BackroomsLevels.LEVEL2_BACKROOMS_LEVEL.getSpawnPos(),
                            this,
                            com.sp.init.BackroomsLevels.LEVEL2_BACKROOMS_LEVEL // or whichever level you want
                    ));
                }
            }

            return transitions;
        }, "level5 → level2");
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
