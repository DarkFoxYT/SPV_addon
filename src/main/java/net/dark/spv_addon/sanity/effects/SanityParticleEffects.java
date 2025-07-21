package net.dark.spv_addon.sanity.effects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sanity Particle Effects System
 * Handles particle effects that appear during low sanity states
 */
public class SanityParticleEffects {
    private static final Logger LOGGER = LoggerFactory.getLogger("SanityParticleEffects");
    
    // Particle configuration
    private static final int MAX_PHANTOM_FOOTSTEPS = 3;
    private static final double FOOTSTEP_DISTANCE_MIN = 2.0;
    private static final double FOOTSTEP_DISTANCE_MAX = 8.0;
    private static final int PARTICLE_LIFETIME = 40; // 2 seconds
    
    // Timing state
    private int particleTimer = 0;
    private int nextParticleTime = 0;
    private final Random random = Random.create();
    
    // Particle effect state
    private boolean phantomFootstepsActive = false;
    private boolean shadowParticlesActive = false;
    private boolean distortionParticlesActive = false;
    
    public SanityParticleEffects() {
        resetParticleTimer();
        LOGGER.info("Sanity particle effects initialized");
    }
    
    /**
     * Update particle effects based on sanity level and intensity
     */
    public void updateEffects(int sanityLevel, float intensity, float tickDelta, PlayerEntity player) {
        try {
            particleTimer++;
            
            // Update particle effect states
            updateParticleStates(sanityLevel, intensity);
            
            // Spawn particles based on sanity level
            if (particleTimer >= nextParticleTime) {
                spawnSanityParticles(sanityLevel, intensity, player);
                resetParticleTimer();
            }
            
        } catch (Exception e) {
            LOGGER.warn("Error updating particle effects: " + e.getMessage());
        }
    }
    
    /**
     * Update particle effect states based on sanity level
     */
    private void updateParticleStates(int sanityLevel, float intensity) {
        phantomFootstepsActive = sanityLevel <= 30;
        shadowParticlesActive = sanityLevel <= 20;
        distortionParticlesActive = sanityLevel <= 10;
    }
    
    /**
     * Spawn sanity-related particles
     */
    private void spawnSanityParticles(int sanityLevel, float intensity, PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        
        if (world == null || player == null) return;
        
        try {
            // Spawn phantom footsteps - Much rarer
            if (phantomFootstepsActive && random.nextFloat() < intensity * 0.05f) {
                spawnPhantomFootsteps(world, player);
            }

            // Spawn shadow particles - Much rarer
            if (shadowParticlesActive && random.nextFloat() < intensity * 0.08f) {
                spawnShadowParticles(world, player);
            }

            // Spawn distortion particles - Much rarer
            if (distortionParticlesActive && random.nextFloat() < intensity * 0.1f) {
                spawnDistortionParticles(world, player);
            }
            
        } catch (Exception e) {
            LOGGER.warn("Failed to spawn sanity particles: " + e.getMessage());
        }
    }
    
