package net.dark.spv_addon.voicechat;

import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.*;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import net.dark.spv_addon.Spv_addon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class SpvAddonVoicechatPlugin implements VoicechatPlugin {
    public static VoicechatServerApi voicechatApi;
    private final ConcurrentHashMap<UUID, OpusDecoder> decoders = new ConcurrentHashMap<>();
    public static final Set<UUID> justMadeNoise = new ConcurrentSkipListSet<>();

    @Override
    public String getPluginId() {
        return Spv_addon.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        // Assign the API properly!
        decoders.clear();
        // don't clear justMadeNoise here, it's cleared per tick
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStart);
        registration.registerEvent(VoicechatServerStoppedEvent.class, this::onServerStop);
        registration.registerEvent(PlayerDisconnectedEvent.class, this::onPlayerDisconnect);
    }

    private void onMicrophonePacket(MicrophonePacketEvent event) {
        VoicechatConnection sender = event.getSenderConnection();
        if (sender != null && sender.getPlayer().getPlayer() instanceof PlayerEntity player) {
            justMadeNoise.add(player.getUuid());
        }
    }

    private void onServerStart(VoicechatServerStartedEvent event) {
        voicechatApi = event.getVoicechat();
    }

    private void onServerStop(VoicechatServerStoppedEvent event) {
        decoders.forEach(this::removePlayerDecoder);
        decoders.clear();
        justMadeNoise.clear();
    }

    private void onPlayerDisconnect(PlayerDisconnectedEvent event) {
        UUID uuid = event.getPlayerUuid();
        removePlayerDecoder(uuid, decoders.get(uuid));
        justMadeNoise.remove(uuid);
    }

    private void removePlayerDecoder(UUID uuid, OpusDecoder decoder) {
        if (decoder != null) {
            try { decoder.close(); } catch (Exception ignored) {}
        }
        decoders.remove(uuid);
    }

    /** Call this once per server tick to reset the sound status */
    public static void resetNoiseEachTick(MinecraftServer server) {
        justMadeNoise.clear();
    }
}
