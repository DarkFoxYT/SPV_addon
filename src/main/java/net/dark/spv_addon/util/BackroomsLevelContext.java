package net.dark.spv_addon.util;

import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class BackroomsLevelContext {
    private BackroomsLevelContext() {
    }

    public static double aggressionMultiplier(World world) {
        if (world == null) {
            return 1.0;
        }
        if (world.getRegistryKey().equals(BackroomsLevels.GLITCHED_WORLD_KEY)) {
            return 1.28;
        }
        if (world.getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY)) {
            return 1.18;
        }
        if (world.getRegistryKey().equals(BackroomsLevels.LEVEL_IKEA_WORLD_KEY)) {
            return 1.08;
        }
        if (world.getRegistryKey().equals(BackroomsLevels.LEVEL_KITTY_WORLD_KEY)) {
            return 0.92;
        }
        if (world.getRegistryKey().equals(com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY)) {
            return 0.96;
        }
        return 1.0;
    }

    public static int searchRadiusBonus(World world) {
        if (world == null) {
            return 0;
        }
        if (world.getRegistryKey().equals(BackroomsLevels.GLITCHED_WORLD_KEY)) {
            return 6;
        }
        if (world.getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY)) {
            return 4;
        }
        return 0;
    }

    public static double stalkingBias(World world) {
        if (world == null) {
            return 0.0;
        }
        if (world.getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY)) {
            return 0.16;
        }
        if (world.getRegistryKey().equals(BackroomsLevels.GLITCHED_WORLD_KEY)) {
            return 0.12;
        }
        return 0.0;
    }

    public static double darknessScore(Entity entity, BlockPos pos) {
        if (entity == null || entity.getWorld() == null || pos == null) {
            return 0.0;
        }
        int light = entity.getWorld().getLightLevel(pos);
        return 1.0 - (light / 15.0);
    }
}
