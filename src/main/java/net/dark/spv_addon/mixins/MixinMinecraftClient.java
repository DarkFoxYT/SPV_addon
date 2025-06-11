package net.dark.spv_addon.mixins;

import net.dark.spv_addon.client.gui.CustomDeathScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof DeathScreen) {
            MinecraftClient mc = MinecraftClient.getInstance();
            String name = mc.player != null ? mc.player.getName().getString() : "Unknown";
            mc.setScreen(new CustomDeathScreen(name));
            ci.cancel();
        }
    }
}