package net.dark.spv_addon.cosmetics;

public enum CosmeticType {
    HEAD("Head", "Cosmetics worn on the head"),
    BACK("Back", "Cosmetics worn on the back"),
    CHEST("Chest", "Cosmetics worn on the chest"),
    WAIST("Waist", "Cosmetics worn around the waist"),
    LEGS("Legs", "Cosmetics worn on the legs"),
    FEET("Feet", "Cosmetics worn on the feet"),
    HANDS("Hands", "Cosmetics worn on the hands"),
    ACCESSORY("Accessory", "Additional accessories");
    
    private final String displayName;
    private final String description;
    
    CosmeticType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getId() {
        return name().toLowerCase();
    }
}
