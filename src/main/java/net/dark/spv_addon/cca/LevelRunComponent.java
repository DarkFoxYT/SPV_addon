package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

/**
 * Component to track Level RUN specific data for players
 */
public class LevelRunComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    
    // Distance tracking
    private Vec3d startPosition = null;
    private double totalDistanceTraveled = 0.0;
    private Vec3d lastPosition = null;
    
    // Damage tracking
    private int damageTimer = 0;
    private static final int DAMAGE_INTERVAL = 60; // 3 seconds at 20 TPS
    private static final float DAMAGE_AMOUNT = 0.5f; // Half heart damage
    
    // Transition tracking
    private static final double TRANSITION_DISTANCE = 200.0; // 200 blocks
    private boolean hasTriggeredTransition = false;
    
    // Level RUN state
    private boolean inLevelRun = false;
    private int ticksInLevelRun = 0;
    
    public LevelRunComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        totalDistanceTraveled = tag.getDouble("TotalDistanceTraveled");
        damageTimer = tag.getInt("DamageTimer");
        hasTriggeredTransition = tag.getBoolean("HasTriggeredTransition");
        inLevelRun = tag.getBoolean("InLevelRun");
        ticksInLevelRun = tag.getInt("TicksInLevelRun");
        
        if (tag.contains("StartPositionX")) {
            double x = tag.getDouble("StartPositionX");
            double y = tag.getDouble("StartPositionY");
            double z = tag.getDouble("StartPositionZ");
            startPosition = new Vec3d(x, y, z);
        }
        
        if (tag.contains("LastPositionX")) {
            double x = tag.getDouble("LastPositionX");
            double y = tag.getDouble("LastPositionY");
            double z = tag.getDouble("LastPositionZ");
            lastPosition = new Vec3d(x, y, z);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putDouble("TotalDistanceTraveled", totalDistanceTraveled);
        tag.putInt("DamageTimer", damageTimer);
        tag.putBoolean("HasTriggeredTransition", hasTriggeredTransition);
        tag.putBoolean("InLevelRun", inLevelRun);
        tag.putInt("TicksInLevelRun", ticksInLevelRun);
        
        if (startPosition != null) {
            tag.putDouble("StartPositionX", startPosition.x);
            tag.putDouble("StartPositionY", startPosition.y);
            tag.putDouble("StartPositionZ", startPosition.z);
        }
        
        if (lastPosition != null) {
            tag.putDouble("LastPositionX", lastPosition.x);
            tag.putDouble("LastPositionY", lastPosition.y);
            tag.putDouble("LastPositionZ", lastPosition.z);
        }
    }
    
    /**
     * Initialize Level RUN tracking when player enters
     */
    public void enterLevelRun() {
        inLevelRun = true;
        startPosition = player.getPos();
        lastPosition = player.getPos();
        totalDistanceTraveled = 0.0;
        damageTimer = 0;
        hasTriggeredTransition = false;
        ticksInLevelRun = 0;
        InitializeComponents.LEVEL_RUN.sync(player);
    }
    
    /**
     * Clean up when player exits Level RUN
     */
    public void exitLevelRun() {
        inLevelRun = false;
        startPosition = null;
        lastPosition = null;
        totalDistanceTraveled = 0.0;
        damageTimer = 0;
        hasTriggeredTransition = false;
        ticksInLevelRun = 0;
        InitializeComponents.LEVEL_RUN.sync(player);
    }
    
    /**
     * Tick method to be called every server tick for players in Level RUN
     */
    public void tick() {
        if (!inLevelRun || player.isSpectator() || !player.isAlive()) {
            return;
        }
        
        ticksInLevelRun++;
        
        // Update distance tracking
        updateDistanceTracking();
        
        // Apply damage over time
        applyDamageOverTime();
        
        // Check for transition
        checkForTransition();
    }
    
    private void updateDistanceTracking() {
        if (lastPosition == null) {
            lastPosition = player.getPos();
            return;
        }
        
        Vec3d currentPos = player.getPos();
        double distanceThisTick = lastPosition.distanceTo(currentPos);
        totalDistanceTraveled += distanceThisTick;
        lastPosition = currentPos;
    }
    
    private void applyDamageOverTime() {
        damageTimer++;
        
        if (damageTimer >= DAMAGE_INTERVAL) {
            damageTimer = 0;
            
            // Apply damage if player is not in creative or spectator mode
            if (!player.isCreative() && !player.isSpectator()) {
                player.damage(player.getDamageSources().generic(), DAMAGE_AMOUNT);
                
                // Optional: Add visual/audio feedback
                if (!player.getWorld().isClient) {
                    // Could add particle effects or sounds here
                }
            }
        }
    }
    
    private void checkForTransition() {
        if (hasTriggeredTransition) {
            return;
        }
        
        if (totalDistanceTraveled >= TRANSITION_DISTANCE) {
            hasTriggeredTransition = true;
            triggerTransition();
        }
    }
    
    private void triggerTransition() {
        if (player.getWorld().isClient) {
            return;
        }

        // Trigger transition out of Level RUN
        // This will be handled by the LevelRunManager
        if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
            net.dark.spv_addon.world.levels.managers.LevelRunManager.triggerTransitionOut(serverPlayer);
        }
    }
    
    // Getters
    public boolean isInLevelRun() {
        return inLevelRun;
    }
    
    public double getTotalDistanceTraveled() {
        return totalDistanceTraveled;
    }
    
    public double getDistanceToTransition() {
        return Math.max(0, TRANSITION_DISTANCE - totalDistanceTraveled);
    }
    
    public boolean hasTriggeredTransition() {
        return hasTriggeredTransition;
    }
    
    public int getTicksInLevelRun() {
        return ticksInLevelRun;
    }
    
    public Vec3d getStartPosition() {
        return startPosition;
    }
    
    public static double getTransitionDistance() {
        return TRANSITION_DISTANCE;
    }
    
    public static float getDamageAmount() {
        return DAMAGE_AMOUNT;
    }
    
    public static int getDamageInterval() {
        return DAMAGE_INTERVAL;
    }
}
