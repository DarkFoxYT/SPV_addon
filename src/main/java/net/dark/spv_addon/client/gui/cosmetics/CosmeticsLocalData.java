package net.dark.spv_addon.client.gui.cosmetics;

import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cosmetics.CosmeticType;
import net.dark.spv_addon.cosmetics.SpvCosmetics;
import net.dark.spv_addon.cosmetics.registry.RegisteredCosmetic;
import net.dark.spv_addon.networking.CosmeticsUpdatePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class CosmeticsLocalData {
    private final Map<CosmeticType, RegisteredCosmetic> selectedCosmetics = new HashMap<>();
    
    public CosmeticsLocalData() {
        loadCurrentCosmetics();
    }
    
    private void loadCurrentCosmetics() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        
        for (CosmeticType type : CosmeticType.values()) {
            RegisteredCosmetic current = SpvCosmetics.getEquippedCosmetic(player, type);
            selectedCosmetics.put(type, current);
        }
    }
    
    public RegisteredCosmetic getCosmetic(CosmeticType type) {
        return selectedCosmetics.getOrDefault(type, 
            new RegisteredCosmetic("none", type, Items.AIR, "None"));
    }
    
    public void setCosmetic(CosmeticType type, RegisteredCosmetic cosmetic) {
        selectedCosmetics.put(type, cosmetic);
    }
    
    public void setCosmetic(CosmeticType type, String cosmeticId) {
        RegisteredCosmetic cosmetic = SpvCosmetics.getCosmetic(cosmeticId);
        if (cosmetic != null) {
            selectedCosmetics.put(type, cosmetic);
        }
    }
    
    public Map<CosmeticType, RegisteredCosmetic> getAllCosmetics() {
        return new HashMap<>(selectedCosmetics);
    }
    
    public void uploadToServer() {
        Map<String, String> cosmeticsData = new HashMap<>();
        for (Map.Entry<CosmeticType, RegisteredCosmetic> entry : selectedCosmetics.entrySet()) {
            cosmeticsData.put(entry.getKey().getId(), entry.getValue().getId());
        }
        
        // Send packet to server
        CosmeticsUpdatePacket packet = new CosmeticsUpdatePacket(cosmeticsData);
        ClientPlayNetworking.send(CosmeticsUpdatePacket.ID, packet.toPacketByteBuf());
    }
    
    public void reset() {
        selectedCosmetics.clear();
        loadCurrentCosmetics();
    }
}
