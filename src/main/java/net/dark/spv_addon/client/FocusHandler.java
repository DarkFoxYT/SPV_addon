// FocusHandler.java
package net.dark.spv_addon.client;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;

public class FocusHandler {
    public static final Identifier FOCUS_POST = new Identifier("spv_addon", "camera_focus");
    public static float currentFocus = 8.0f;   // Lissé (ce qu’on envoie au shader)
    public static float targetFocus = 8.0f;    // Calculé chaque tick

    // Pour le lerp (entre 0 et 1), 0.1 = lent, 0.3 = rapide
    private static final float FOCUS_LERP_SPEED = 0.13f;

    public static void register() {
        // PAS de manipulation Veil ici !

        // Tick (activation pipeline, calcul focus)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            VeilRenderer renderer = VeilRenderSystem.renderer();
            if (renderer == null) return;

            PostProcessingManager ppm = renderer.getPostProcessingManager();

            // Toujours activer le focus pipeline pour test (ou mets ta condition ici)
            if (!ppm.isActive(FOCUS_POST)) {
                ppm.add(FOCUS_POST);
            }

            // Calcul autofocus
            if (client.crosshairTarget != null) {
                HitResult hit = client.crosshairTarget;
                if (hit.getType() == HitResult.Type.BLOCK || hit.getType() == HitResult.Type.ENTITY) {
                    double d = client.player.squaredDistanceTo(hit.getPos());
                    targetFocus = (float) Math.sqrt(d);
                } else {
                    targetFocus = 8.0f;
                }
            }

            // Interpolation smooth
            currentFocus += (targetFocus - currentFocus) * FOCUS_LERP_SPEED;
        });

        // Passe les uniforms au shader
        VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
            if (FOCUS_POST.equals(name)) {
                ShaderProgram shader = context.getShader(new Identifier("spv_addon:camera_focus"));
                if (shader != null) {
                    shader.setFloat("FocusDistance", currentFocus);
                    shader.setFloat("FocusRange", 4.2f);
                    shader.setFloat("BlurStrength", 7.5f);
                }
            }
        });

        // HUD Focus (pas besoin de Veil ici)
        //HudRenderCallback.EVENT.register(FocusHandler::drawHud);
    }


    private static void drawHud(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int w = client.getWindow().getScaledWidth();
        int h = client.getWindow().getScaledHeight();

        // Centre de l'écran
        int cx = w / 2;
        int cy = h / 2;
        int r = 15;
        int color = 0x66FFDD44; // Jaune-orangé, semi-transparent

        // -- Cercle simple (approx avec points) --
        int lastX = cx + r, lastY = cy;
        for (int i = 1; i <= 32; i++) {
            double angle = 2 * Math.PI * i / 32;
            int x = cx + (int) (r * Math.cos(angle));
            int y = cy + (int) (r * Math.sin(angle));
            drawLineSimple(drawContext, lastX, lastY, x, y, color);
            lastX = x;
            lastY = y;
        }

        // -- Petite croix au centre --
        int crossLen = 5;
        drawContext.drawHorizontalLine(cx - crossLen, cx + crossLen, cy, color);
        drawContext.drawVerticalLine(cx, cy - crossLen, cy + crossLen, color);

        // -- Texte info focus (distance) --
        String txt = String.format("Focus: %.1f blocks", currentFocus);
        drawContext.drawText(client.textRenderer, txt, cx + r + 8, cy - 5, 0xFFFFFFAA, true);
    }

    // Petit utilitaire pour dessiner un trait (algorithme de Bresenham simplifié)
    private static void drawLineSimple(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            ctx.fill(x1, y1, x1 + 1, y1 + 1, color); // Pixel
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
    }
}
