package net.dark.spv_addon.init.config;

import net.dark.spv_addon.init.gamerules.SpvGameRules;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

/**
 * Server-side configuration that respects gamerules
 * This replaces client-side config usage in server logic
 */
public class ServerConfig {
    
    /**
     * Check if battery system is enabled on the server
     */
    public static boolean isBatterySystemEnabled(MinecraftServer server) {
        if (server == null) return true; // Default for singleplayer
        GameRules gameRules = server.getGameRules();
        return SpvGameRules.isBatterySystemEnabled(gameRules);
    }
    
    /**
     * Get battery drain rate from server gamerules
     */
    public static float getBatteryDrainRate(MinecraftServer server) {
        if (server == null) return 1.0f; // Default for singleplayer
        GameRules gameRules = server.getGameRules();
        return SpvGameRules.getBatteryDrainRate(gameRules);
    }
    
    /**
     * Check if thirst system is enabled on the server
     */
    public static boolean isThirstSystemEnabled(MinecraftServer server) {
        if (server == null) return true; // Default for singleplayer
        GameRules gameRules = server.getGameRules();
        return SpvGameRules.isThirstSystemEnabled(gameRules);
    }
    
    /**
     * Get thirst drain rate from server gamerules
     */
    public static float getThirstDrainRate(MinecraftServer server) {
        if (server == null) return 1.0f; // Default for singleplayer
        GameRules gameRules = server.getGameRules();
        return SpvGameRules.getThirstDrainRate(gameRules);
    }
    
    /**
     * Check if thirst damage is enabled on the server
     */
    public static boolean isThirstDamageEnabled(MinecraftServer server) {
        if (server == null) return true; // Default for singleplayer
        GameRules gameRules = server.getGameRules();
        return SpvGameRules.isThirstDamageEnabled(gameRules);
    }
    
    /**
     * Check if sanity system is enabled on the server
     */
    public static boolean isSanitySystemEnabled(MinecraftServer server) {
        if (server == null) return true; // Default for singleplayer
        GameRules gameRules = server.getGameRules();
        return SpvGameRules.isSanitySystemEnabled(gameRules);
    }
    
    /**
     * Get sanity drain rate from server gamerules
     */
    public static float getSanityDrainRate(MinecraftServer server) {
        if (server == null) return 1.0f; // Default for singleplayer
        GameRules gameRules = server.getGameRules();
        return SpvGameRules.getSanityDrainRate(gameRules);
    }
    
    /**
     * Check if sanity effects are enabled on the server
     */
    public static boolean areSanityEffectsEnabled(MinecraftServer server) {
        if (server == null) return true; // Default for singleplayer
        GameRules gameRules = server.getGameRules();
        return SpvGameRules.areSanityEffectsEnabled(gameRules);
    }

    public static boolean areLevelRunRandomTransitionsEnabled(MinecraftServer server) {
        if (server == null) return true;
        return SpvGameRules.areLevelRunRandomTransitionsEnabled(server.getGameRules());
    }

    public static int getLevelRunChance(MinecraftServer server) {
        if (server == null) return 50000;
        return SpvGameRules.getLevelRunChance(server.getGameRules());
    }
    
    /**
     * Check if crawling is enabled on the server
     * For now, this is always enabled but can be controlled via gamerules in the future
     */
    public static boolean isCrawlingEnabled(MinecraftServer server) {
        return true; // Always enabled for now
    }
    
    /**
     * Get crawling eye height
     */
    public static float getCrawlingEyeHeight(MinecraftServer server) {
        return 0.4f; // Default value
    }
}
