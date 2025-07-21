package net.dark.spv_addon.Additions.sanity.effects;

import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sanity Visual Effects System
 * Handles all visual effects that scale with sanity levels
 */
public class SanityVisualEffects {
    private static final Logger LOGGER = LoggerFactory.getLogger("SanityVisualEffects");
    private static final Identifier SANITY_EFFECTS_SHADER = new Identifier("spv_addon", "sanity_effects");
    
    // Effect configuration
    private static final float MAX_DISTORTION = 0.15f;
    private static final float MAX_NOISE = 0.8f;
    private static final float MAX_COLOR_SHIFT = 0.6f;
    private static final float MAX_VIGNETTE = 0.9f;
    private static final float MAX_CHROMATIC_ABERRATION = 0.02f;
    
    // Current effect values
    private float currentDistortion = 0.0f;
    private float currentNoise = 0.0f;
    private float currentColorShift = 0.0f;
    private float currentVignette = 0.0f;
    private float currentChromaticAberration = 0.0f;
    private float currentDesaturation = 0.0f;
    
    // Animation state
    private float pulseTime = 0.0f;
    private float flickerTime = 0.0f;
    private boolean initialized = false;
    
    // Special effect triggers
    private boolean dangerousEffectActive = false;
    private boolean criticalEffectActive = false;
    private boolean nightmareEffectActive = false;
    private float specialEffectTimer = 0.0f;
    
    public SanityVisualEffects() {
        initialize();
    }
    
    private void initialize() {
        if (initialized) return;

        try {
            // Register post-processing event for sanity effects
            VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
                // Check if this is our sanity effects shader
                if (SANITY_EFFECTS_SHADER.equals(name)) {
                    try {
                        ShaderProgram shader = context.getShader(SANITY_EFFECTS_SHADER);
                        if (shader != null) {
                            updateShaderUniforms(shader);
                        }
                    } catch (Exception e) {
                        // Silently handle shader errors to prevent crashes
                        LOGGER.debug("Shader uniform update failed: " + e.getMessage());
                    }
                }
            });

