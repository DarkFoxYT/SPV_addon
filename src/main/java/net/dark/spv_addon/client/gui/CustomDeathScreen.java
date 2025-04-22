package net.dark.spv_addon.client.gui;


import com.mojang.blaze3d.systems.RenderSystem;
import net.dark.spv_addon.render.CutsceneManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Completely replaces the vanilla DeathScreen.
 * - No buttons
 * - Full‑screen image, pixel‑perfect
 * - Two lines of centered text: "<player> was" and "Never Found"
 * - 10s countdown, then auto‑respawn
 * - Static overlay fades in over that time
 */
@Environment(EnvType.CLIENT)
public class CustomDeathScreen extends Screen {
    private static final Identifier BACKGROUND = new Identifier("spv_addon",
            "textures/gui/death_full.png"
    );
    private static final Identifier STATIC_OVERLAY = new Identifier("spv_addon",
            "textures/gui/static_overlay.png"
    );
    private final String playerName;
    private int   ticksElapsed;

    public CustomDeathScreen(String playerName) {
        super(Text.empty());
        this.playerName = playerName;
        this.ticksElapsed = 0;
    }

    @Override
    public void tick() {
        // Advance counter and, after 200 ticks (~10s), respawn
        if (++ticksElapsed >= 200) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) {
                mc.player.requestRespawn();
            }
            mc.setScreen(null);
        }
    }

    public boolean isPauseScreen() {
        // Keep the client ticking so our tick() runs
        return false;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();

        // 1) Draw full‑screen background
        RenderSystem.enableBlend();
        ctx.drawTexture(
                BACKGROUND,
                0, 0,        // screen x,y
                0, 0,        // u,v
                w, h,        // draw width, height
                w, h         // texture width, height
        );
        RenderSystem.disableBlend();

        // 2) Draw player name line: "<name> was"
        String line1 = playerName + " was";
        int th = mc.textRenderer.fontHeight;
        int x1 = (w - mc.textRenderer.getWidth(line1)) / 2;
        int y1 = (h - th) / 2;
        ctx.drawText(mc.textRenderer, Text.literal(line1), x1, y1, 0xFFFFFF, false);

        // 3) Draw "Never Found" just below
        String line2 = "Never Found";
        int x2 = (w - mc.textRenderer.getWidth(line2)) / 2;
        int y2 = y1 + th + 2;
        ctx.drawText(mc.textRenderer, Text.literal(line2), x2, y2, 0xFFFFFF, false);

        // 4) Fade‑in static overlay: alpha from 0→255 over 200 ticks
        float frac = Math.min(ticksElapsed / 200f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, frac);
        ctx.drawTexture(
                STATIC_OVERLAY,
                0, 0,
                0, 0,
                w, h,
                w, h
        );
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }
}