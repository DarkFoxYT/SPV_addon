package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.dark.spv_addon.Additions.battery.BatteryManager;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.config.SpvAddonConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import static net.dark.spv_addon.Spv_addon.MOD_ID;

public class BatteryHud implements HudRenderCallback {
    private static final Identifier BATTERY_1 = new Identifier(MOD_ID, "textures/gui/battery1.png");
    private static final Identifier BATTERY_2 = new Identifier(MOD_ID, "textures/gui/battery2.png");
    private static final int BAR_W = 44, BAR_H = 44;
    private static final float SCALE = 0.8f;

    public static void register() {
        HudRenderCallback.EVENT.register(new BatteryHud());
    }

    @Override
    public void onHudRender(DrawContext dc, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        // Hide battery HUD when sanity is below 25%
        try {
            var sanityOpt = SanityComponent.KEY1.maybeGet(player);
            if (sanityOpt.isPresent() && sanityOpt.get().getSanityLevel() < 25) {
                return; // Don't show battery HUD at low sanity
            }
        } catch (Exception e) {
            // Continue if sanity check fails
        }

        int level = BatteryManager.getBattery(player.getUuid());
        int health = BatteryManager.getBatteryHealth(player.getUuid());
        PlayerComponent comp = InitializeComponents.PLAYER.getNullable(player);

        if (level != 0 && (comp == null || !comp.isFlashLightOn())) return;

        float norm = Math.max(0, Math.min(level, 100)) / 100f;
        int filledHeight = Math.round(norm * BAR_H);

        // Enhanced visual feedback
        float alpha = (level <= 15) ? getPulseAlpha() : 1f;
        float[] color = getBatteryColor(level, health);

        int sw = client.getWindow().getScaledWidth();
        int x = sw - (int) (BAR_W * SCALE) - 16;
        int y = 16;


        dc.getMatrices().push();
        dc.getMatrices().translate(x, y, 0);
        dc.getMatrices().scale(SCALE, SCALE, SCALE);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        dc.drawTexture(BATTERY_1, 0, 0, 0, 0, BAR_W, BAR_H, 44, 44);

        if (filledHeight > 0) {
            // Apply color tinting based on battery condition
            RenderSystem.setShaderColor(color[0], color[1], color[2], alpha);
            dc.drawTexture(BATTERY_2, 0, BAR_H - filledHeight, 0, BAR_H - filledHeight, BAR_W, filledHeight, 44, 44);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        dc.getMatrices().pop();

        // Enhanced battery display with status
        String txt = level + "%";
        String statusText = BatteryManager.getBatteryStatusText(player.getUuid());
        String healthText = health < 100 ? "(" + health + "% health)" : "";

        int tw = client.textRenderer.getWidth(txt);
        int statusTw = client.textRenderer.getWidth(statusText);
        int healthTw = client.textRenderer.getWidth(healthText);

        int tx = x + (int) (BAR_W * SCALE) / 2 - tw / 2;
        int ty = y + (int) (BAR_H * SCALE) + 2;

        // Main percentage text
        int textColor = getBatteryTextColor(level, health);
        dc.drawText(client.textRenderer, txt, tx, ty, textColor, true);

        // Status text
        if (!statusText.isEmpty()) {
            int statusTx = x + (int) (BAR_W * SCALE) / 2 - statusTw / 2;
            dc.drawText(client.textRenderer, statusText, statusTx, ty + 12, textColor, true);
        }

        // Health text (if degraded)
        if (!healthText.isEmpty()) {
            int healthTx = x + (int) (BAR_W * SCALE) / 2 - healthTw / 2;
            dc.drawText(client.textRenderer, healthText, healthTx, ty + 24, 0xFFAA00, true);
        }
    }

    private float getPulseAlpha() {
        double t = Util.getMeasuringTimeMs() / 600.0;
        double sway = (Math.sin(t) + 1.0) / 2.0;
        return 0.3f + (float) (sway * 0.7f);
    }

    /**
     * Get battery color based on level and health using configuration presets
     */
    private float[] getBatteryColor(int level, int health) {
        return SpvAddonConfig.getBatteryColorFloat(level, health, false);
    }

    /**
     * Get battery text color based on level and health
     */
    private int getBatteryTextColor(int level, int health) {
        if (level <= 0) {
            return 0x666666; // Dark gray for dead
        } else if (level <= 5) {
            return 0xFF4444; // Red for critical
        } else if (level <= 15) {
            return 0xFFAA44; // Orange for low
        } else if (health <= 30) {
            return 0xFFFF66; // Yellow for degraded health
        } else {
            return 0xFFFFFF; // White for good
        }
    }
}
