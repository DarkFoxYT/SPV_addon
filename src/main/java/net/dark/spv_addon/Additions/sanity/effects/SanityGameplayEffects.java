package net.dark.spv_addon.Additions.sanity.effects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sanity Gameplay Effects System
 * Handles gameplay-affecting effects that scale with sanity levels
 */
public class SanityGameplayEffects {
    private static final Logger LOGGER = LoggerFactory.getLogger("SanityGameplayEffects");
    
    // Effect configuration
    private static final float MAX_CONTROL_INVERSION = 0.8f;
    private static final float MAX_FALSE_BLOCK_CHANCE = 0.3f;
    private static final float MAX_PHANTOM_ENTITY_CHANCE = 0.1f;
    private static final float MAX_MOVEMENT_DISRUPTION = 0.6f;
    
    // Current effect state
    private float currentControlInversion = 0.0f;
    private float currentFalseBlockChance = 0.0f;
    private float currentPhantomEntityChance = 0.0f;
    private float currentMovementDisruption = 0.0f;
    
    // Control inversion state
    private boolean mouseInverted = false;
    private boolean movementInverted = false;
    private int inversionTimer = 0;
    private int nextInversionTime = 0;
    
    // False block state
    private int falseBlockTimer = 0;
    private int nextFalseBlockTime = 0;
    
    // Phantom entity state
    private int phantomEntityTimer = 0;
    private int nextPhantomEntityTime = 0;
    
    // Movement disruption state
    private int disruptionTimer = 0;
    private int nextDisruptionTime = 0;
    private boolean movementDisrupted = false;
    
    // Random for gameplay variation
    private final Random random = Random.create();
    
    // Nightmare mode state
    private boolean nightmareModeActive = false;
    private int nightmareModeTimer = 0;

    // Random damage state
    private int damageTimer = 0;
    private int nextDamageTime = 0;
    
    public SanityGameplayEffects() {
        LOGGER.info("Sanity gameplay effects initialized");
        resetTimers();
    }
    
    /**
     * Update gameplay effects based on sanity level and intensity
     */
    public void updateEffects(int sanityLevel, float intensity, float tickDelta, PlayerEntity player) {
        try {
            // Update effect intensities
            updateEffectIntensities(intensity);
            
            // Update timers
            updateTimers();
            
            // Apply control effects
            updateControlEffects(sanityLevel, intensity, player);
            
            // Apply false block effects
            updateFalseBlockEffects(sanityLevel, intensity, player);
            
            // Apply phantom entity effects
            updatePhantomEntityEffects(sanityLevel, intensity, player);
            
            // Apply movement disruption
            updateMovementDisruption(sanityLevel, intensity, player);

            // Apply random damage
            updateRandomDamage(sanityLevel, intensity, player);

            // Update nightmare mode
            updateNightmareMode(sanityLevel, intensity, player);
            
        } catch (Exception e) {
            LOGGER.warn("Error updating gameplay effects: " + e.getMessage());
        }
    }
    
    /**
     * Update effect intensities based on overall sanity intensity
     */
    private void updateEffectIntensities(float intensity) {
        currentControlInversion = intensity * MAX_CONTROL_INVERSION;
        currentFalseBlockChance = intensity * MAX_FALSE_BLOCK_CHANCE;
        currentPhantomEntityChance = intensity * MAX_PHANTOM_ENTITY_CHANCE;
        currentMovementDisruption = intensity * MAX_MOVEMENT_DISRUPTION;
    }
    
    /**
     * Update all effect timers
     */
    private void updateTimers() {
        inversionTimer++;
        falseBlockTimer++;
        phantomEntityTimer++;
        disruptionTimer++;
        damageTimer++;
        
        if (nightmareModeActive && nightmareModeTimer > 0) {
            nightmareModeTimer--;
            if (nightmareModeTimer <= 0) {
                nightmareModeActive = false;
            }
        }
    }
    
