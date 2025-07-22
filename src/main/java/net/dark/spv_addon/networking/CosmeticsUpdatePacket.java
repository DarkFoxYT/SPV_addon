package net.dark.spv_addon.networking;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cosmetics.CosmeticType;
import net.dark.spv_addon.cosmetics.SpvCosmetics;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CosmeticsUpdatePacket {
    public static final Identifier ID = new Identifier(Spv_addon.MOD_ID, "cosmetics_update");
    
    private final Map<String, String> cosmeticsData;
    
    public CosmeticsUpdatePacket(Map<String, String> cosmeticsData) {
        this.cosmeticsData = cosmeticsData;
    }
    
    public CosmeticsUpdatePacket(PacketByteBuf buf) {
        this.cosmeticsData = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            String typeId = buf.readString();
            String cosmeticId = buf.readString();
            cosmeticsData.put(typeId, cosmeticId);
        }
    }
    
    public PacketByteBuf toPacketByteBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(cosmeticsData.size());
        for (Map.Entry<String, String> entry : cosmeticsData.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeString(entry.getValue());
        }
        return buf;
    }
    
    public Map<String, String> getCosmeticsData() {
        return cosmeticsData;
    }
    
    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {
            CosmeticsUpdatePacket packet = new CosmeticsUpdatePacket(buf);
            
            server.execute(() -> {
                handleServerReceive(player, packet);
            });
        });
    }
    
    private static void handleServerReceive(ServerPlayerEntity player, CosmeticsUpdatePacket packet) {
        try {
            var component = InitializeComponents.COSMETICS.get(player);
            
            for (Map.Entry<String, String> entry : packet.getCosmeticsData().entrySet()) {
                try {
                    CosmeticType type = CosmeticType.valueOf(entry.getKey().toUpperCase());
                    String cosmeticId = entry.getValue();
                    
                    // Validate that the cosmetic exists
                    if (SpvCosmetics.cosmeticExists(cosmeticId) || "none".equals(cosmeticId)) {
                        component.setEquippedCosmetic(type, cosmeticId);
                    } else {
                        Spv_addon.LOGGER.warn("Player {} tried to equip unknown cosmetic: {}", 
                            player.getName().getString(), cosmeticId);
                    }
                } catch (IllegalArgumentException e) {
                    Spv_addon.LOGGER.warn("Player {} sent invalid cosmetic type: {}", 
                        player.getName().getString(), entry.getKey());
                }
            }
            
            Spv_addon.LOGGER.debug("Updated cosmetics for player: {}", player.getName().getString());
        } catch (Exception e) {
            Spv_addon.LOGGER.error("Error handling cosmetics update for player {}: {}", 
                player.getName().getString(), e.getMessage());
        }
    }
}
