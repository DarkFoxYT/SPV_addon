package net.dark.spv_addon.sanity.effects;

import net.dark.spv_addon.init.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sanity Audio Effects System
 * Handles all audio effects that scale with sanity levels
 */
public class SanityAudioEffects {
    private static final Logger LOGGER = LoggerFactory.getLogger("SanityAudioEffects");
    
    // Audio effect configuration - Increased volumes
    private static final float MAX_AMBIENT_VOLUME = 0.6f;
    private static final float MAX_WHISPER_VOLUME = 0.5f;
    private static final float MAX_DISTORTION_VOLUME = 0.7f;
    
    // Timing configuration - Nightmare mode: 30-60 seconds between sounds
    private static final int MIN_WHISPER_INTERVAL = 400; // 20 seconds
    private static final int MAX_WHISPER_INTERVAL = 800; // 40 seconds
    private static final int MIN_AMBIENT_INTERVAL = 600; // 30 seconds
    private static final int MAX_AMBIENT_INTERVAL = 1200; // 60 seconds
    private static final int MIN_FOOTSTEP_INTERVAL = 300; // 15 seconds
    private static final int MAX_FOOTSTEP_INTERVAL = 900; // 45 seconds
    
    // Current state
    private float currentAmbientVolume = 0.0f;
    private float currentWhisperVolume = 0.0f;
    private float currentDistortionLevel = 0.0f;
    
    // Timing state
    private int whisperTimer = 0;
    private int ambientTimer = 0;
    private int footstepTimer = 0;
    private int nextWhisperTime = 0;
    private int nextAmbientTime = 0;
    private int nextFootstepTime = 0;
    
    // Sound instances
    private SoundInstance currentAmbientSound = null;
    private SoundInstance currentWhisperSound = null;
    
    // Random for audio variation
    private final Random random = Random.create();
    
    // Special effect state
    private boolean playingSpecialEffect = false;
    private int specialEffectTimer = 0;
    
    public SanityAudioEffects() {
        LOGGER.info("Sanity audio effects initialized");
        resetTimers();
    }
    
    /**
     * Update audio effects based on sanity level and intensity
     */
    public void updateEffects(int sanityLevel, float intensity, float tickDelta) {
        try {
            // Update effect volumes based on intensity
            updateEffectVolumes(intensity);
            
            // Update timers
            whisperTimer++;
            ambientTimer++;
            footstepTimer++;

            if (specialEffectTimer > 0) {
                specialEffectTimer--;
                if (specialEffectTimer <= 0) {
                    playingSpecialEffect = false;
                }
            }

            // Play ambient sounds based on sanity level
            updateAmbientSounds(sanityLevel, intensity);

            // Play whisper sounds at low sanity
            updateWhisperSounds(sanityLevel, intensity);

            // Play phantom footstep sounds
            updatePhantomFootsteps(sanityLevel, intensity);

            // Apply audio distortion effects
            updateAudioDistortion(sanityLevel, intensity);
            
        } catch (Exception e) {
            LOGGER.warn("Error updating audio effects: " + e.getMessage());
        }
    }
    
    /**
     * Update effect volumes based on intensity
     */
    private void updateEffectVolumes(float intensity) {
        currentAmbientVolume = intensity * MAX_AMBIENT_VOLUME;
        currentWhisperVolume = intensity * MAX_WHISPER_VOLUME;
        currentDistortionLevel = intensity * MAX_DISTORTION_VOLUME;
    }
    
    /**
     * Update ambient sounds
     */
    private void updateAmbientSounds(int sanityLevel, float intensity) {
        if (sanityLevel <= 60 && ambientTimer >= nextAmbientTime && !playingSpecialEffect) {
            playAmbientSound(sanityLevel, intensity);
            resetAmbientTimer();
        }
    }
    
    /**
     * Update whisper sounds
     */
    private void updateWhisperSounds(int sanityLevel, float intensity) {
        if (sanityLevel <= 30 && whisperTimer >= nextWhisperTime && !playingSpecialEffect) {
            playWhisperSound(sanityLevel, intensity);
            resetWhisperTimer();
        }
    }

