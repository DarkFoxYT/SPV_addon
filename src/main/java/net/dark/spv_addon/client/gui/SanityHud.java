// File: net/dark/spv_addon/client/gui/SanityHud.java

package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import static net.dark.spv_addon.Spv_addon.MOD_ID;

public class SanityHud implements HudRenderCallback {
    // textures for 0%, 25%, 50%, 75%, 100%
    private static final Identifier TEX_FULL = new Identifier(MOD_ID, "textures/gui/sanity_100.png");
    private static final Identifier TEX_75  = new Identifier(MOD_ID, "textures/gui/sanity_75.png");
    private static final Identifier TEX_50  = new Identifier(MOD_ID, "textures/gui/sanity_50.png");
    private static final Identifier TEX_25  = new Identifier(MOD_ID, "textures/gui/sanity_25.png");
    private static final Identifier TEX_0   = new Identifier(MOD_ID, "textures/gui/sanity_0.png");

    private static final int ICON_W = 88, ICON_H = 64;
    private static final float SCALE = 0.25f;

    /** Are we drawing at all? */
    public static boolean enabled = true;

    /** Anchor positions for the icon */
    public enum Anchor {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        CENTER_BOTTOM
    }

    /** Tweak these at runtime if you like (ModMenu, command, config…) */
    public static Anchor   anchor     = Anchor.CENTER_BOTTOM;
    public static int      margin     = 5;    // pixels from edges
    public static int      chatOffset = 50;   // lift it this many px above bottom chat

    @Override
    public void onHudRender(DrawContext dc, float tickDelta) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // get component
        SanityComponent sc = InitializeComponents.SANITY.get(client.player);
        int s = sc.getSanity(); // 0–100

        // pick texture
        Identifier tex = (s == 0)   ? TEX_0
                : (s <= 25)  ? TEX_25
                : (s <= 50)  ? TEX_50
                : (s <= 75)  ? TEX_75
                : TEX_FULL;

        // pulse alpha when low
        float alpha = 1f;
        if (s <= 25) {
            double t    = Util.getMeasuringTimeMs() / 500.0;
            double sway = (Math.sin(t) + 1.0) / 2.0;
            alpha = 0.25f + (float)(sway * 0.75f);
        }

        int sw    = client.getWindow().getScaledWidth();
        int sh    = client.getWindow().getScaledHeight();
        int iW    = (int)(ICON_W * SCALE);
        int iH    = (int)(ICON_H * SCALE);

        // determine x,y by anchor
        int x, y;
        switch (anchor) {
            case TOP_LEFT:
                x = margin;
                y = margin;
                break;
            case TOP_RIGHT:
                x = sw - iW - margin;
                y = margin;
                break;
            case BOTTOM_LEFT:
                x = margin;
                y = sh - iH - chatOffset - margin;
                break;
            case BOTTOM_RIGHT:
                x = sw - iW - margin;
                y = sh - iH - chatOffset - margin;
                break;
            case CENTER_BOTTOM:
            default:
                x = (sw - iW) / 2;
                y = sh - iH - chatOffset - margin;
                break;
        }

        // draw
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

    /** Call this once in your ClientModInitializer: */
    public static void register() {
        HudRenderCallback.EVENT.register(new SanityHud());
    }
}