    /**
     * Update control effects (mouse and movement inversion)
     */
    private void updateControlEffects(int sanityLevel, float intensity, PlayerEntity player) {
        if (sanityLevel <= 40 && inversionTimer >= nextInversionTime) {
            if (random.nextFloat() < currentControlInversion * 0.1f) {
                triggerControlInversion(sanityLevel);
            }
            resetInversionTimer();
        }
        
        // Handle active control inversion
        if (mouseInverted || movementInverted) {
            // Control inversion is handled in mixins
            // This just manages the duration
            if (inversionTimer >= 60) { // 3 seconds
                mouseInverted = false;
                movementInverted = false;
                inversionTimer = 0;
            }
        }
    }
    
    /**
     * Update false block effects
     */
    private void updateFalseBlockEffects(int sanityLevel, float intensity, PlayerEntity player) {
        if (sanityLevel <= 30 && falseBlockTimer >= nextFalseBlockTime) {
            if (random.nextFloat() < currentFalseBlockChance * 0.05f) {
                triggerFalseBlockEffect(player);
            }
            resetFalseBlockTimer();
        }
    }
    
    /**
     * Update phantom entity effects
     */
    private void updatePhantomEntityEffects(int sanityLevel, float intensity, PlayerEntity player) {
        if (sanityLevel <= 20 && phantomEntityTimer >= nextPhantomEntityTime) {
            if (random.nextFloat() < currentPhantomEntityChance * 0.02f) {
                triggerPhantomEntityEffect(player);
            }
            resetPhantomEntityTimer();
        }
    }
    
    /**
     * Update movement disruption effects
     */
    private void updateMovementDisruption(int sanityLevel, float intensity, PlayerEntity player) {
        if (sanityLevel <= 25 && disruptionTimer >= nextDisruptionTime) {
            if (random.nextFloat() < currentMovementDisruption * 0.08f) {
                triggerMovementDisruption(player);
            }
            resetDisruptionTimer();
        }
        
        // Handle active movement disruption
        if (movementDisrupted) {
            if (disruptionTimer >= 40) { // 2 seconds
                movementDisrupted = false;
                disruptionTimer = 0;
            }
        }
    }
    
    /**
     * Update random damage effects
     */
    private void updateRandomDamage(int sanityLevel, float intensity, PlayerEntity player) {
        if (sanityLevel <= 50 && damageTimer >= nextDamageTime) {
            if (random.nextFloat() < intensity * 0.3f) {
                applyRandomDamage(player, sanityLevel);
            }
            resetDamageTimer();
        }
    }

    /**
     * Apply random damage from nowhere
     */
    private void applyRandomDamage(PlayerEntity player, int sanityLevel) {
        try {
            // Calculate damage based on sanity level
            float damage = 1.0f + (50 - sanityLevel) / 50.0f * 2.0f; // 1-3 damage

            // Apply damage with custom damage source
            player.damage(player.getDamageSources().magic(), damage);

            LOGGER.debug("Applied random sanity damage: {} to player", damage);
        } catch (Exception e) {
            LOGGER.warn("Failed to apply random damage: " + e.getMessage());
        }
    }

    /**
     * Update nightmare mode effects
     */
    private void updateNightmareMode(int sanityLevel, float intensity, PlayerEntity player) {
        if (nightmareModeActive) {
            // Extreme effects during nightmare mode
            if (random.nextFloat() < 0.1f) {
                // Random control inversions
                mouseInverted = !mouseInverted;
                movementInverted = !movementInverted;
            }

            if (random.nextFloat() < 0.05f) {
                // Random movement disruptions
                movementDisrupted = true;
                disruptionTimer = 0;
            }
        }
    }
    
    /**
     * Trigger control inversion effect
     */
    private void triggerControlInversion(int sanityLevel) {
        if (sanityLevel <= 10) {
            // Both mouse and movement at very low sanity
            mouseInverted = true;
            movementInverted = true;
        } else if (sanityLevel <= 20) {
            // Either mouse or movement
            if (random.nextBoolean()) {
                mouseInverted = true;
            } else {
                movementInverted = true;
            }
        } else {
            // Only mouse inversion at moderate low sanity
            mouseInverted = true;
        }
        
        inversionTimer = 0;
        LOGGER.debug("Triggered control inversion - Mouse: {}, Movement: {}", mouseInverted, movementInverted);
    }
    
