// File: net/dark/spv_addon/client/gui/ThirstHud.java

package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.ThirstComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import static net.dark.spv_addon.Spv_addon.MOD_ID;

public class ThirstHud implements HudRenderCallback {
    private static final Identifier TEX_FULL = new Identifier(MOD_ID, "textures/gui/thirst_100.png");
    private static final Identifier TEX_75   = new Identifier(MOD_ID, "textures/gui/thirst_75.png");
    private static final Identifier TEX_50   = new Identifier(MOD_ID, "textures/gui/thirst_50.png");
    private static final Identifier TEX_25   = new Identifier(MOD_ID, "textures/gui/thirst_25.png");
    private static final Identifier TEX_0    = new Identifier(MOD_ID, "textures/gui/thirst_0.png");

    private static final int ICON_W = 88, ICON_H = 64;
    private static final float SCALE = 0.25f;

    /** Toggle on/off at runtime (e.g. via `/thirst enabled true/false`) */
    public static boolean enabled = true;

    @Override
    public void onHudRender(DrawContext dc, float tickDelta) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        // **no cast**: read the synced component from the local player
        ThirstComponent thirstComp = InitializeComponents.THIRST.get(player);
        int level = thirstComp.getThirst(); // 0–100

        Identifier tex = (level == 0)   ? TEX_0
                : (level <= 25) ? TEX_25
                : (level <= 50) ? TEX_50
                : (level <= 75) ? TEX_75
                : TEX_FULL;

        // pulse when critically low
        float alpha = 1f;
        if (level <= 25) {
            double t    = Util.getMeasuringTimeMs() / 500.0;
            double sway = (Math.sin(t) + 1.0) / 2.0;
            alpha = 0.25f + (float)(sway * 0.75f);
        }

        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        int x  = (sw - (int)(ICON_W * SCALE)) / 2;
        int y  = sh - 55; // same as your template

        // draw icon
        dc.getMatrices().push();
        dc.getMatrices().translate(x, y, 0);
        dc.getMatrices().scale(SCALE, SCALE, 1f);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        dc.drawTexture(tex, 0, 0, 0, 0, ICON_W, ICON_H, ICON_W, ICON_H);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        dc.getMatrices().pop();
    }

    /** Call once in your ClientModInitializer **/
    public static void register() {
        HudRenderCallback.EVENT.register(new ThirstHud());
    }
}
