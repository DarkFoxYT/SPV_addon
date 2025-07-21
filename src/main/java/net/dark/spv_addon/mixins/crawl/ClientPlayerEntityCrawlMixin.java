package net.dark.spv_addon.mixins.crawl;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.dark.spv_addon.crawl.CrawlClient;
import net.dark.spv_addon.crawl.CrawlSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityCrawlMixin extends AbstractClientPlayerEntity {
    
    @Shadow public Input input;
    @Shadow protected int ticksLeftToDoubleTapSprint;
    
    public ClientPlayerEntityCrawlMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    
    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void beforeTickMovement(CallbackInfo ci) {
        if (MinecraftClient.getInstance().player.getPose() == CrawlSystem.Shared.CRAWLING) {
            this.input.sneaking = false;
            this.ticksLeftToDoubleTapSprint = 0;
        }
    }
    
    @Inject(
        method = "tickMovement",
        at = @At(value = "INVOKE", target = "net/minecraft/client/network/AbstractClientPlayerEntity.tickMovement()V")
    )
    public void beforeSuperMovementTick(CallbackInfo ci) {
        boolean wantsToCrawl = CrawlClient.crawlKey.isPressed();
        
        if (wantsToCrawl != getDataTracker().get(CrawlSystem.Shared.CRAWL_REQUEST)) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(wantsToCrawl);
            ClientPlayNetworking.send(CrawlSystem.CRAWL_IDENTIFIER, buf);
            getDataTracker().set(CrawlSystem.Shared.CRAWL_REQUEST, wantsToCrawl);
        }
        
        if (getPose() == CrawlSystem.Shared.CRAWLING) {
            setSprinting(false);
        }
    }
}
