package net.dark.spv_addon.init.config;

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

    public enum ColorPreset {
        RED("Red Theme", 0xFF4444, 0xFF6666, 0xFF8888),
        GREEN("Green Theme", 0x44FF44, 0x66FF66, 0x88FF88),
        BLUE("Blue Theme", 0x4444FF, 0x6666FF, 0x8888FF),
        YELLOW("Yellow Theme", 0xFFFF44, 0xFFFF66, 0xFFFF88),
        ORANGE("Orange Theme", 0xFF8844, 0xFFAA44, 0xFFCC44),
        PURPLE("Purple Theme", 0x8844FF, 0xAA44FF, 0xCC44FF),
        CYAN("Cyan Theme", 0x44FFFF, 0x66FFFF, 0x88FFFF),
        MAGENTA("Magenta Theme", 0xFF44FF, 0xFF66FF, 0xFF88FF),
        WHITE("White Theme", 0xFFFFFF, 0xEEEEEE, 0xDDDDDD),
        GRAY("Gray Theme", 0x888888, 0xAAAAAA, 0xCCCCCC),
        LIME("Lime Theme", 0x32FF32, 0x66FF66, 0x99FF99),
        PINK("Pink Theme", 0xFF69B4, 0xFF91C7, 0xFFB6D9),
        TEAL("Teal Theme", 0x008080, 0x20A0A0, 0x40C0C0),
        INDIGO("Indigo Theme", 0x4B0082, 0x6A0DAD, 0x8A2BE2),
        GOLD("Gold Theme", 0xFFD700, 0xFFE135, 0xFFEB6A);

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


    @Entry(category = "hud")
    public static ColorPreset batteryColorPreset = ColorPreset.GREEN;

    @Entry(category = "hud")
    public static ColorPreset sanityColorPreset = ColorPreset.BLUE;

    @Entry(category = "hud")
    public static ColorPreset thirstColorPreset = ColorPreset.CYAN;

    @Entry(category = "hud")
    public static ColorPreset crawlingColorPreset = ColorPreset.BLUE;
    @Entry(category = "systems")
    public static boolean enableCrawling = true;

    @Entry(category = "systems")
    public static float crawlingEyeHeight = 0.6f;

    @Entry(category = "systems")
    public static float crawlingWidth = 0.6f;

    @Entry(category = "systems")
    public static float crawlingHeight = 0.6f;

    @Entry(category = "systems")
    public static float crawlingSpeedMultiplier = 0.3f;

    @Entry(category = "systems")
    public static boolean allowCrawlingInWater = true;


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

    public static int getBatteryColor(int battery, int health, boolean isChanging) {
        if (isChanging) {
            return 0x00AAFF;
        } else if (battery <= 0) {
            return 0x666666;
        } else if (battery <= 5) {
            return batteryColorPreset.critical;
        } else if (battery <= 15) {
            return batteryColorPreset.low;
        } else if (health <= 30) {
            return 0xFFFF66;
        } else {
            return batteryColorPreset.good;
        }
    }

    public static int getSanityColor(int sanity) {
        if (sanity <= 25) {
            return sanityColorPreset.critical;
        } else if (sanity <= 50) {
            return sanityColorPreset.low;
        } else {
            return sanityColorPreset.good;
        }
    }

    public static int getThirstColor(int thirst) {
        if (thirst <= 20) {
            return thirstColorPreset.critical;
        } else if (thirst <= 40) {
            return thirstColorPreset.low;
        } else {
            return thirstColorPreset.good;
        }
    }

    public static int getCrawlingColor() {
        return crawlingColorPreset.good;
    }

    public static float[] getBatteryColorFloat(int battery, int health, boolean isChanging) {
        int color = getBatteryColor(battery, health, isChanging);
        return new float[]{
            ((color >> 16) & 0xFF) / 255.0f,
            ((color >> 8) & 0xFF) / 255.0f,
            (color & 0xFF) / 255.0f
        };
    }

    public static float[] getSanityColorFloat(int sanity) {
        int color = getSanityColor(sanity);
        return new float[]{
            ((color >> 16) & 0xFF) / 255.0f,
            ((color >> 8) & 0xFF) / 255.0f,
            (color & 0xFF) / 255.0f
        };
    }

    public static float[] getThirstColorFloat(int thirst) {
        int color = getThirstColor(thirst);
        return new float[]{
            ((color >> 16) & 0xFF) / 255.0f,
            ((color >> 8) & 0xFF) / 255.0f,
            (color & 0xFF) / 255.0f
        };
    }

}
