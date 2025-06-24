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
    private final Random random = Random.create();
    private static final int SMILER_SPAWN_INTERVAL = 40;
    private int smilerSpawnTick = 0;

    public LevelRUNBackroomsLevel() {
        super("run", RunChunkGenerator.CODEC, new Vec3d(-7.5, 1, 7.5), BackroomsLevels.LEVELRUN_WORLD_KEY, "spv_addon");
    }

    @Override
    public void register() {

        events.add(HaHvavCustomEvent::new);

    }

    public void tick(ServerWorld world) {
        smilerSpawnTick++;
        if (smilerSpawnTick < SMILER_SPAWN_INTERVAL) return;

        smilerSpawnTick = 0;

        int spawnRadius = 15;
        int smilersPerPlayer = 2 + world.random.nextInt(3);

        world.getPlayers().forEach(player -> {
            for (int i = 0; i < smilersPerPlayer; i++) {
                double offsetX = random.nextBetween(-spawnRadius, spawnRadius);
                double offsetZ = random.nextBetween(-spawnRadius, spawnRadius);
                double spawnX = player.getX() + offsetX;
                double spawnZ = player.getZ() + offsetZ;
                double spawnY = player.getY();

                // Évite de spawner dans les murs
                if (!world.getBlockState(player.getBlockPos()).isAir()) continue;

                SmilerEntity smiler = new SmilerEntity(ModEntities.SMILER_ENTITY, world);
                smiler.refreshPositionAndAngles(spawnX, spawnY, spawnZ, random.nextFloat() * 360F, 0);
                world.spawnEntity(smiler);
            }
        });
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
