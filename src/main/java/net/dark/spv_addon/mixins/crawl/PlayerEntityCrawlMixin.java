package net.dark.spv_addon.mixins.crawl;

import net.dark.spv_addon.init.config.SpvAddonConfig;
import net.dark.spv_addon.init.crawl.CrawlSystem;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityCrawlMixin extends LivingEntity {
    
    protected PlayerEntityCrawlMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    
    @Shadow @Final private PlayerAbilities abilities;
    
    @Inject(method = "initDataTracker", at = @At("HEAD"))
    public void onInitDataTracker(CallbackInfo ci) {
        getDataTracker().startTracking(CrawlSystem.Shared.CRAWL_REQUEST, false);
    }
    
    @ModifyArg(
        method = "updatePose",
        index = 0,
        at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.setPose(Lnet/minecraft/entity/EntityPose;)V")
    )
    public EntityPose onPreSetPose(EntityPose pose) {
        if (SpvAddonConfig.enableCrawling && !isFallFlying() && !this.isSpectator() && !this.hasVehicle() && !this.abilities.flying) {
            boolean requested = getDataTracker().get(CrawlSystem.Shared.CRAWL_REQUEST);
            boolean swimming = isSwimming() || isTouchingWater();

            if (requested) {
                if (!swimming) {
                    pose = CrawlSystem.Shared.CRAWLING;
                } else {
                    pose = EntityPose.SWIMMING;
                }
            } else if (pose == EntityPose.SWIMMING && !swimming) {
                pose = CrawlSystem.Shared.CRAWLING;
            }
        }
        return pose;
    }
    
    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    public void onGetActiveEyeHeight(EntityPose pose, net.minecraft.entity.EntityDimensions size, CallbackInfoReturnable<Float> cir) {
        if (pose == CrawlSystem.Shared.CRAWLING) {
            cir.setReturnValue(SpvAddonConfig.crawlingEyeHeight);
        }
    }
}
