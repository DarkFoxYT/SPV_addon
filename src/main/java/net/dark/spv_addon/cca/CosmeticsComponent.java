package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.dark.spv_addon.cosmetics.CosmeticType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class CosmeticsComponent implements ComponentV3, AutoSyncedComponent {
    private final Map<CosmeticType, String> equippedCosmetics = new HashMap<>();
    
    public CosmeticsComponent() {
        // Initialize with default values
        for (CosmeticType type : CosmeticType.values()) {
            equippedCosmetics.put(type, "none");
        }
    }
    
    public String getEquippedCosmetic(CosmeticType type) {
        return equippedCosmetics.getOrDefault(type, "none");
    }
    
    public void setEquippedCosmetic(CosmeticType type, String cosmeticId) {
        equippedCosmetics.put(type, cosmeticId);
        // Note: Sync will be handled by the networking packet system
    }
    
    public Map<CosmeticType, String> getAllEquippedCosmetics() {
        return new HashMap<>(equippedCosmetics);
    }
    
    public void clearCosmetic(CosmeticType type) {
        equippedCosmetics.put(type, "none");
        // Note: Sync will be handled by the networking packet system
    }

    public void clearAllCosmetics() {
        for (CosmeticType type : CosmeticType.values()) {
            equippedCosmetics.put(type, "none");
        }
        // Note: Sync will be handled by the networking packet system
    }
    
    @Override
    public void readFromNbt(NbtCompound tag) {
        equippedCosmetics.clear();
        for (CosmeticType type : CosmeticType.values()) {
            String key = type.name().toLowerCase();
            if (tag.contains(key)) {
                equippedCosmetics.put(type, tag.getString(key));
            } else {
                equippedCosmetics.put(type, "none");
            }
        }
    }
    
    @Override
    public void writeToNbt(NbtCompound tag) {
        for (Map.Entry<CosmeticType, String> entry : equippedCosmetics.entrySet()) {
            tag.putString(entry.getKey().name().toLowerCase(), entry.getValue());
        }
    }
    
    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(equippedCosmetics.size());
        for (Map.Entry<CosmeticType, String> entry : equippedCosmetics.entrySet()) {
            buf.writeString(entry.getKey().name());
            buf.writeString(entry.getValue());
        }
    }
    
    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        equippedCosmetics.clear();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            CosmeticType type = CosmeticType.valueOf(buf.readString());
            String cosmeticId = buf.readString();
            equippedCosmetics.put(type, cosmeticId);
        }
    }
}
