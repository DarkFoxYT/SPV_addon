// File: net/dark/spv_addon/client/gui/BatteryHudRenderer.java

package net.dark.spv_addon.client.gui;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.dark.spv_addon.battery.BatteryManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.mojang.blaze3d.systems.RenderSystem;

public class BatteryHudRenderer implements HudRenderCallback {
    private static final Identifier BATTERY_ICONS = new Identifier("spv_addon", "textures/gui/battery_warning.png");
    private Long fadeStart;
    private float fadeTimer = 0.0f;

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        int battery = BatteryManager.getBattery(player.getUuid());

        // Only render under 10%
        if (battery <= 10) {
            this.fadeStart = null;
            this.fadeTimer = 0.0f;

            drawBatteryWarning(drawContext);
        } else if (this.fadeTimer < 1.0f) {
            if (this.fadeStart == null) {
                this.fadeStart = Util.getMeasuringTimeMs();
            }
            this.fadeTimer = Math.min((float)(Util.getMeasuringTimeMs() - this.fadeStart) / 1000F, 1.0F);
            drawBatteryWarning(drawContext, 1.0F - fadeTimer); // Fade out
        }
    }

    private void drawBatteryWarning(DrawContext context) {
        drawBatteryWarning(context, 0.25F); // constant alpha when under 10%
    }

    private void drawBatteryWarning(DrawContext context, float alpha) {
        int width = 48, height = 64;
        RenderSystem.enableBlend();
        context.getMatrices().push();
        context.getMatrices().translate((float)(context.getScaledWindowWidth() / 2), (float)(context.getScaledWindowHeight() / 2), 0.0F);
        context.getMatrices().scale(0.2F, 0.2F, 0.2F);

        context.setShaderColor(1F, 1F, 1F, alpha);
        context.drawTexture(BATTERY_ICONS, width / 2, -height / 2, width, 0, 64, height);
        context.drawTexture(BATTERY_ICONS, width / 2 + 4, -height / 2, 0, 0, width, height);

        context.setShaderColor(1F, 1F, 1F, 1F);
        context.getMatrices().pop();
        RenderSystem.disableBlend();
    }
}
