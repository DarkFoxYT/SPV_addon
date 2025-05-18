package net.dark.spv_addon.mixins;

import net.dark.spv_addon.client.CameraZoomHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    // On modifie le champ de vision du joueur pour appliquer le zoom
    @ModifyVariable(
            method = "getFov",
            at = @At(value = "STORE"), // le moment où la variable est stockée
            ordinal = 0,
            argsOnly = true)
    private float injectedZoom(float originalFov) {
        return originalFov / CameraZoomHandler.getCameraZoom();
    }
}
