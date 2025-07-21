package net.dark.spv_addon.config;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

/**
 * Main configuration class for SPV Addon
 * Client-side configuration for singleplayer only
 */
@Environment(EnvType.CLIENT)
public class SpvAddonConfig extends MidnightConfig {

    // Color preset options
    public enum ColorPreset {
        RED("Red", 0xFF4444, 0xFF6666, 0xFF8888),
        GREEN("Green", 0x44FF44, 0x66FF66, 0x88FF88),
        BLUE("Blue", 0x4444FF, 0x6666FF, 0x8888FF),
        YELLOW("Yellow", 0xFFFF44, 0xFFFF66, 0xFFFF88),
        ORANGE("Orange", 0xFF8844, 0xFFAA44, 0xFFCC44),
        PURPLE("Purple", 0x8844FF, 0xAA44FF, 0xCC44FF),
        CYAN("Cyan", 0x44FFFF, 0x66FFFF, 0x88FFFF),
        MAGENTA("Magenta", 0xFF44FF, 0xFF66FF, 0xFF88FF),
        WHITE("White", 0xFFFFFF, 0xEEEEEE, 0xDDDDDD),
        GRAY("Gray", 0x888888, 0xAAAAAA, 0xCCCCCC);

        public final String displayName;
        public final int critical;
        public final int low;
        public final int good;

        ColorPreset(String displayName, int critical, int low, int good) {
            this.displayName = displayName;
            this.critical = critical;
            this.low = low;
            this.good = good;
        }
    }

    // ===== HUD CONFIGURATION =====
    @Entry(category = "hud")
    public static boolean enableUnifiedHud = true;

    @Entry(category = "hud")
    public static int hudFadeDuration = 1000;

    @Entry(category = "hud")
    public static int hudMarginTop = 10;

    @Entry(category = "hud")
    public static int hudMarginRight = 10;

    @Entry(category = "hud")
    public static int hudLineHeight = 12;

    @Entry(category = "hud")
    public static float hudTextScale = 1.0f;

    @Entry(category = "hud")
    public static int hudBackgroundOpacity = 0;

    @Entry(category = "hud")
    public static boolean showBatteryHud = true;

    @Entry(category = "hud")
    public static boolean showSanityHud = true;

    @Entry(category = "hud")
    public static boolean showThirstHud = true;

    @Entry(category = "hud")
    public static boolean showCrawlingHud = true;

    @Entry(category = "hud")
    public static int hideBelowSanity = 25;

    // ===== HUD COLORS =====
    @Entry(category = "hud_colors")
    public static ColorPreset batteryColorPreset = ColorPreset.GREEN;

    @Entry(category = "hud_colors")
    public static ColorPreset sanityColorPreset = ColorPreset.BLUE;

    @Entry(category = "hud_colors")
    public static ColorPreset thirstColorPreset = ColorPreset.CYAN;

    @Entry(category = "hud_colors")
    public static ColorPreset crawlingColorPreset = ColorPreset.ORANGE;

    // ===== CRAWLING CONFIGURATION =====
    @Entry(category = "crawling")
    public static boolean enableCrawling = true;

    @Entry(category = "crawling")
    public static float crawlingEyeHeight = 0.6f;

    @Entry(category = "crawling")
    public static float crawlingWidth = 0.6f;

    @Entry(category = "crawling")
    public static float crawlingHeight = 0.6f;

    @Entry(category = "crawling")
    public static float crawlingSpeedMultiplier = 0.3f;

    @Entry(category = "crawling")
    public static boolean allowCrawlingInWater = true;

    // ===== SYSTEMS CONFIGURATION (Singleplayer Only) =====
    @Entry(category = "systems")
    public static boolean enableBatterySystem = true;

    @Entry(category = "systems")
    public static float batteryDrainRate = 1.0f;

    @Entry(category = "systems")
    public static int showBatteryThreshold = 25;

    @Entry(category = "systems")
    public static boolean batteryPulseEffect = true;

    @Entry(category = "systems")
    public static boolean batteryHealthDegradation = true;

    @Entry(category = "systems")
    public static float batteryDegradationRate = 1.0f;

    @Entry(category = "systems")
    public static boolean batteryFlickerEffect = true;

