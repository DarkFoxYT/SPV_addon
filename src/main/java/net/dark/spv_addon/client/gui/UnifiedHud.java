package net.dark.spv_addon.client.gui;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.dark.spv_addon.Additions.battery.BatteryManager;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.cca.ThirstComponent;
import net.dark.spv_addon.init.config.SpvAddonConfig;
import net.dark.spv_addon.init.crawl.CrawlSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Unified HUD system displaying Battery, Sanity, Thirst, and Crawling status as clean text in the top-right corner
 * Features smooth fade in/out animations and full configuration support
 */
public class UnifiedHud implements HudRenderCallback {

    // Configuration-based constants (updated from config)
    private static int MARGIN_RIGHT = 10;
    private static int MARGIN_TOP = 10;
    private static int LINE_HEIGHT = 12;

    // Fade animation settings
    private static final int FADE_IN_DURATION = 10; // 0.5 seconds (10 ticks)
    private static final int FADE_OUT_DURATION = 40; // 2 seconds (40 ticks)
    private static final int THIRST_FADE_OUT_DURATION = 120; // 6 seconds (120 ticks) - longer for thirst
    private static final int DISPLAY_DURATION = 100; // 5 seconds (100 ticks)

    // Fade state tracking
    private static final Map<String, FadeState> fadeStates = new HashMap<>();

    // Fade state data
    private static class FadeState {
        public long lastUpdateTime;
        public float alpha;
        public boolean isVisible;
        public boolean shouldShow;
        public int lastValue; // Track last value to detect changes

        public FadeState() {
            this.lastUpdateTime = 0;
            this.alpha = 0.0f;
            this.isVisible = false;
            this.shouldShow = false;
            this.lastValue = -1; // Initialize to invalid value
        }
    }
    
    public static void register() {
        HudRenderCallback.EVENT.register(new UnifiedHud());
    }

