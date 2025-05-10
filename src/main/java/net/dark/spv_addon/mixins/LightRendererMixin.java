// net.dark.spv_addon.mixin.LightRendererMixin.java

package net.dark.spv_addon.mixins;

import foundry.veil.api.client.render.deferred.light.Light;
import foundry.veil.api.client.render.deferred.light.PointLight;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import net.dark.spv_addon.Additions.Sanity.SanityLightStore;
import net.dark.spv_addon.client.SanityLightTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LightRenderer.class, remap = false)
public class LightRendererMixin {

    @Inject(method = "addLight", at = @At("HEAD"))
    private void onAddLight(Light light, CallbackInfo ci) {
        if (light instanceof PointLight pointLight) {
            SanityLightStore.add(pointLight);
        }
    }

    @Inject(method = "removeLight", at = @At("HEAD"))
    private void onRemoveLight(Light light, CallbackInfo ci) {
        if (light instanceof PointLight pointLight) {
            SanityLightStore.remove(pointLight);
        }
    }
}