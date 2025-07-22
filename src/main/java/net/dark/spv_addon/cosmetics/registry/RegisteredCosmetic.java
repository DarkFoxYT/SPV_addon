package net.dark.spv_addon.cosmetics.registry;

import net.dark.spv_addon.cosmetics.CosmeticType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class RegisteredCosmetic {
    private final String id;
    private final CosmeticType type;
    private final Item item;
    private final String displayName;
    private final String description;
    
    public RegisteredCosmetic(String id, CosmeticType type, Item item, String displayName) {
        this(id, type, item, displayName, "");
    }
    
    public RegisteredCosmetic(String id, CosmeticType type, Item item, String displayName, String description) {
        this.id = id;
        this.type = type;
        this.item = item;
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getId() {
        return id;
    }
    
    public CosmeticType getType() {
        return type;
    }
    
    public Item getItem() {
        return item;
    }
    
    public ItemStack getItemStack() {
        return new ItemStack(item);
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Text getDisplayText() {
        return Text.literal(displayName);
    }
    
    public Text getDescriptionText() {
        return Text.literal(description);
    }
    
    public boolean isNone() {
        return "none".equals(id);
    }
    
    @Override
    public String toString() {
        return "RegisteredCosmetic{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", displayName='" + displayName + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RegisteredCosmetic that = (RegisteredCosmetic) obj;
        return id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
