// DarknessManager.java

package net.dark.spv_addon.Additions.Sanity;

import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.registry.SanityLightStore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class DarknessManager {

    private static final int SANITY_LIGHT_RADIUS = 10;
    private static final int DRAIN_AMOUNT = 1;
    private static final int RESTORE_AMOUNT = 0;

    public static void tickPlayer(ServerPlayerEntity player) {
        SanityComponent sanity = InitializeComponents.SANITY.get(player);
        BlockPos playerPos = player.getBlockPos();

        boolean isNearLight = SanityLightStore.isNearLight(playerPos, SANITY_LIGHT_RADIUS);

        if (!isNearLight && sanity.getSanityLevel() > 0) {
            sanity.setSanityLevel(sanity.getSanityLevel() - DRAIN_AMOUNT);
        } else {
            if (sanity.getSanityLevel() > 0) {
                sanity.setSanityLevel(sanity.getSanityLevel() - DRAIN_AMOUNT);
            }
        }
    }
}
