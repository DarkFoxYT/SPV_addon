package net.dark.spv_addon.init.crawl;

import net.dark.spv_addon.init.config.ServerConfig;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Shared crawl state validator to keep crawl transitions stable and predictable.
 */
public final class CrawlStateController {
    private CrawlStateController() {
    }

    public static boolean canUseCrawl(PlayerEntity player) {
        if (player == null) {
            return false;
        }
        if (player.isFallFlying() || player.hasVehicle() || player.isSpectator()) {
            return false;
        }
        if (!player.getWorld().isClient && player.getWorld().getServer() != null) {
            return ServerConfig.isCrawlingEnabled(player.getWorld().getServer());
        }
        return true;
    }

    public static EntityPose resolvePose(PlayerEntity player, EntityPose requestedPose, boolean crawlRequested) {
        if (!canUseCrawl(player)) {
            return requestedPose;
        }

        boolean swimming = player.isSwimming() || player.isTouchingWater();
        if (swimming) {
            return EntityPose.SWIMMING;
        }

        boolean canStand = player.wouldPoseNotCollide(EntityPose.STANDING);
        if (crawlRequested) {
            return CrawlSystem.Shared.CRAWLING;
        }

        if (requestedPose == EntityPose.SWIMMING) {
            return CrawlSystem.Shared.CRAWLING;
        }

        if (!canStand && requestedPose == EntityPose.STANDING) {
            return CrawlSystem.Shared.CRAWLING;
        }

        return requestedPose;
    }
}