            initialized = true;
            LOGGER.info("Sanity visual effects initialized with Veil integration");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize sanity visual effects: " + e.getMessage());
        }
    }
    
    /**
     * Update visual effects based on sanity level and intensity
     */
    public void updateEffects(int sanityLevel, float intensity, float tickDelta) {
        if (!initialized) return;
        
        try {
            // Update animation timers
            pulseTime += tickDelta * 0.05f;
            flickerTime += tickDelta * 0.1f;
            
            // Calculate base effect values
            updateBaseEffects(intensity);
            
            // Apply sanity-specific modulations
            applySanityModulations(sanityLevel, intensity, tickDelta);
            
            // Update special effects
            updateSpecialEffects(tickDelta);
            
        } catch (Exception e) {
            LOGGER.warn("Error updating visual effects: " + e.getMessage());
        }
    }
    
    /**
     * Update base effect values based on intensity
     */
    private void updateBaseEffects(float intensity) {
        // Screen distortion (warping effect)
        currentDistortion = intensity * MAX_DISTORTION;
        
        // Visual noise
        currentNoise = intensity * MAX_NOISE;
        
        // Color shift (red/green channel shifting)
        currentColorShift = intensity * MAX_COLOR_SHIFT;
        
        // Vignette effect (darkening edges)
        currentVignette = intensity * MAX_VIGNETTE;
        
        // Chromatic aberration
        currentChromaticAberration = intensity * MAX_CHROMATIC_ABERRATION;
        
        // Desaturation (loss of color)
        currentDesaturation = intensity * 0.7f;
    }
    
    /**
     * Apply sanity-specific modulations
     */
    private void applySanityModulations(int sanityLevel, float intensity, float tickDelta) {
        // Pulse effect at low sanity
        if (sanityLevel <= 30) {
            float pulseIntensity = (30 - sanityLevel) / 30.0f;
            float pulse = (float)(Math.sin(pulseTime * 2.0) * 0.5 + 0.5);
            
            currentVignette += pulse * pulseIntensity * 0.3f;
            currentDistortion += pulse * pulseIntensity * 0.05f;
        }
        
        // Flicker effect at very low sanity
        if (sanityLevel <= 15) {
            float flickerIntensity = (15 - sanityLevel) / 15.0f;
            float flicker = (float)(Math.sin(flickerTime * 8.0) * 0.5 + 0.5);
            
            currentNoise += flicker * flickerIntensity * 0.4f;
            currentColorShift += flicker * flickerIntensity * 0.2f;
        }
        
        // Extreme effects at critical sanity
        if (sanityLevel <= 5) {
            float extremeIntensity = (5 - sanityLevel) / 5.0f;
            float chaos = (float)(Math.sin(pulseTime * 4.0) * Math.cos(flickerTime * 6.0));
            
            currentDistortion += chaos * extremeIntensity * 0.1f;
            currentChromaticAberration += Math.abs(chaos) * extremeIntensity * 0.01f;
        }
    }
    
    /**
     * Update special triggered effects
     */
    private void updateSpecialEffects(float tickDelta) {
        if (specialEffectTimer > 0) {
            specialEffectTimer -= tickDelta;
            
            float effectStrength = specialEffectTimer / 60.0f; // 3 second duration
            
            if (dangerousEffectActive) {
                // Red flash effect
                currentColorShift += effectStrength * 0.5f;
                currentVignette += effectStrength * 0.4f;
            } else if (criticalEffectActive) {
                // Intense distortion effect
                currentDistortion += effectStrength * 0.2f;
                currentNoise += effectStrength * 0.6f;
            } else if (nightmareEffectActive) {
                // Complete visual chaos
                currentDistortion += effectStrength * 0.3f;
                currentNoise += effectStrength * 1.0f;
                currentColorShift += effectStrength * 0.8f;
                currentChromaticAberration += effectStrength * 0.03f;
            }
            
            if (specialEffectTimer <= 0) {
                dangerousEffectActive = false;
                criticalEffectActive = false;
                nightmareEffectActive = false;
            }
        }
    }
    
    /**
     * Update shader uniforms with current effect values
     */
    private void updateShaderUniforms(ShaderProgram shader) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            float gameTime = client.world != null ? client.world.getTime() + client.getTickDelta() : 0;

            // Clamp values to prevent extreme effects
            float distortion = MathHelper.clamp(currentDistortion, 0.0f, MAX_DISTORTION * 1.5f);
            float noise = MathHelper.clamp(currentNoise, 0.0f, MAX_NOISE * 1.2f);
            float colorShift = MathHelper.clamp(currentColorShift, 0.0f, MAX_COLOR_SHIFT * 1.3f);
            float vignette = MathHelper.clamp(currentVignette, 0.0f, MAX_VIGNETTE);
            float chromatic = MathHelper.clamp(currentChromaticAberration, 0.0f, MAX_CHROMATIC_ABERRATION * 2.0f);
            float desaturation = MathHelper.clamp(currentDesaturation, 0.0f, 1.0f);

            // Set shader uniforms using Veil's uniform system
            try {
                shader.setFloat("SanityDistortion", distortion);
                shader.setFloat("SanityNoise", noise);
                shader.setFloat("SanityColorShift", colorShift);
                shader.setFloat("SanityVignette", vignette);
                shader.setFloat("SanityChromaticAberration", chromatic);
                shader.setFloat("SanityDesaturation", desaturation);
                shader.setFloat("SanityTime", gameTime * 0.02f);

                // Additional effect parameters
                shader.setVector("SanityDistortionCenter", 0.5f, 0.5f);
                shader.setFloat("SanityPulseSpeed", 2.0f);
                shader.setFloat("SanityFlickerSpeed", 8.0f);
            } catch (Exception uniformError) {
                // Some uniforms might not exist in the shader, that's okay
                LOGGER.debug("Some shader uniforms not available: " + uniformError.getMessage());
            }

        } catch (Exception e) {
            LOGGER.warn("Failed to update shader uniforms: " + e.getMessage());
        }
    }
    
    /**
     * Trigger dangerous sanity effect
     */
    public void triggerDangerousEffect() {
        dangerousEffectActive = true;
        specialEffectTimer = 60.0f; // 3 seconds at 20 TPS
    }
    
    /**
     * Trigger critical sanity effect
     */
    public void triggerCriticalEffect() {
        criticalEffectActive = true;
        specialEffectTimer = 80.0f; // 4 seconds
    }
    
    /**
     * Trigger nightmare sanity effect
     */
    public void triggerNightmareEffect() {
        nightmareEffectActive = true;
        specialEffectTimer = 120.0f; // 6 seconds
    }
    
    public float getCurrentIntensity() {
        return Math.max(currentDistortion / MAX_DISTORTION,
                       Math.max(currentNoise / MAX_NOISE, currentVignette / MAX_VIGNETTE));
    }

    public void cleanup() {
        currentDistortion = 0.0f;
        currentNoise = 0.0f;
        currentColorShift = 0.0f;
        currentVignette = 0.0f;
        currentChromaticAberration = 0.0f;
        currentDesaturation = 0.0f;

        dangerousEffectActive = false;
        criticalEffectActive = false;
        nightmareEffectActive = false;
        specialEffectTimer = 0.0f;
    }
}