    /**
     * Trigger false block effect
     */
    private void triggerFalseBlockEffect(PlayerEntity player) {
        // This would be implemented in a mixin to intercept block placement
        // For now, just log the effect
        LOGGER.debug("Triggered false block effect for player at {}", player.getBlockPos());
        
        // In a full implementation, this would:
        // 1. Intercept block placement attempts
        // 2. Show fake block placement client-side
        // 3. Remove the fake block after a short delay
    }
    
    /**
     * Trigger phantom entity effect
     */
    private void triggerPhantomEntityEffect(PlayerEntity player) {
        // This would spawn fake entities that only the player can see
        // For now, just log the effect
        LOGGER.debug("Triggered phantom entity effect for player at {}", player.getBlockPos());
        
        // In a full implementation, this would:
        // 1. Spawn client-side only entities
        // 2. Make them appear threatening but harmless
        // 3. Remove them after a short duration
    }
    
    /**
     * Trigger movement disruption effect
     */
    private void triggerMovementDisruption(PlayerEntity player) {
        movementDisrupted = true;
        disruptionTimer = 0;
        LOGGER.debug("Triggered movement disruption for player");
        
        // This effect is handled in movement mixins
    }
    
    /**
     * Reset inversion timer with random interval
     */
    private void resetInversionTimer() {
        inversionTimer = 0;
        nextInversionTime = 100 + random.nextInt(300); // 5-20 seconds
    }
    
    /**
     * Reset false block timer with random interval
     */
    private void resetFalseBlockTimer() {
        falseBlockTimer = 0;
        nextFalseBlockTime = 200 + random.nextInt(400); // 10-30 seconds
    }
    
    /**
     * Reset phantom entity timer with random interval
     */
    private void resetPhantomEntityTimer() {
        phantomEntityTimer = 0;
        nextPhantomEntityTime = 300 + random.nextInt(600); // 15-45 seconds
    }
    
    /**
     * Reset disruption timer with random interval
     */
    private void resetDisruptionTimer() {
        disruptionTimer = 0;
        nextDisruptionTime = 80 + random.nextInt(240); // 4-16 seconds
    }

    /**
     * Reset damage timer with random interval (2-7 minutes)
     */
    private void resetDamageTimer() {
        damageTimer = 0;
        nextDamageTime = 2400 + random.nextInt(6000); // 2-7 minutes (120-420 seconds)
    }

    /**
     * Reset all timers
     */
    private void resetTimers() {
        resetInversionTimer();
        resetFalseBlockTimer();
        resetPhantomEntityTimer();
        resetDisruptionTimer();
        resetDamageTimer();
    }
    
    /**
     * Trigger nightmare mode
     */
    public void triggerNightmareMode() {
        nightmareModeActive = true;
        nightmareModeTimer = 1200; // 60 seconds
        LOGGER.info("Nightmare mode activated!");
    }
    
    /**
     * Check if mouse is inverted (currently disabled)
     */
    public boolean isMouseInverted() {
        return false; // Temporarily disabled
    }

    /**
     * Check if movement is inverted (currently disabled)
     */
    public boolean isMovementInverted() {
        return false; // Temporarily disabled
    }

    /**
     * Check if movement is disrupted (currently disabled)
     */
    public boolean isMovementDisrupted() {
        return false; // Temporarily disabled
    }
    
    /**
     * Check if nightmare mode is active
     */
    public boolean isNightmareModeActive() {
        return nightmareModeActive;
    }
    
    /**
     * Cleanup gameplay effects
     */
    public void cleanup() {
        mouseInverted = false;
        movementInverted = false;
        movementDisrupted = false;
        nightmareModeActive = false;
        
        currentControlInversion = 0.0f;
        currentFalseBlockChance = 0.0f;
        currentPhantomEntityChance = 0.0f;
        currentMovementDisruption = 0.0f;
        
        nightmareModeTimer = 0;
        resetTimers();
    }
}
