package net.dark.spv_addon.voicechat;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.*;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import net.dark.spv_addon.Spv_addon;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpvAddonVoicechatPlugin implements VoicechatPlugin {
    public static VoicechatServerApi voicechatApi;
    private ConcurrentHashMap<UUID, OpusDecoder> decoders;
    public static ConcurrentHashMap<UUID, Float> speakingTime;

    public static List<UUID> justSpoke = new ArrayList<>();

    @Override
    public String getPluginId() {
        return Spv_addon.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        decoders = new ConcurrentHashMap<>();
        speakingTime = new ConcurrentHashMap<>();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::recordPlayersTalking);
        registration.registerEvent(VoicechatServerStoppedEvent.class, this::onServerStop);
        registration.registerEvent(PlayerDisconnectedEvent.class, this::playerDisconnect);
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStart);
    }

    private void recordPlayersTalking(MicrophonePacketEvent microphonePacketEvent) {
        VoicechatConnection senderConnection = microphonePacketEvent.getSenderConnection();
        if (senderConnection == null) {
            return;
        }

        if (!(senderConnection.getPlayer().getPlayer() instanceof PlayerEntity player)) {
            return;
        }

        justSpoke.add(player.getUuid());
    }

    private void onServerStart(VoicechatServerStartedEvent voicechatServerStartedEvent) {
        voicechatApi = voicechatServerStartedEvent.getVoicechat();
    }

    private void playerDisconnect(PlayerDisconnectedEvent playerDisconnectedEvent) {
        UUID playerUUID = playerDisconnectedEvent.getPlayerUuid();
        this.removePlayerDecoder(playerUUID, decoders.get(playerUUID));
        speakingTime.remove(playerUUID);
    }

    private void onServerStop(VoicechatServerStoppedEvent voicechatServerStoppedEvent) {
        decoders.forEach(this::removePlayerDecoder);
        speakingTime.clear();
    }

    private void removePlayerDecoder(UUID uuid, OpusDecoder decoder){
        if(decoder != null) {
            decoder.close();
        }
        decoders.remove(uuid);
    }
}