package net.dark.spv_addon.cosmetics.registry;

import net.dark.spv_addon.cosmetics.CosmeticType;
import net.dark.spv_addon.cosmetics.SpvCosmetics;
import net.dark.spv_addon.init.ModItems;
import net.minecraft.item.Items;

public class CosmeticRegistry {
    
    public static void registerDefaultCosmetics() {
        // Register the "none" cosmetic for each type
        for (CosmeticType type : CosmeticType.values()) {
            SpvCosmetics.registerCosmetic("none", type, Items.AIR, "None");
        }
        
        // Register example head cosmetics using existing items
        registerHeadCosmetics();
        
        // Register other cosmetic types (can be expanded later)
        registerBackCosmetics();
        registerChestCosmetics();
        registerAccessoryCosmetics();
    }
    
    private static void registerHeadCosmetics() {
        // Example head cosmetic using the existing kitty plushie
        SpvCosmetics.registerCosmetic(
            "kitty_plush_head", 
            CosmeticType.HEAD, 
            ModItems.KITTY_PLUSH_DEV, 
            "Kitty Plushie Hat"
        );
        
        // Add more head cosmetics using other existing items
        SpvCosmetics.registerCosmetic(
            "battery_hat", 
            CosmeticType.HEAD, 
            ModItems.BATTERY_ITEM, 
            "Battery Hat"
        );
        
        SpvCosmetics.registerCosmetic(
            "canteen_hat", 
            CosmeticType.HEAD, 
            ModItems.CANTEEN, 
            "Canteen Hat"
        );
        
        SpvCosmetics.registerCosmetic(
            "almond_bottle_hat", 
            CosmeticType.HEAD, 
            ModItems.ALMOND_BOTTLE, 
            "Almond Water Hat"
        );
    }
    
    private static void registerBackCosmetics() {
        // Example back cosmetics
        SpvCosmetics.registerCosmetic(
            "tape_recorder_back", 
            CosmeticType.BACK, 
            ModItems.TAPE_RECORDER, 
            "Tape Recorder Backpack"
        );
        
        SpvCosmetics.registerCosmetic(
            "battery_pack", 
            CosmeticType.BACK, 
            ModItems.BATTERY_ITEM, 
            "Battery Pack"
        );
    }
    
    private static void registerChestCosmetics() {
        // Example chest cosmetics
        SpvCosmetics.registerCosmetic(
            "cross_pendant", 
            CosmeticType.CHEST, 
            ModItems.CROSS, 
            "Cross Pendant"
        );
    }
    
    private static void registerAccessoryCosmetics() {
        // Example accessories
        SpvCosmetics.registerCosmetic(
            "water_bottle_accessory", 
            CosmeticType.ACCESSORY, 
            ModItems.WATER_BOTTLE, 
            "Water Bottle Charm"
        );
        
        SpvCosmetics.registerCosmetic(
            "energy_drink_accessory", 
            CosmeticType.ACCESSORY, 
            ModItems.ENERGY_DRINK, 
            "Energy Drink Charm"
        );
    }
}
