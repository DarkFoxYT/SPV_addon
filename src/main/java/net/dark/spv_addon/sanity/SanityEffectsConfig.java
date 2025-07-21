package net.dark.spv_addon.sanity;

import static net.dark.spv_addon.sanity.SanityEffectsConfig.Presets.applyNormalPreset;

/**
 * Configuration class for sanity effects
 * Allows easy tuning of effect intensities and behaviors
 */
public class SanityEffectsConfig {

    public static class Visual {
        public static float MAX_DISTORTION = 0.15f;
        public static float MAX_NOISE = 0.8f;
        public static float MAX_COLOR_SHIFT = 0.6f;

        public static float PULSE_SPEED = 2.0f;
        public static float FLICKER_SPEED = 8.0f;
        public static float DISTORTION_FREQUENCY = 2.0f;
    }

    public static class Audio {
        public static float MAX_AMBIENT_VOLUME = 0.3f;
        public static float MAX_WHISPER_VOLUME = 0.2f;
    }

    public static class Gameplay {
        public static float MAX_CONTROL_INVERSION = 0.8f;
    }

    public static class Performance {
        public static boolean ENABLE_VISUAL_EFFECTS = true;
        public static boolean ENABLE_AUDIO_EFFECTS = true;
        public static boolean ENABLE_GAMEPLAY_EFFECTS = true;
        public static boolean ENABLE_SHADER_EFFECTS = true;

        public static int NOISE_OCTAVES = 3;
        public static int CHROMATIC_SAMPLES = 5;
        public static float UPDATE_FREQUENCY = 1.0f;
    }

    public static class Presets {
        
        public static void applyMildPreset() {
            Visual.MAX_DISTORTION = 0.08f;
            Visual.MAX_NOISE = 0.4f;
            Visual.MAX_COLOR_SHIFT = 0.3f;
            Audio.MAX_AMBIENT_VOLUME = 0.15f;
            Audio.MAX_WHISPER_VOLUME = 0.1f;
            Gameplay.MAX_CONTROL_INVERSION = 0.4f;
        }
        
        public static void applyNormalPreset() {
            Visual.MAX_DISTORTION = 0.15f;
            Visual.MAX_NOISE = 0.8f;
            Visual.MAX_COLOR_SHIFT = 0.6f;
            Audio.MAX_AMBIENT_VOLUME = 0.3f;
            Audio.MAX_WHISPER_VOLUME = 0.2f;
            Gameplay.MAX_CONTROL_INVERSION = 0.8f;
        }
        
        public static void applyIntensePreset() {
            Visual.MAX_DISTORTION = 0.25f;
            Visual.MAX_NOISE = 1.2f;
            Visual.MAX_COLOR_SHIFT = 0.9f;
            Audio.MAX_AMBIENT_VOLUME = 0.5f;
            Audio.MAX_WHISPER_VOLUME = 0.4f;
            Gameplay.MAX_CONTROL_INVERSION = 1.0f;
        }
        
        public static void applyNightmarePreset() {
            Visual.MAX_DISTORTION = 0.4f;
            Visual.MAX_NOISE = 1.5f;
            Visual.MAX_COLOR_SHIFT = 1.2f;
            Audio.MAX_AMBIENT_VOLUME = 0.7f;
            Audio.MAX_WHISPER_VOLUME = 0.6f;
            Gameplay.MAX_CONTROL_INVERSION = 1.0f;
            Visual.PULSE_SPEED = 4.0f;
            Visual.FLICKER_SPEED = 12.0f;
        }
    }
    
    /**
     * Reset all settings to default values
     */
    public static void resetToDefaults() {
        applyNormalPreset();
        Performance.ENABLE_VISUAL_EFFECTS = true;
        Performance.ENABLE_AUDIO_EFFECTS = true;
        Performance.ENABLE_GAMEPLAY_EFFECTS = true;
        Performance.ENABLE_SHADER_EFFECTS = true;
    }
    
    /**
     * Apply performance optimizations for lower-end systems
     */
    public static void applyPerformanceMode() {
        Performance.NOISE_OCTAVES = 2;
        Performance.CHROMATIC_SAMPLES = 3;
        Performance.UPDATE_FREQUENCY = 0.5f; // Update every other tick
        Visual.MAX_DISTORTION *= 0.7f;
        Visual.MAX_NOISE *= 0.6f;
    }
    
    /**
     * Apply maximum quality settings for high-end systems
     */
    public static void applyQualityMode() {
        Performance.NOISE_OCTAVES = 4;
        Performance.CHROMATIC_SAMPLES = 7;
        Performance.UPDATE_FREQUENCY = 1.0f;
        Visual.DISTORTION_FREQUENCY = 3.0f;
    }
}
