package net.dark.spv_addon.mixins.sky;

import net.dark.spv_addon.client.CustomSkyboxRenderer;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public abstract class SkyboxMixin {

    @Inject(
            method = "renderSky(Lnet/minecraft/client/render/BufferBuilder;F)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void renderCustomSkybox(BufferBuilder builder, float tickDelta, CallbackInfoReturnable<BufferBuilder.BuiltBuffer> cir) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world != null && mc.world.getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY)) {
            CustomSkyboxRenderer.render(new MatrixStack(), 0);
            cir.setReturnValue(null);
        }
    }
}

