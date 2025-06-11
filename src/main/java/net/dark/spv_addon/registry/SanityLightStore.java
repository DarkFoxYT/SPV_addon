package net.dark.spv_addon.registry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SanityLightStore {
    private static final Set<BlockPos> REGISTERED_LIGHT_BLOCKS = new HashSet<>();
    private static final int LIGHT_RADIUS = 15;
    private static final int RADIUS_SQ = LIGHT_RADIUS * LIGHT_RADIUS;

    public static void addLight(BlockPos pos) {
        REGISTERED_LIGHT_BLOCKS.add(pos.toImmutable());
    }

    public static void removeLight(BlockPos pos) {
        REGISTERED_LIGHT_BLOCKS.remove(pos);
    }

    public static Set<BlockPos> getLightBlocks() {
        return Collections.unmodifiableSet(REGISTERED_LIGHT_BLOCKS);
    }

    public static boolean isNearLight(BlockPos playerPos, int radius) {
        return REGISTERED_LIGHT_BLOCKS.stream()
                .anyMatch(lightPos -> lightPos.isWithinDistance(playerPos, radius));
    }

    public static boolean isPlayerInLightRange(World world, PlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        return REGISTERED_LIGHT_BLOCKS.stream()
                .anyMatch(lightSource -> lightSource.getSquaredDistance(playerPos) < RADIUS_SQ);
            }
        }

/* (pour blocs personnalisés si besoin)
@Override
public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
    if (!world.isClient) {
        SanityLightStore.addLight(pos);
    }
}

@Override
public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
    if (!world.isClient()) {
        SanityLightStore.removeLight(pos);
    }
    super.onBroken(world, pos, state);
}
 */