    /**
     * Update phantom footstep sounds
     */
    private void updatePhantomFootsteps(int sanityLevel, float intensity) {
        if (sanityLevel <= 20 && footstepTimer >= nextFootstepTime && !playingSpecialEffect) {
            playPhantomFootsteps(sanityLevel, intensity);
            resetFootstepTimer();
        }
    }

    /**
     * Play phantom footstep sounds
     */
    private void playPhantomFootsteps(int sanityLevel, float intensity) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getSoundManager() == null) return;

        try {
            float volume = currentAmbientVolume * (0.4f + random.nextFloat() * 0.6f);
            float pitch = 0.7f + random.nextFloat() * 0.6f;

            // Create and play phantom footstep sound
            SoundInstance footstepSound = PositionedSoundInstance.ambient(
                ModSounds.SANITY_FOOTSTEPS_PHANTOM,
                pitch,
                volume
            );

            client.getSoundManager().play(footstepSound);

            LOGGER.debug("Playing phantom footsteps at volume: {}", volume);
        } catch (Exception e) {
            LOGGER.warn("Failed to play phantom footsteps: " + e.getMessage());
        }
    }
    
    /**
     * Update audio distortion effects
     */
    private void updateAudioDistortion(int sanityLevel, float intensity) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (sanityLevel <= 20 && client.getSoundManager() != null) {
            // Apply subtle audio distortion by modifying master volume
            float distortionFactor = 1.0f - (currentDistortionLevel * 0.3f);
            
            // Note: This is a simplified approach. In a full implementation,
            // you might want to use audio filters or custom sound processing
            try {
                // Subtle volume modulation to simulate audio distortion
                if (random.nextFloat() < intensity * 0.1f) {
                    // Brief audio "glitches"
                    client.getSoundManager().updateListenerPosition(client.gameRenderer.getCamera());
                }
            } catch (Exception e) {
                // Silently handle any audio system errors
            }
        }
    }
    
    /**
     * Play ambient sound based on sanity level
     */
    private void playAmbientSound(int sanityLevel, float intensity) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getSoundManager() == null) return;

        try {
            SoundEvent soundEvent = getAmbientSoundForSanity(sanityLevel);
            if (soundEvent != null) {
                float volume = currentAmbientVolume * (0.5f + random.nextFloat() * 0.5f);
                float pitch = 0.8f + random.nextFloat() * 0.4f;

                // Stop current ambient sound if playing
                if (currentAmbientSound != null) {
                    client.getSoundManager().stop(currentAmbientSound);
                }

                // Create and play new ambient sound
                currentAmbientSound = PositionedSoundInstance.ambient(
                    soundEvent,
                    pitch,
                    volume
                );

                client.getSoundManager().play(currentAmbientSound);

                LOGGER.debug("Playing ambient sound: {} at volume: {}", soundEvent.getId(), volume);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to play ambient sound: " + e.getMessage());
        }
    }
    
    /**
     * Play whisper sound based on sanity level
     */
    private void playWhisperSound(int sanityLevel, float intensity) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getSoundManager() == null) return;

        try {
            SoundEvent soundEvent = getWhisperSoundForSanity(sanityLevel);
            if (soundEvent != null) {
                float volume = currentWhisperVolume * (0.3f + random.nextFloat() * 0.7f);
                float pitch = 0.6f + random.nextFloat() * 0.8f;

                // Stop current whisper sound if playing
                if (currentWhisperSound != null) {
                    client.getSoundManager().stop(currentWhisperSound);
                }

                // Create and play new whisper sound
                currentWhisperSound = PositionedSoundInstance.ambient(
                    soundEvent,
                    pitch,
                    volume
                );

                client.getSoundManager().play(currentWhisperSound);

                LOGGER.debug("Playing whisper sound: {} at volume: {}", soundEvent.getId(), volume);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to play whisper sound: " + e.getMessage());
        }
    }
    
    /**
     * Get ambient sound event based on sanity level
     */
    private SoundEvent getAmbientSoundForSanity(int sanityLevel) {
        if (sanityLevel <= 10) {
            // Nightmare sounds
            return ModSounds.SANITY_AMBIENT_NIGHTMARE;
        } else if (sanityLevel <= 20) {
            // Critical sounds
            return ModSounds.SANITY_AMBIENT_CRITICAL;
        } else if (sanityLevel <= 40) {
            // Low sanity sounds
            return ModSounds.SANITY_AMBIENT_LOW;
        } else {
            // Moderate sanity sounds - heartbeat
            return ModSounds.SANITY_HEARTBEAT;
        }
    }

    /**
     * Get whisper sound event based on sanity level
     */
    private SoundEvent getWhisperSoundForSanity(int sanityLevel) {
        // Use custom whisper sounds
        int whisperType = random.nextInt(3);
        switch (whisperType) {
            case 0:
                return ModSounds.SANITY_WHISPER_1;
            case 1:
                return ModSounds.SANITY_WHISPER_2;
            default:
                return ModSounds.SANITY_WHISPER_3;
        }
    }
    
    /**
     * Reset ambient timer with random interval
     */
    private void resetAmbientTimer() {
        ambientTimer = 0;
        nextAmbientTime = MIN_AMBIENT_INTERVAL + random.nextInt(MAX_AMBIENT_INTERVAL - MIN_AMBIENT_INTERVAL);
    }
    
    /**
     * Reset whisper timer with random interval
     */
    private void resetWhisperTimer() {
        whisperTimer = 0;
        nextWhisperTime = MIN_WHISPER_INTERVAL + random.nextInt(MAX_WHISPER_INTERVAL - MIN_WHISPER_INTERVAL);
    }

    /**
     * Reset footstep timer with random interval
     */
    private void resetFootstepTimer() {
        footstepTimer = 0;
        nextFootstepTime = MIN_FOOTSTEP_INTERVAL + random.nextInt(MAX_FOOTSTEP_INTERVAL - MIN_FOOTSTEP_INTERVAL);
    }

    /**
     * Reset all timers
     */
    private void resetTimers() {
        resetAmbientTimer();
        resetWhisperTimer();
        resetFootstepTimer();
    }
    
    /**
     * Play dangerous transition sound
     */
    public void playDangerousTransition() {
        playSpecialSoundEvent(ModSounds.SANITY_STATIC, 0.4f, 0.9f);
    }

    /**
     * Play critical transition sound
     */
    public void playCriticalTransition() {
        playSpecialSoundEvent(ModSounds.SANITY_TRANSITION_CRITICAL, 0.5f, 0.8f);
    }

    /**
     * Play nightmare transition sound
     */
    public void playNightmareTransition() {
        playSpecialSoundEvent(ModSounds.SANITY_TRANSITION_NIGHTMARE, 0.6f, 0.7f);
    }
    
    /**
     * Play special sound effect using SoundEvent
     */
    private void playSpecialSoundEvent(SoundEvent soundEvent, float volume, float pitch) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getSoundManager() == null) return;

        try {
            SoundInstance sound = PositionedSoundInstance.ambient(
                soundEvent,
                pitch,
                volume
            );

            client.getSoundManager().play(sound);
            playingSpecialEffect = true;
            specialEffectTimer = 60; // 3 seconds

            LOGGER.debug("Playing special sound: {} at volume: {}", soundEvent.getId(), volume);
        } catch (Exception e) {
            LOGGER.warn("Failed to play special sound: " + e.getMessage());
        }
    }
    
    /**
     * Cleanup audio effects
     */
    public void cleanup() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getSoundManager() != null) {
            try {
                if (currentAmbientSound != null) {
                    client.getSoundManager().stop(currentAmbientSound);
                    currentAmbientSound = null;
                }
                
                if (currentWhisperSound != null) {
                    client.getSoundManager().stop(currentWhisperSound);
                    currentWhisperSound = null;
                }
            } catch (Exception e) {
                LOGGER.warn("Error cleaning up audio effects: " + e.getMessage());
            }
        }
        
        currentAmbientVolume = 0.0f;
        currentWhisperVolume = 0.0f;
        currentDistortionLevel = 0.0f;
        playingSpecialEffect = false;
        specialEffectTimer = 0;
        resetTimers();
    }
}
