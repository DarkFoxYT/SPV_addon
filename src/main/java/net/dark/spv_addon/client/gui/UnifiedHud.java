package net.dark.spv_addon.client.gui;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.dark.spv_addon.Additions.battery.BatteryManager;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.cca.ThirstComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Unified HUD system displaying Battery, Sanity, and Thirst as clean text in the top-right corner
 * Features smooth fade in/out animations
 */
public class UnifiedHud implements HudRenderCallback {

    private static final int MARGIN_RIGHT = 10;
    private static final int MARGIN_TOP = 10;
    private static final int LINE_HEIGHT = 12;

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

        public FadeState() {
            this.lastUpdateTime = 0;
            this.alpha = 0.0f;
            this.isVisible = false;
            this.shouldShow = false;
        }
    }
    
    public static void register() {
        HudRenderCallback.EVENT.register(new UnifiedHud());
    }
    
    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || client.options.hudHidden) return;

        // Hide HUD when sanity is below 25% (adds to the disorientation)
        int sanity = getSanityLevel(player);
        if (sanity < 25) {
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

        // Update and render battery with fade
        boolean isBatteryChanging = BatteryManager.isBatteryChanging(player.getUuid());
        boolean shouldShowBattery = flashlightOn || battery <= 25 || isBatteryChanging;
        float batteryAlpha = updateFadeState("battery", shouldShowBattery);
        if (batteryAlpha > 0.0f) {
            String batteryText = formatBatteryText(battery, player);
            int batteryColor = getBatteryTextColor(battery, player);

            // Add pulsing effect when changing battery
            if (isBatteryChanging) {
                float pulseAlpha = getPulseAlpha();
                batteryAlpha *= pulseAlpha;
            }

            drawRightAlignedTextWithAlpha(context, batteryText, screenWidth, yOffset, batteryColor, batteryAlpha);
            yOffset += LINE_HEIGHT;
        }

        // Update and render sanity with fade
        boolean shouldShowSanity = sanity < 90;
        float sanityAlpha = updateFadeState("sanity", shouldShowSanity);
        if (sanityAlpha > 0.0f) {
            String sanityText = formatSanityText(sanity);
            int sanityColor = getSanityTextColor(sanity);
            drawRightAlignedTextWithAlpha(context, sanityText, screenWidth, yOffset, sanityColor, sanityAlpha);
            yOffset += LINE_HEIGHT;
        }

        // Update and render thirst with fade
        boolean shouldShowThirst = thirst < 90;
        float thirstAlpha = updateFadeState("thirst", shouldShowThirst);
        if (thirstAlpha > 0.0f) {
            String thirstText = formatThirstText(thirst);
            int thirstColor = getThirstTextColor(thirst);
            drawRightAlignedTextWithAlpha(context, thirstText, screenWidth, yOffset, thirstColor, thirstAlpha);
        }
    }
    
    /**
     * Update fade state for a HUD element
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
     * Draw text aligned to the right side of the screen with alpha
     */
    private void drawRightAlignedTextWithAlpha(DrawContext context, String text, int screenWidth, int y, int color, float alpha) {
        MinecraftClient client = MinecraftClient.getInstance();
        int textWidth = client.textRenderer.getWidth(text);
        int x = screenWidth - textWidth - MARGIN_RIGHT;

        // Apply alpha to color
        int alphaInt = (int)(alpha * 255);
        int colorWithAlpha = (alphaInt << 24) | (color & 0x00FFFFFF);

        // Draw text with shadow and alpha for better visibility
        context.drawText(client.textRenderer, text, x, y, colorWithAlpha, true);
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
     * Get battery text color
     */
    private int getBatteryTextColor(int battery, PlayerEntity player) {
        // Check if battery is being changed first
        if (BatteryManager.isBatteryChanging(player.getUuid())) {
            return 0x00AAFF; // Bright blue for changing
        }

        int health = BatteryManager.getBatteryHealth(player.getUuid());

        if (battery <= 0) {
            return 0x666666; // Dark gray for dead
        } else if (battery <= 5) {
            return 0xFF4444; // Red for critical
        } else if (battery <= 15) {
            return 0xFFAA44; // Orange for low
        } else if (health <= 30) {
            return 0xFFFF66; // Yellow for degraded health
        } else {
            return 0x88FF88; // Light green for good
        }
    }
    
    /**
     * Get sanity text color
     */
    private int getSanityTextColor(int sanity) {
        if (sanity <= 5) {
            return 0xFF4444; // Dark red
        } else if (sanity <= 15) {
            return 0xFF6666; // Red
        } else if (sanity <= 30) {
            return 0xFFAA44; // Orange
        } else if (sanity <= 50) {
            return 0xFFDD66; // Yellow
        } else if (sanity <= 75) {
            return 0xAAFFAA; // Light green
        } else {
            return 0x88FF88; // Green
        }
    }
    
    /**
     * Get thirst text color
     */
    private int getThirstTextColor(int thirst) {
        if (thirst <= 0) {
            return 0x8B0000; // Dark red
        } else if (thirst <= 10) {
            return 0xFF0000; // Red
        } else if (thirst <= 20) {
            return 0xFF4500; // Orange red
        } else if (thirst <= 40) {
            return 0xFFA500; // Orange
        } else if (thirst <= 60) {
            return 0xFFD700; // Gold
        } else {
            return 0x00BFFF; // Deep sky blue
        }
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
