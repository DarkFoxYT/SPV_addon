package net.dark.spv_addon.init.gamerules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

/**
 * Game rules for SPV Addon systems
 * Allows server admins to control battery, thirst, and sanity systems
 */
public class SpvGameRules {
    
    // Battery System Game Rules
    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_BATTERY_SYSTEM =
            GameRuleRegistry.register("spvBatterySystem", GameRules.Category.MISC, 
                    GameRuleFactory.createBooleanRule(true));
    
    public static final GameRules.Key<GameRules.IntRule> BATTERY_DRAIN_RATE =
            GameRuleRegistry.register("spvBatteryDrainRate", GameRules.Category.MISC, 
                    GameRuleFactory.createIntRule(100, 1, 1000));
    
    // Thirst System Game Rules
    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_THIRST_SYSTEM =
            GameRuleRegistry.register("spvThirstSystem", GameRules.Category.MISC, 
                    GameRuleFactory.createBooleanRule(true));
    
    public static final GameRules.Key<GameRules.IntRule> THIRST_DRAIN_RATE =
            GameRuleRegistry.register("spvThirstDrainRate", GameRules.Category.MISC, 
                    GameRuleFactory.createIntRule(100, 1, 1000));
    
    public static final GameRules.Key<GameRules.BooleanRule> THIRST_DAMAGE_ENABLED =
            GameRuleRegistry.register("spvThirstDamage", GameRules.Category.MISC, 
                    GameRuleFactory.createBooleanRule(true));
    
    // Sanity System Game Rules
    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_SANITY_SYSTEM =
            GameRuleRegistry.register("spvSanitySystem", GameRules.Category.MISC, 
                    GameRuleFactory.createBooleanRule(true));
    
    public static final GameRules.Key<GameRules.IntRule> SANITY_DRAIN_RATE =
            GameRuleRegistry.register("spvSanityDrainRate", GameRules.Category.MISC, 
                    GameRuleFactory.createIntRule(100, 1, 1000));
    
    public static final GameRules.Key<GameRules.BooleanRule> SANITY_EFFECTS_ENABLED =
            GameRuleRegistry.register("spvSanityEffects", GameRules.Category.MISC, 
                    GameRuleFactory.createBooleanRule(true));

    public static final GameRules.Key<GameRules.BooleanRule> LEVEL_RUN_RANDOM_TRANSITIONS =
            GameRuleRegistry.register("spvLevelRunRandomTransitions", GameRules.Category.MISC,
                    GameRuleFactory.createBooleanRule(true));

    public static final GameRules.Key<GameRules.IntRule> LEVEL_RUN_CHANCE =
            GameRuleRegistry.register("spvLevelRunChance", GameRules.Category.MISC,
                    GameRuleFactory.createIntRule(50000, 100, 1000000));
    
    /**
     * Initialize game rules
     */
    public static void initialize() {
        // Game rules are automatically registered when the class is loaded
    }
    
    /**
     * Check if battery system is enabled via game rule
     */
    public static boolean isBatterySystemEnabled(GameRules gameRules) {
        return gameRules.getBoolean(ENABLE_BATTERY_SYSTEM);
    }
    
    /**
     * Get battery drain rate from game rule (percentage of normal rate)
     */
    public static float getBatteryDrainRate(GameRules gameRules) {
        return gameRules.getInt(BATTERY_DRAIN_RATE) / 100.0f;
    }
    
    /**
     * Check if thirst system is enabled via game rule
     */
    public static boolean isThirstSystemEnabled(GameRules gameRules) {
        return gameRules.getBoolean(ENABLE_THIRST_SYSTEM);
    }
    
    /**
     * Get thirst drain rate from game rule (percentage of normal rate)
     */
    public static float getThirstDrainRate(GameRules gameRules) {
        return gameRules.getInt(THIRST_DRAIN_RATE) / 100.0f;
    }
    
    /**
     * Check if thirst damage is enabled via game rule
     */
    public static boolean isThirstDamageEnabled(GameRules gameRules) {
        return gameRules.getBoolean(THIRST_DAMAGE_ENABLED);
    }
    
    /**
     * Check if sanity system is enabled via game rule
     */
    public static boolean isSanitySystemEnabled(GameRules gameRules) {
        return gameRules.getBoolean(ENABLE_SANITY_SYSTEM);
    }
    
    /**
     * Get sanity drain rate from game rule (percentage of normal rate)
     */
    public static float getSanityDrainRate(GameRules gameRules) {
        return gameRules.getInt(SANITY_DRAIN_RATE) / 100.0f;
    }
    
    /**
     * Check if sanity effects are enabled via game rule
     */
    public static boolean areSanityEffectsEnabled(GameRules gameRules) {
        return gameRules.getBoolean(SANITY_EFFECTS_ENABLED);
    }

    public static boolean areLevelRunRandomTransitionsEnabled(GameRules gameRules) {
        return gameRules.getBoolean(LEVEL_RUN_RANDOM_TRANSITIONS);
    }

    public static int getLevelRunChance(GameRules gameRules) {
        return gameRules.getInt(LEVEL_RUN_CHANCE);
    }
}
