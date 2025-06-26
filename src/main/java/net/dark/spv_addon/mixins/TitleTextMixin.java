package net.dark.spv_addon.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevampedClient;
import com.sp.render.camera.CutsceneManager;
import com.sp.render.gui.TitleText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleText.class)
public class TitleTextMixin {

    @Inject(method = "onHudRender", at = @At("TAIL"))
    private void injectVolatileCredit(DrawContext drawContext, float tickDelta, CallbackInfo ci) {
        CutsceneManager cutsceneManager = SPBRevampedClient.getCutsceneManager();
        if (!cutsceneManager.backroomsBySP || cutsceneManager.blackScreen.isBlackScreen) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        Text prefix = Text.literal("and The Volatile Addon Made by: ").formatted(Formatting.DARK_RED);
        Text suffix = Text.literal("DarkFox Studios").formatted(Formatting.DARK_PURPLE);

        String fullText = prefix.getString() + suffix.getString();
        int fullWidth = client.textRenderer.getWidth(fullText);

        float scale = 0.8F;
        int yOffset = (screenHeight / 2) + 20;

        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();

        // Center scaling from middle
        matrices.translate(screenWidth / 2f, yOffset, 0);
        matrices.scale(scale, scale, scale);
        matrices.translate(-fullWidth / 2f, 0, 0);

        RenderSystem.enableBlend();
        drawContext.drawText(client.textRenderer, prefix, 0, 0, 0xDC143C, true);
        drawContext.drawText(client.textRenderer, suffix, client.textRenderer.getWidth(prefix.getString()), 0, 0xAA00FF, true);
        RenderSystem.disableBlend();

        matrices.pop();
    }
}
