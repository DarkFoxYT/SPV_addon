package net.dark.spv_addon.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SanityLightEmitter {
    int sPV_addon$getSanityRadius(World world, BlockPos pos);

    static boolean isPlayerNearSanityLight(World world, BlockPos playerPos) {
        int checkRadius = 16;
        int verticalMargin = 5; // Met 0 si tu veux vraiment que Y strictement égal
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int dx = -checkRadius; dx <= checkRadius; dx++) {
            for (int dz = -checkRadius; dz <= checkRadius; dz++) {
                for (int dy = -verticalMargin; dy <= verticalMargin; dy++) {
                    mutablePos.set(playerPos.getX() + dx, playerPos.getY() + dy, playerPos.getZ() + dz);
                    BlockState state = world.getBlockState(mutablePos);
                    Block block = state.getBlock();

                    if (block instanceof SanityLightEmitter sanity) {
                        int radius = sanity.sPV_addon$getSanityRadius(world, mutablePos);
                        int dX = mutablePos.getX() - playerPos.getX();
                        int dZ = mutablePos.getZ() - playerPos.getZ();
                        if ((dX * dX + dZ * dZ) <= radius * radius) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
