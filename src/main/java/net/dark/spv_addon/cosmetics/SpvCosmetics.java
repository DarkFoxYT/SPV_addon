package net.dark.spv_addon.cosmetics;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.cca.CosmeticsComponent;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cosmetics.registry.CosmeticRegistry;
import net.dark.spv_addon.cosmetics.registry.RegisteredCosmetic;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SpvCosmetics {
    private static final Map<String, RegisteredCosmetic> COSMETICS_REGISTRY = new HashMap<>();
    
    public static void initialize() {
        Spv_addon.LOGGER.info("Initializing SPV Cosmetics System");
        CosmeticRegistry.registerDefaultCosmetics();
    }
    
    /**
     * Register a new cosmetic
     */
    public static void registerCosmetic(String id, CosmeticType type, Item item, String displayName) {
        RegisteredCosmetic cosmetic = new RegisteredCosmetic(id, type, item, displayName);
        COSMETICS_REGISTRY.put(id, cosmetic);
        Spv_addon.LOGGER.debug("Registered cosmetic: {} ({})", displayName, id);
    }
    
    /**
     * Get a cosmetic by ID
     */
    @Nullable
    public static RegisteredCosmetic getCosmetic(String id) {
        return COSMETICS_REGISTRY.get(id);
    }
    
    /**
     * Get all cosmetics of a specific type
     */
    public static Map<String, RegisteredCosmetic> getCosmeticsOfType(CosmeticType type) {
        Map<String, RegisteredCosmetic> result = new HashMap<>();
        for (Map.Entry<String, RegisteredCosmetic> entry : COSMETICS_REGISTRY.entrySet()) {
            if (entry.getValue().getType() == type) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    /**
     * Get all registered cosmetics
     */
    public static Map<String, RegisteredCosmetic> getAllCosmetics() {
        return new HashMap<>(COSMETICS_REGISTRY);
    }
    
    /**
     * Get the equipped cosmetic for a player and type
     */
    @NotNull
    public static RegisteredCosmetic getEquippedCosmetic(PlayerEntity player, CosmeticType type) {
        CosmeticsComponent component = InitializeComponents.COSMETICS.get(player);
        String cosmeticId = component.getEquippedCosmetic(type);
        
        RegisteredCosmetic cosmetic = getCosmetic(cosmeticId);
        if (cosmetic != null) {
            return cosmetic;
        }
        
        // Return default "none" cosmetic
        return new RegisteredCosmetic("none", type, Items.AIR, "None");
    }
    
    /**
     * Get the equipped cosmetic for a player by UUID and type
     */
    @NotNull
    public static RegisteredCosmetic getEquippedCosmetic(UUID playerUuid, CosmeticType type) {
        // This method is used for client-side rendering when we might not have the player entity
        // For now, return none - this could be enhanced with client-side caching
        return new RegisteredCosmetic("none", type, Items.AIR, "None");
    }
    
    /**
     * Equip a cosmetic for a player
     */
    public static boolean equipCosmetic(PlayerEntity player, String cosmeticId) {
        RegisteredCosmetic cosmetic = getCosmetic(cosmeticId);
        if (cosmetic == null) {
            return false;
        }
        
        CosmeticsComponent component = InitializeComponents.COSMETICS.get(player);
        component.setEquippedCosmetic(cosmetic.getType(), cosmeticId);
        return true;
    }
    
    /**
     * Unequip a cosmetic type for a player
     */
    public static void unequipCosmetic(PlayerEntity player, CosmeticType type) {
        CosmeticsComponent component = InitializeComponents.COSMETICS.get(player);
        component.clearCosmetic(type);
    }
    
    /**
     * Check if a cosmetic exists
     */
    public static boolean cosmeticExists(String id) {
        return COSMETICS_REGISTRY.containsKey(id);
    }
    
    /**
     * Create an identifier for cosmetic resources
     */
    public static Identifier id(String path) {
        return new Identifier(Spv_addon.MOD_ID, path);
    }
}
