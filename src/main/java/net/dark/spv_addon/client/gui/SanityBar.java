package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SanityBar implements HudRenderCallback {

    private static final Identifier SANITY_ICONS = new Identifier("spv_addon", "textures/gui/sanity.png");

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        SanityComponent sanity = InitializeComponents.SANITY.get(player);
        int current = sanity.getSanityLevel();

        RenderSystem.enableBlend();
        int x = 5;
        int y = 5;

        int iconSize = 28;
        int maxSanity = 100;
        float fillRatio = (float) current / maxSanity;
        int filledHeight = Math.round(fillRatio * iconSize);

        context.drawTexture(SANITY_ICONS, x, y, 0, 0, 32, iconSize, 64, 32);
        context.drawTexture(SANITY_ICONS, x, y + (iconSize - filledHeight), 32, iconSize - filledHeight, 32, filledHeight, 64, 32);

        context.drawText(client.textRenderer, Text.literal(current + "%"), x, y + iconSize + 2, 0xFFFFFF, false);
        RenderSystem.disableBlend();
    }

}
