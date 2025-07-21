package net.dark.spv_addon.crawl;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Crawling system integrated from fewizz/crawl mod
 */
public class CrawlSystem {
    public static final Identifier CRAWL_IDENTIFIER = new Identifier("spv_addon", "crawl");
    
    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(CRAWL_IDENTIFIER, (server, player, handler, buf, responseSender) -> {
            boolean val = buf.readBoolean();
            server.execute(() -> player.getDataTracker().set(Shared.CRAWL_REQUEST, val));
        });
    }
    
    public static class Shared {
        // We'll use SWIMMING pose instead of creating a new one to avoid complexity
        public static final EntityPose CRAWLING = EntityPose.SWIMMING;
        public static final EntityDimensions CRAWLING_DIMENSIONS = new EntityDimensions(0.6F, 0.6F, false);
        public static final TrackedData<Boolean> CRAWL_REQUEST = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
