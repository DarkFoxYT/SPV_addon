package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dark.spv_addon.Spv_addonClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.UUID;

public class BatteryEmptyHUD implements net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback {

    private static final Identifier BATTERY_TEXTURE = new Identifier("spv_addon", "textures/gui/battery_empty.png");
    private Long fadeStart;
    private float fadeTimer;
    private boolean wasShown = false;
    private UUID lastPlayerId = null;

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null) return;

        UUID uuid = player.getUuid();
        int batteryLevel = Spv_addonClient.getLocalBatteryLevel();

        if (batteryLevel > 0) {
            this.wasShown = false;
            this.fadeStart = null;
            this.fadeTimer = 0f;
            return;
        }

        if (!wasShown || !uuid.equals(lastPlayerId)) {
            System.out.println("⚠ Battery empty for player " + player.getEntityName());
            wasShown = true;
            lastPlayerId = uuid;
        }

        int width = 44;
        int height = 64;

        RenderSystem.enableBlend();
        context.getMatrices().push();
        context.getMatrices().translate((float)(context.getScaledWindowWidth() / 2), (float)(context.getScaledWindowHeight() / 2), 0.0F);
        context.getMatrices().scale(0.2f, 0.2f, 0.2f);

        context.setShaderColor(1.0f, 0.0f, 0.0f, 0.8f); // Red glow
        context.drawTexture(BATTERY_TEXTURE, width / 2, -height / 2, 0, 0, width, height);

        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.getMatrices().pop();
        RenderSystem.disableBlend();
    }
}
