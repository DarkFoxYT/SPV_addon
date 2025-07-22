package net.dark.spv_addon.client.gui.cosmetics;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dark.spv_addon.cosmetics.CosmeticType;
import net.dark.spv_addon.cosmetics.SpvCosmetics;
import net.dark.spv_addon.cosmetics.registry.RegisteredCosmetic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

public class CosmeticsScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("spv_addon", "textures/gui/cosmetics_background.png");
    private static final int BACKGROUND_WIDTH = 256;
    private static final int BACKGROUND_HEIGHT = 166;
    
    private final CosmeticsLocalData data;
    private final PlayerEntity player;
    private int backgroundX;
    private int backgroundY;
    
    public CosmeticsScreen() {
        super(Text.translatable("gui.spv_addon.cosmetics.title"));
        this.player = MinecraftClient.getInstance().player;
        this.data = new CosmeticsLocalData();
    }
    
    public CosmeticsLocalData getData() {
        return data;
    }
    
    @Override
    protected void init() {
        this.backgroundX = (this.width - BACKGROUND_WIDTH) / 2;
        this.backgroundY = (this.height - BACKGROUND_HEIGHT) / 2;
        
        // Create cosmetic selection buttons for each type
        int yOffset = 0;
        for (CosmeticType type : CosmeticType.values()) {
            createCosmeticButton(type, yOffset);
            yOffset += 25;
        }
        
        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.cancel"), button -> {
            this.close();
        }).dimensions(this.backgroundX + 10, this.backgroundY + BACKGROUND_HEIGHT - 30, 60, 20).build());
        
        // Apply button
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.spv_addon.cosmetics.apply"), button -> {
            this.data.uploadToServer();
            this.close();
        }).dimensions(this.backgroundX + BACKGROUND_WIDTH - 70, this.backgroundY + BACKGROUND_HEIGHT - 30, 60, 20).build());
    }
    
    private void createCosmeticButton(CosmeticType type, int yOffset) {
        Map<String, RegisteredCosmetic> cosmetics = SpvCosmetics.getCosmeticsOfType(type);
        if (cosmetics.isEmpty()) return;
        
        List<RegisteredCosmetic> cosmeticList = new ArrayList<>(cosmetics.values());
        RegisteredCosmetic currentCosmetic = data.getCosmetic(type);
        
        int buttonX = this.backgroundX + 80;
        int buttonY = this.backgroundY + 20 + yOffset;
        
        // Type label
        Text typeLabel = Text.literal(type.getDisplayName() + ":");
        
        // Cycling button for cosmetic selection
        this.addDrawableChild(
            CyclingButtonWidget.<RegisteredCosmetic>builder(cosmetic -> cosmetic.getDisplayText())
                .values(cosmeticList)
                .initially(currentCosmetic)
                .build(buttonX, buttonY, 120, 20, typeLabel, (button, cosmetic) -> {
                    data.setCosmetic(type, cosmetic);
                })
        );
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        
        // Draw background
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.fill(this.backgroundX, this.backgroundY, this.backgroundX + BACKGROUND_WIDTH, this.backgroundY + BACKGROUND_HEIGHT, 0xC0101010);
        context.drawBorder(this.backgroundX, this.backgroundY, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 0xFFFFFFFF);
        
        // Draw title
        context.drawText(this.textRenderer, this.title, 
            this.width / 2 - this.textRenderer.getWidth(this.title) / 2, 
            this.backgroundY + 8, 0xFFFFFF, false);
        
        // Draw player model
        if (this.player != null) {
            int playerX = this.backgroundX + 40;
            int playerY = this.backgroundY + 120;
            drawEntity(context, playerX, playerY, 30, 
                (float)(playerX - mouseX), 
                (float)(playerY - 60 - mouseY), 
                this.player);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