    @Entry(category = "systems")
    public static boolean batteryWarningSounds = true;

    @Entry(category = "systems")
    public static boolean enableThirstSystem = true;

    @Entry(category = "systems")
    public static float thirstDrainRate = 1.0f;

    @Entry(category = "systems")
    public static float thirstEnvironmentalMultiplier = 1.0f;

    @Entry(category = "systems")
    public static boolean thirstDamageEnabled = true;

    @Entry(category = "systems")
    public static float thirstDamageAmount = 1.5f;

    @Entry(category = "systems")
    public static boolean thirstParticleEffects = true;

    @Entry(category = "systems")
    public static boolean thirstSoundEffects = true;

    @Entry(category = "systems")
    public static boolean thirstWarningMessages = true;

    @Entry(category = "systems")
    public static boolean thirstBiomeEffects = true;

    @Entry(category = "systems")
    public static boolean enableSanitySystem = true;

    @Entry(category = "systems")
    public static float sanityDrainRate = 1.0f;

    @Entry(category = "systems")
    public static int sanityLightRange = 10;

    @Entry(category = "systems")
    public static boolean sanityEffectsEnabled = true;

    @Entry(category = "systems")
    public static boolean sanityVisualDistortions = true;

    @Entry(category = "systems")
    public static boolean sanityAudioEffects = true;

    @Entry(category = "systems")
    public static boolean sanityWhispers = true;

    @Entry(category = "systems")
    public static boolean sanityScreenEffects = true;

    @Entry(category = "systems")
    public static boolean sanityPhantomSounds = true;
    
    // ===== VISUAL EFFECTS CONFIGURATION =====
    @Entry(category = "visual_effects")
    public static boolean enableParticleEffects = true;

    @Entry(category = "visual_effects")
    public static float particleDensity = 1.0f;

    @Entry(category = "visual_effects")
    public static boolean screenShakeEffects = true;

    @Entry(category = "visual_effects")
    public static boolean colorGradingEffects = true;

    @Entry(category = "visual_effects")
    public static boolean fogEffects = true;

    // ===== AUDIO CONFIGURATION =====
    @Entry(category = "audio")
    public static boolean enableAudio = true;

    @Entry(category = "audio")
    public static float audioVolume = 1.0f;

    @Entry(category = "audio")
    public static boolean ambientSounds = true;

    @Entry(category = "audio")
    public static boolean uiSounds = true;

    // ===== PERFORMANCE CONFIGURATION =====
    @Entry(category = "performance")
    public static int renderDistance = 32;

    @Entry(category = "performance")
    public static int updateFrequency = 20;

    @Entry(category = "performance")
    public static boolean optimizeRendering = true;

    @Entry(category = "performance")
    public static boolean reduceParticles = false;

    /**
     * Check if we're in singleplayer mode
     */
    public static boolean isSingleplayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.isInSingleplayer() || client.getServer() != null;
    }

    /**
     * Helper method to get battery color based on configuration
     */
    public static int getBatteryColor(int battery, int health, boolean isChanging) {
        if (isChanging) {
            return 0x00AAFF; // Blue for changing
        } else if (battery <= 0) {
            return 0x666666; // Dark gray for dead
        } else if (battery <= 5) {
            return batteryColorPreset.critical;
        } else if (battery <= 15) {
            return batteryColorPreset.low;
        } else if (health <= 30) {
            return 0xFFFF66; // Yellow for degraded health
        } else {
            return batteryColorPreset.good;
        }
    }

    /**
     * Helper method to get sanity color based on configuration
     */
    public static int getSanityColor(int sanity) {
        if (sanity <= 25) {
            return sanityColorPreset.critical;
        } else if (sanity <= 50) {
            return sanityColorPreset.low;
        } else {
            return sanityColorPreset.good;
        }
    }

    /**
     * Helper method to get thirst color based on configuration
     */
    public static int getThirstColor(int thirst) {
        if (thirst <= 20) {
            return thirstColorPreset.critical;
        } else if (thirst <= 40) {
            return thirstColorPreset.low;
        } else {
            return thirstColorPreset.good;
        }
    }

    /**
     * Helper method to get crawling color based on configuration
     */
    public static int getCrawlingColor() {
        return crawlingColorPreset.good;
    }

}