    /**
     * Spawn phantom footstep particles behind the player
     */
    private void spawnPhantomFootsteps(ClientWorld world, PlayerEntity player) {
        Vec3d playerPos = player.getPos();
        Vec3d playerLook = player.getRotationVector();
        
        // Create footsteps behind the player
        for (int i = 0; i < MAX_PHANTOM_FOOTSTEPS; i++) {
            // Calculate position behind player
            double distance = FOOTSTEP_DISTANCE_MIN + random.nextDouble() * (FOOTSTEP_DISTANCE_MAX - FOOTSTEP_DISTANCE_MIN);
            Vec3d behindPlayer = playerPos.subtract(playerLook.multiply(distance));
            
            // Add some random offset
            double offsetX = (random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (random.nextDouble() - 0.5) * 2.0;
            
            Vec3d footstepPos = behindPlayer.add(offsetX, 0, offsetZ);
            
            // Find ground level
            BlockPos groundPos = findGroundLevel(world, new BlockPos((int)footstepPos.x, (int)footstepPos.y, (int)footstepPos.z));
            if (groundPos != null) {
                // Spawn dust particles to simulate footsteps
                for (int j = 0; j < 5; j++) {
                    double particleX = groundPos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
                    double particleY = groundPos.getY() + 0.1;
                    double particleZ = groundPos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
                    
                    double velocityX = (random.nextDouble() - 0.5) * 0.1;
                    double velocityY = random.nextDouble() * 0.1;
                    double velocityZ = (random.nextDouble() - 0.5) * 0.1;
                    
                    world.addParticle(ParticleTypes.POOF, 
                        particleX, particleY, particleZ,
                        velocityX, velocityY, velocityZ);
                }
            }
        }
        
        LOGGER.debug("Spawned phantom footsteps behind player");
    }
    
    /**
     * Spawn shadow particles around the player
     */
    private void spawnShadowParticles(ClientWorld world, PlayerEntity player) {
        Vec3d playerPos = player.getPos();
        
        // Spawn dark particles around the player
        for (int i = 0; i < 8; i++) {
            double angle = (i / 8.0) * 2 * Math.PI;
            double radius = 3.0 + random.nextDouble() * 2.0;
            
            double particleX = playerPos.x + Math.cos(angle) * radius;
            double particleY = playerPos.y + random.nextDouble() * 2.0;
            double particleZ = playerPos.z + Math.sin(angle) * radius;
            
            double velocityX = (random.nextDouble() - 0.5) * 0.05;
            double velocityY = -random.nextDouble() * 0.02;
            double velocityZ = (random.nextDouble() - 0.5) * 0.05;
            
            world.addParticle(ParticleTypes.SMOKE, 
                particleX, particleY, particleZ,
                velocityX, velocityY, velocityZ);
        }
        
        LOGGER.debug("Spawned shadow particles around player");
    }
    
    /**
     * Spawn distortion particles for nightmare mode
     */
    private void spawnDistortionParticles(ClientWorld world, PlayerEntity player) {
        Vec3d playerPos = player.getPos();
        
        // Spawn chaotic particles for nightmare effect
        for (int i = 0; i < 15; i++) {
            double particleX = playerPos.x + (random.nextDouble() - 0.5) * 6.0;
            double particleY = playerPos.y + random.nextDouble() * 3.0;
            double particleZ = playerPos.z + (random.nextDouble() - 0.5) * 6.0;
            
            double velocityX = (random.nextDouble() - 0.5) * 0.2;
            double velocityY = (random.nextDouble() - 0.5) * 0.2;
            double velocityZ = (random.nextDouble() - 0.5) * 0.2;
            
            // Mix different particle types for chaotic effect
            if (random.nextBoolean()) {
                world.addParticle(ParticleTypes.LARGE_SMOKE, 
                    particleX, particleY, particleZ,
                    velocityX, velocityY, velocityZ);
            } else {
                world.addParticle(ParticleTypes.ASH, 
                    particleX, particleY, particleZ,
                    velocityX, velocityY, velocityZ);
            }
        }
        
        LOGGER.debug("Spawned distortion particles for nightmare mode");
    }
    
    /**
     * Find the ground level at a given position
     */
    private BlockPos findGroundLevel(World world, BlockPos startPos) {
        // Search downward for solid ground
        for (int y = startPos.getY(); y > world.getBottomY(); y--) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            if (!world.getBlockState(checkPos).isAir() && world.getBlockState(checkPos.up()).isAir()) {
                return checkPos;
            }
        }
        return null;
    }
    
    /**
     * Reset particle timer with random interval - Much less frequent
     */
    private void resetParticleTimer() {
        particleTimer = 0;
        nextParticleTime = 400 + random.nextInt(800); // 20-60 seconds
    }
    
    /**
     * Trigger special particle effect for sanity transitions
     */
    public void triggerTransitionEffect(int sanityLevel, PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        
        if (world == null || player == null) return;
        
        Vec3d playerPos = player.getPos();
        
        // Create dramatic particle burst
        for (int i = 0; i < 30; i++) {
            double particleX = playerPos.x + (random.nextDouble() - 0.5) * 4.0;
            double particleY = playerPos.y + random.nextDouble() * 2.0;
            double particleZ = playerPos.z + (random.nextDouble() - 0.5) * 4.0;
            
            double velocityX = (random.nextDouble() - 0.5) * 0.3;
            double velocityY = random.nextDouble() * 0.3;
            double velocityZ = (random.nextDouble() - 0.5) * 0.3;
            
            if (sanityLevel <= 10) {
                // Nightmare transition - dark particles
                world.addParticle(ParticleTypes.LARGE_SMOKE, 
                    particleX, particleY, particleZ,
                    velocityX, velocityY, velocityZ);
            } else {
                // Critical transition - lighter particles
                world.addParticle(ParticleTypes.CLOUD, 
                    particleX, particleY, particleZ,
                    velocityX, velocityY, velocityZ);
            }
        }
        
        LOGGER.debug("Triggered sanity transition particle effect for level: {}", sanityLevel);
    }
    
    /**
     * Cleanup particle effects
     */
    public void cleanup() {
        phantomFootstepsActive = false;
        shadowParticlesActive = false;
        distortionParticlesActive = false;
        resetParticleTimer();
    }
}