    /**
     * Update configuration values
     */
    private static void updateConfigValues() {
        MARGIN_RIGHT = SpvAddonConfig.hudMarginRight;
        MARGIN_TOP = SpvAddonConfig.hudMarginTop;
        LINE_HEIGHT = SpvAddonConfig.hudLineHeight;
    }
    
    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || client.options.hudHidden) return;

        // Update config values
        updateConfigValues();

        // Check if unified HUD is enabled
        if (!SpvAddonConfig.enableUnifiedHud) return;

        // Hide HUD when sanity is below configured threshold (adds to the disorientation)
        int sanity = getSanityLevel(player);
        if (sanity < SpvAddonConfig.hideBelowSanity) {
            // Clear all fade states when HUD is hidden
            fadeStates.clear();
            return;
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int yOffset = MARGIN_TOP;

        // Get all the values
        int battery = getBatteryLevel(player);
        sanity = getSanityLevel(player);
        int thirst = getThirstLevel(player);
        boolean flashlightOn = isFlashlightOn(player);
        boolean isCrawling = isCrawling(player);

        // Update and render battery with fade
        if (SpvAddonConfig.showBatteryHud && SpvAddonConfig.enableBatterySystem) {
            boolean isBatteryChanging = BatteryManager.isBatteryChanging(player.getUuid());
            boolean shouldShowBattery = flashlightOn || battery <= SpvAddonConfig.showBatteryThreshold || isBatteryChanging;
            float batteryAlpha = updateFadeState("battery", shouldShowBattery);
            if (batteryAlpha > 0.0f) {
                String batteryText = formatBatteryText(battery, player);
                int batteryColor = getBatteryTextColor(battery, player);

                // Add pulsing effect when changing battery
                if (isBatteryChanging && SpvAddonConfig.batteryPulseEffect) {
                    float pulseAlpha = getPulseAlpha();
                    batteryAlpha *= pulseAlpha;
                }

                drawRightAlignedTextWithAlpha(context, batteryText, screenWidth, yOffset, batteryColor, batteryAlpha);
                yOffset += LINE_HEIGHT;
            }
        }

        // Update and render sanity with fade - show when value changes or is low
        if (SpvAddonConfig.showSanityHud && SpvAddonConfig.enableSanitySystem) {
            boolean shouldShowSanity = sanity < 90 || hasValueChanged("sanity", sanity);
            float sanityAlpha = updateFadeStateWithValue("sanity", shouldShowSanity, sanity);
            if (sanityAlpha > 0.0f) {
                String sanityText = formatSanityText(sanity);
                int sanityColor = getSanityTextColor(sanity);
                drawRightAlignedTextWithAlpha(context, sanityText, screenWidth, yOffset, sanityColor, sanityAlpha);
                yOffset += LINE_HEIGHT;
            }
        }

        // Update and render thirst with fade - show when value changes or is low
        if (SpvAddonConfig.showThirstHud && SpvAddonConfig.enableThirstSystem) {
            boolean shouldShowThirst = thirst < 90 || hasValueChanged("thirst", thirst);
            float thirstAlpha = updateFadeStateWithValue("thirst", shouldShowThirst, thirst);
            if (thirstAlpha > 0.0f) {
                String thirstText = formatThirstText(thirst);
                int thirstColor = getThirstTextColor(thirst);
                drawRightAlignedTextWithAlpha(context, thirstText, screenWidth, yOffset, thirstColor, thirstAlpha);
                yOffset += LINE_HEIGHT;
            }
        }

        // Update and render crawling status with fade - show only when crawling
        if (SpvAddonConfig.showCrawlingHud && SpvAddonConfig.enableCrawling) {
            float crawlingAlpha = updateFadeState("crawling", isCrawling);
            if (crawlingAlpha > 0.0f) {
                String crawlingText = Text.translatable("hud.spv_addon.crawling").getString();
                int crawlingColor = SpvAddonConfig.getCrawlingColor();
                drawRightAlignedTextWithAlpha(context, crawlingText, screenWidth, yOffset, crawlingColor, crawlingAlpha);
            }
        }
    }
    
    /**
     * Check if a value has changed since last update
     */
    private boolean hasValueChanged(String elementName, int currentValue) {
        FadeState state = fadeStates.get(elementName);
        if (state == null) return true; // First time seeing this value
        return state.lastValue != currentValue;
    }

    /**
     * Update fade state for a HUD element with value change detection
     */
    private float updateFadeStateWithValue(String elementName, boolean shouldShow, int currentValue) {
        FadeState state = fadeStates.computeIfAbsent(elementName, k -> new FadeState());
        long currentTime = Util.getMeasuringTimeMs();

        // Check if value changed
        boolean valueChanged = state.lastValue != currentValue;
        if (valueChanged) {
            state.lastValue = currentValue;
            // Show UI when value changes
            if (!shouldShow) shouldShow = true;
        }

        // Update should show state
        if (shouldShow != state.shouldShow) {
            state.shouldShow = shouldShow;
            state.lastUpdateTime = currentTime;
        }

        // Calculate fade progress
        long timeSinceUpdate = currentTime - state.lastUpdateTime;

        if (state.shouldShow) {
            // Fading in
            if (state.alpha < 1.0f) {
                float fadeProgress = Math.min(1.0f, timeSinceUpdate / (float)(FADE_IN_DURATION * 50)); // Convert ticks to ms
                state.alpha = fadeProgress;
            } else {
                state.alpha = 1.0f;
            }
        } else {
            // Fading out - use different durations for different elements
            if (state.alpha > 0.0f) {
                int fadeOutDuration = elementName.equals("thirst") ? THIRST_FADE_OUT_DURATION : FADE_OUT_DURATION;
                float fadeProgress = Math.min(1.0f, timeSinceUpdate / (float)(fadeOutDuration * 50)); // Convert ticks to ms
                state.alpha = 1.0f - fadeProgress;
            } else {
                state.alpha = 0.0f;
            }
        }

        return Math.max(0.0f, Math.min(1.0f, state.alpha));
    }

    /**
     * Update fade state for a HUD element (original method for battery)
     */
    private float updateFadeState(String elementName, boolean shouldShow) {
        FadeState state = fadeStates.computeIfAbsent(elementName, k -> new FadeState());
        long currentTime = Util.getMeasuringTimeMs();

        // Update should show state
        if (shouldShow != state.shouldShow) {
            state.shouldShow = shouldShow;
            state.lastUpdateTime = currentTime;
        }

        // Calculate fade progress
        long timeSinceUpdate = currentTime - state.lastUpdateTime;

        if (state.shouldShow) {
            // Fading in
            if (state.alpha < 1.0f) {
                float fadeProgress = Math.min(1.0f, timeSinceUpdate / (float)(FADE_IN_DURATION * 50)); // Convert ticks to ms
                state.alpha = fadeProgress;
            } else {
                state.alpha = 1.0f;
            }
        } else {
            // Fading out - use different durations for different elements
            if (state.alpha > 0.0f) {
                int fadeOutDuration = elementName.equals("thirst") ? THIRST_FADE_OUT_DURATION : FADE_OUT_DURATION;
                float fadeProgress = Math.min(1.0f, timeSinceUpdate / (float)(fadeOutDuration * 50)); // Convert ticks to ms
                state.alpha = 1.0f - fadeProgress;
            } else {
                state.alpha = 0.0f;
            }
        }

        return state.alpha;
    }

    /**
     * Draw text aligned to the right side of the screen with alpha and configurable options
     */
    private void drawRightAlignedTextWithAlpha(DrawContext context, String text, int screenWidth, int y, int color, float alpha) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Apply text scale
        context.getMatrices().push();
        context.getMatrices().scale(SpvAddonConfig.hudTextScale, SpvAddonConfig.hudTextScale, 1.0f);

        // Adjust positions for scale
        float scaledScreenWidth = screenWidth / SpvAddonConfig.hudTextScale;
        float scaledMarginRight = MARGIN_RIGHT / SpvAddonConfig.hudTextScale;
        float scaledY = y / SpvAddonConfig.hudTextScale;

        int textWidth = client.textRenderer.getWidth(text);
        int x = (int)(scaledScreenWidth - textWidth - scaledMarginRight);
        int adjustedY = (int)scaledY;

        // Draw background if configured
        if (SpvAddonConfig.hudBackgroundOpacity > 0) {
            int bgAlpha = (int)((SpvAddonConfig.hudBackgroundOpacity / 100.0f) * alpha * 255) << 24;
            int bgColor = bgAlpha | 0x000000; // Black background
            context.fill(x - 2, adjustedY - 1, x + textWidth + 2, adjustedY + client.textRenderer.fontHeight + 1, bgColor);
        }

        // Apply alpha to color
        int alphaInt = (int)(alpha * 255);
        int colorWithAlpha = (alphaInt << 24) | (color & 0x00FFFFFF);

        // Draw text with shadow and alpha for better visibility
        context.drawText(client.textRenderer, text, x, adjustedY, colorWithAlpha, true);

        context.getMatrices().pop();
    }

    /**
     * Draw text aligned to the right side of the screen (legacy method)
     */
    private void drawRightAlignedText(DrawContext context, String text, int screenWidth, int y, int color) {
        drawRightAlignedTextWithAlpha(context, text, screenWidth, y, color, 1.0f);
    }
    
    /**
     * Format battery text with status
     */
    private String formatBatteryText(int battery, PlayerEntity player) {
        String status = BatteryManager.getBatteryStatusText(player.getUuid());
        return String.format("Battery: %d%% - %s", battery, status);
    }
    
    /**
     * Format sanity text with status
     */
    private String formatSanityText(int sanity) {
        String status = getSanityStatus(sanity);
        return String.format("Sanity: %d%% - %s", sanity, status);
    }
    
    /**
     * Format thirst text with status
     */
    private String formatThirstText(int thirst) {
        String status = ThirstManager.getThirstStatusText(thirst);
        return String.format("Thirst: %d%% - %s", thirst, status);
    }
    
    /**
     * Get battery level for player
     */
    private int getBatteryLevel(PlayerEntity player) {
        try {
            return BatteryManager.getBattery(player.getUuid());
        } catch (Exception e) {
            return 100;
        }
    }
    
    /**
     * Get sanity level for player
     */
    private int getSanityLevel(PlayerEntity player) {
        try {
            var sanityOpt = SanityComponent.KEY1.maybeGet(player);
            if (sanityOpt.isPresent()) {
                return sanityOpt.get().getSanityLevel();
            }
        } catch (Exception e) {
            // Fall back to default
        }
        return 100;
    }
    
    /**
     * Get thirst level for player
     */
    private int getThirstLevel(PlayerEntity player) {
        try {
            ThirstComponent thirstComp = net.dark.spv_addon.cca.InitializeComponents.THIRST.get(player);
            return thirstComp.getThirst();
        } catch (Exception e) {
            return 100;
        }
    }
    
    /**
     * Check if flashlight is on
     */
    private boolean isFlashlightOn(PlayerEntity player) {
        try {
            PlayerComponent comp = InitializeComponents.PLAYER.getNullable(player);
            return comp != null && comp.isFlashLightOn();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if player is crawling
     */
    private boolean isCrawling(PlayerEntity player) {
        try {
            return player.getPose() == CrawlSystem.Shared.CRAWLING;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get battery text color using configurable colors
     */
    private int getBatteryTextColor(int battery, PlayerEntity player) {
        int health = BatteryManager.getBatteryHealth(player.getUuid());
        boolean isChanging = BatteryManager.isBatteryChanging(player.getUuid());

        return SpvAddonConfig.getBatteryColor(battery, health, isChanging);
    }
    
    /**
     * Get sanity text color using configurable colors
     */
    private int getSanityTextColor(int sanity) {
        return SpvAddonConfig.getSanityColor(sanity);
    }
    
    /**
     * Get thirst text color using configurable colors
     */
    private int getThirstTextColor(int thirst) {
        return SpvAddonConfig.getThirstColor(thirst);
    }
    
    /**
     * Get pulsing alpha for changing battery
     */
    private float getPulseAlpha() {
        double t = Util.getMeasuringTimeMs() / 400.0; // Faster pulse for changing
        double pulse = (Math.sin(t) + 1.0) / 2.0;
        return 0.6f + (float) (pulse * 0.4f); // Pulse between 0.6 and 1.0
    }

    /**
     * Get sanity status text
     */
    private String getSanityStatus(int sanity) {
        if (sanity <= 5) {
            return "NIGHTMARE";
        } else if (sanity <= 15) {
            return "CRITICAL";
        } else if (sanity <= 30) {
            return "UNSTABLE";
        } else if (sanity <= 50) {
            return "STRESSED";
        } else if (sanity <= 75) {
            return "MODERATE";
        } else {
            return "HIGH";
        }
    }
}
