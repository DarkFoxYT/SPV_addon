package net.dark.spv_addon.Additions.sanity;

import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.Additions.sanity.effects.SanityAudioEffects;
import net.dark.spv_addon.Additions.sanity.effects.SanityGameplayEffects;
import net.dark.spv_addon.Additions.sanity.effects.SanityParticleEffects;
import net.dark.spv_addon.Additions.sanity.effects.SanityVisualEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhanced Sanity Effects Manager
 * Manages all sanity-related visual, audio, and gameplay effects
 * Effects scale progressively based on sanity levels
 */
public class SanityEffectsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("SanityEffects");
    
    // Effect intensity thresholds - Effects start at 75%
    public static final int SANITY_PERFECT = 100;
    public static final int SANITY_GOOD = 75;  // Effects start here
    public static final int SANITY_MODERATE = 50;
    public static final int SANITY_LOW = 35;
    public static final int SANITY_CRITICAL = 20;
    public static final int SANITY_DANGEROUS = 10;
    public static final int SANITY_NIGHTMARE = 0;
    
    private static SanityEffectsManager instance;
    private final SanityVisualEffects visualEffects;
    private final SanityAudioEffects audioEffects;
    private final SanityGameplayEffects gameplayEffects;
    private final SanityParticleEffects particleEffects;
    
    private int lastSanityLevel = 100;
    private long lastUpdateTime = 0;
    private boolean effectsEnabled = true;
    
    private SanityEffectsManager() {
        this.visualEffects = new SanityVisualEffects();
        this.audioEffects = new SanityAudioEffects();
        this.gameplayEffects = new SanityGameplayEffects();
        this.particleEffects = new SanityParticleEffects();
        LOGGER.info("Enhanced Sanity Effects Manager initialized");
    }
    
    public static SanityEffectsManager getInstance() {
        if (instance == null) {
            instance = new SanityEffectsManager();
        }
        return instance;
    }
    
    /**
     * Initialize the sanity effects system
     */
    public static void initialize() {
        getInstance();
    }
    
    /**
     * Update all sanity effects based on current sanity level
     */
    public void updateEffects(float tickDelta) {
        if (!effectsEnabled) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        
        if (player == null) return;
        
        try {
            var sanityOpt = SanityComponent.KEY1.maybeGet(player);
            if (sanityOpt.isEmpty()) return;
            
            int currentSanity = sanityOpt.get().getSanityLevel();
            long currentTime = System.currentTimeMillis();
            
            // Calculate effect intensity (0.0 = no effects, 1.0 = maximum effects)
            float effectIntensity = calculateEffectIntensity(currentSanity);
            
            // Update visual effects
            visualEffects.updateEffects(currentSanity, effectIntensity, tickDelta);
            
            // Update audio effects
            audioEffects.updateEffects(currentSanity, effectIntensity, tickDelta);
            
            // Update gameplay effects
            gameplayEffects.updateEffects(currentSanity, effectIntensity, tickDelta, player);

            // Update particle effects
            particleEffects.updateEffects(currentSanity, effectIntensity, tickDelta, player);

            // Check for sanity level changes
            if (currentSanity != lastSanityLevel) {
                onSanityLevelChanged(lastSanityLevel, currentSanity);
                lastSanityLevel = currentSanity;
            }
            
            lastUpdateTime = currentTime;
            
        } catch (Exception e) {
            LOGGER.warn("Error updating sanity effects: " + e.getMessage());
        }
    }
    
    /**
     * Calculate effect intensity based on sanity level
     * Returns 0.0 for high sanity (above 75%), 1.0 for zero sanity
     */
    private float calculateEffectIntensity(int sanity) {
        if (sanity > SANITY_GOOD) {
            return 0.0f; // No effects above 75% sanity
        } else if (sanity >= SANITY_MODERATE) {
            // Subtle effects start at 75% sanity
            return (SANITY_GOOD - sanity) / (float)(SANITY_GOOD - SANITY_MODERATE) * 0.3f;
        } else if (sanity >= SANITY_LOW) {
            // Moderate effects from 50% to 35%
            return 0.3f + (SANITY_MODERATE - sanity) / (float)(SANITY_MODERATE - SANITY_LOW) * 0.3f;
        } else if (sanity >= SANITY_CRITICAL) {
            // Strong effects from 35% to 20%
            return 0.6f + (SANITY_LOW - sanity) / (float)(SANITY_LOW - SANITY_CRITICAL) * 0.2f;
        } else {
            // Maximum effects below 20%
            return 0.8f + (SANITY_CRITICAL - sanity) / (float)SANITY_CRITICAL * 0.2f;
        }
    }
    
    /**
     * Called when sanity level changes
     */
    private void onSanityLevelChanged(int oldSanity, int newSanity) {
        // Trigger special effects on sanity level changes
        MinecraftClient client = MinecraftClient.getInstance();

        if (newSanity <= SANITY_DANGEROUS && oldSanity > SANITY_DANGEROUS) {
            // Entering dangerous sanity zone
            audioEffects.playDangerousTransition();
            visualEffects.triggerDangerousEffect();
            if (client.player != null) {
                particleEffects.triggerTransitionEffect(newSanity, client.player);
            }
        } else if (newSanity <= SANITY_CRITICAL && oldSanity > SANITY_CRITICAL) {
            // Entering critical sanity zone
            audioEffects.playCriticalTransition();
            visualEffects.triggerCriticalEffect();
            if (client.player != null) {
                particleEffects.triggerTransitionEffect(newSanity, client.player);
            }
        } else if (newSanity == SANITY_NIGHTMARE && oldSanity > SANITY_NIGHTMARE) {
            // Reaching zero sanity
            audioEffects.playNightmareTransition();
            visualEffects.triggerNightmareEffect();
            gameplayEffects.triggerNightmareMode();
            if (client.player != null) {
                particleEffects.triggerTransitionEffect(newSanity, client.player);
            }
        }
    }
    
    /**
     * Get current effect intensity for external systems
     */
    public float getCurrentEffectIntensity() {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        
        if (player == null) return 0.0f;
        
        var sanityOpt = SanityComponent.KEY1.maybeGet(player);
        if (sanityOpt.isEmpty()) return 0.0f;
        
        return calculateEffectIntensity(sanityOpt.get().getSanityLevel());
    }
    
    /**
     * Get current sanity level
     */
    public int getCurrentSanityLevel() {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        
        if (player == null) return 100;
        
        var sanityOpt = SanityComponent.KEY1.maybeGet(player);
        if (sanityOpt.isEmpty()) return 100;
        
        return sanityOpt.get().getSanityLevel();
    }
    
    /**
     * Enable or disable sanity effects
     */
    public void setEffectsEnabled(boolean enabled) {
        this.effectsEnabled = enabled;
        if (!enabled) {
            // Clean up effects when disabled
            visualEffects.cleanup();
            audioEffects.cleanup();
            gameplayEffects.cleanup();
            particleEffects.cleanup();
        }
    }
    
    /**
     * Check if effects are enabled
     */
    public boolean areEffectsEnabled() {
        return effectsEnabled;
    }
    
    /**
     * Get visual effects instance
     */
    public SanityVisualEffects getVisualEffects() {
        return visualEffects;
    }
    
    /**
     * Get audio effects instance
     */
    public SanityAudioEffects getAudioEffects() {
        return audioEffects;
    }
    
    /**
     * Get gameplay effects instance
     */
    public SanityGameplayEffects getGameplayEffects() {
        return gameplayEffects;
    }

    /**
     * Get particle effects instance
     */
    public SanityParticleEffects getParticleEffects() {
        return particleEffects;
    }

    /**
     * Cleanup all effects
     */
    public void cleanup() {
        visualEffects.cleanup();
        audioEffects.cleanup();
        gameplayEffects.cleanup();
        particleEffects.cleanup();
    }
}
