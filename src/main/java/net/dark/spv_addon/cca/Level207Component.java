package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;

/**
 * Component to track Level 207 specific data for players
 */
public class Level207Component implements AutoSyncedComponent {
    private final PlayerEntity player;
    private final Random random = Random.create();
    
    // Level 207 state
    private boolean inLevel207 = false;
    private int ticksInLevel207 = 0;
    
    // Time-based exit system
    private int exitTime = 0; // When to trigger exit (randomized between 3-4 minutes)
    private boolean hasTriggeredExit = false;
    
    // Exit time constants (3-4 minutes in ticks)
    private static final int MIN_EXIT_TIME = 3600; // 3 minutes (3 * 60 * 20 ticks)
    private static final int MAX_EXIT_TIME = 4800; // 4 minutes (4 * 60 * 20 ticks)
    
    // Glitching effects
    private int glitchTimer = 0;
    private static final int GLITCH_INTERVAL = 200; // 10 seconds
    
    public Level207Component(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        inLevel207 = tag.getBoolean("InLevel207");
        ticksInLevel207 = tag.getInt("TicksInLevel207");
        exitTime = tag.getInt("ExitTime");
        hasTriggeredExit = tag.getBoolean("HasTriggeredExit");
        glitchTimer = tag.getInt("GlitchTimer");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("InLevel207", inLevel207);
        tag.putInt("TicksInLevel207", ticksInLevel207);
        tag.putInt("ExitTime", exitTime);
        tag.putBoolean("HasTriggeredExit", hasTriggeredExit);
        tag.putInt("GlitchTimer", glitchTimer);
    }
    
    /**
     * Initialize Level 207 tracking when player enters
     */
    public void enterLevel207() {
        inLevel207 = true;
        ticksInLevel207 = 0;
        hasTriggeredExit = false;
        glitchTimer = 0;
        
        // Randomize exit time between 3-4 minutes
        exitTime = MIN_EXIT_TIME + random.nextInt(MAX_EXIT_TIME - MIN_EXIT_TIME + 1);
        InitializeComponents.LEVEL207.sync(player);

    }
    
    /**
     * Clean up when player exits Level 207
     */
    public void exitLevel207() {
        inLevel207 = false;
        ticksInLevel207 = 0;
        exitTime = 0;
        hasTriggeredExit = false;
        glitchTimer = 0;
        InitializeComponents.LEVEL207.sync(player);
    }

    /**
     * Tick method to be called every server tick for players in Level 207
     */
    public void tick() {
        if (!inLevel207 || player.isSpectator() || !player.isAlive()) {
            return;
        }
        
        ticksInLevel207++;
        glitchTimer++;
        
        // Apply periodic glitching effects
        applyGlitchingEffects();
    }
    
    private void applyGlitchingEffects() {
        if (glitchTimer >= GLITCH_INTERVAL) {
            glitchTimer = 0;
            
            // Apply subtle glitching effects
            if (!player.getWorld().isClient && random.nextFloat() < 0.3f) {
                // Could add particle effects, sounds, or other atmospheric effects here
                // For now, we'll keep it simple to avoid client-side dependencies
            }
        }
    }
    
    /**
     * Check if it's time to trigger the exit
     */
    public boolean shouldTriggerTimeBasedExit() {
        return inLevel207 && ticksInLevel207 >= exitTime && !hasTriggeredExit;
    }
    
    /**
     * Get time remaining until exit in ticks
     */
    public int getTimeUntilExit() {
        if (!inLevel207 || hasTriggeredExit) {
            return 0;
        }
        return Math.max(0, exitTime - ticksInLevel207);
    }
    
    /**
     * Get time remaining until exit in seconds
     */
    public int getTimeUntilExitSeconds() {
        return getTimeUntilExit() / 20;
    }
    
    /**
     * Get time remaining until exit in minutes and seconds
     */
    public String getFormattedTimeUntilExit() {
        int totalSeconds = getTimeUntilExitSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    /**
     * Get progress percentage (0-100)
     */
    public int getProgressPercentage() {
        if (!inLevel207 || exitTime == 0) {
            return 0;
        }
        return Math.min(100, (ticksInLevel207 * 100) / exitTime);
    }
    
    // Getters and setters
    public boolean isInLevel207() {
        return inLevel207;
    }
    
    public int getTicksInLevel207() {
        return ticksInLevel207;
    }
    
    public boolean hasTriggeredExit() {
        return hasTriggeredExit;
    }
    
    public void setTriggeredExit(boolean triggered) {
        this.hasTriggeredExit = triggered;
        InitializeComponents.LEVEL207.sync(player);
    }
    
    public int getExitTime() {
        return exitTime;
    }
    
    /**
     * Force set exit time (for testing or admin commands)
     */
    public void setExitTime(int exitTime) {
        this.exitTime = Math.max(0, exitTime);
        InitializeComponents.LEVEL207.sync(player);
    }
    
    /**
     * Add time to the exit timer (extend stay in Level 207)
     */
    public void addExitTime(int additionalTicks) {
        this.exitTime += additionalTicks;
        InitializeComponents.LEVEL207.sync(player);
    }
    
    /**
     * Reduce time until exit (speed up escape)
     */
    public void reduceExitTime(int reductionTicks) {
        this.exitTime = Math.max(ticksInLevel207, exitTime - reductionTicks);
        InitializeComponents.LEVEL207.sync(player);
    }
    
    /**
     * Check if player is close to exit (within 30 seconds)
     */
    public boolean isCloseToExit() {
        return getTimeUntilExit() <= 600; // 30 seconds in ticks
    }
    
    /**
     * Check if player has been in Level 207 for a long time (over 5 minutes)
     */
    public boolean hasBeenInLevelTooLong() {
        return ticksInLevel207 > 6000; // 5 minutes in ticks
    }

    
    /**
     * Reset the component to initial state
     */
    public void reset() {
        exitLevel207();
    }
    
    // Static utility methods
    public static int getMinExitTime() {
        return MIN_EXIT_TIME;
    }
    
    public static int getMaxExitTime() {
        return MAX_EXIT_TIME;
    }
    
    public static int getMinExitTimeSeconds() {
        return MIN_EXIT_TIME / 20;
    }
    
    public static int getMaxExitTimeSeconds() {
        return MAX_EXIT_TIME / 20;
    }
    
    public static int getMinExitTimeMinutes() {
        return MIN_EXIT_TIME / 1200; // 1200 ticks = 1 minute
    }
    
    public static int getMaxExitTimeMinutes() {
        return MAX_EXIT_TIME / 1200;
    }
}
