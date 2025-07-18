package net.dark.spv_addon.world.levels.custom;

import com.sp.entity.custom.SmilerEntity;
import com.sp.init.ModEntities;
import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.run.RunChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class LevelRUNBackroomsLevel extends BackroomsLevel {
    private static final int SMILER_SPAWN_INTERVAL = 40;
    private final Random random = Random.create();
    private int smilerSpawnTick = 0;

    public LevelRUNBackroomsLevel() {
        super("run", RunChunkGenerator.CODEC, new Vec3d(-7.5, 1, 7.5), BackroomsLevels.LEVELRUN_WORLD_KEY, "spv_addon");
    }

    @Override
    public void register() {

        this.registerEvents("empty", HaHvavCustomEvent::new);

    }

    public void tick(ServerWorld world) {
        smilerSpawnTick++;
        if (smilerSpawnTick >= SMILER_SPAWN_INTERVAL) {
            smilerSpawnTick = 0;
            int count = 2 + world.random.nextInt(3);
            for (int i = 0; i < count; i++) {
                double x = 10 + world.random.nextDouble() * (world.getWorldBorder().getSize() - 20);
                double y = 1;
                double z = 10 + world.random.nextDouble() * (world.getWorldBorder().getSize() - 20);
                SmilerEntity smiler = new SmilerEntity(ModEntities.SMILER_ENTITY, world);
                smiler.refreshPositionAndAngles(x, y, z, world.random.nextFloat() * 360F, 0);
                world.spawnEntity(smiler);
            }
        }
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
