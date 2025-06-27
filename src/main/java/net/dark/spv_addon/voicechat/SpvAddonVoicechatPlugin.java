package net.dark.spv_addon.voicechat;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.*;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import net.dark.spv_addon.Spv_addon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class SpvAddonVoicechatPlugin implements VoicechatPlugin {
    public static final Set<UUID> justMadeNoise = new ConcurrentSkipListSet<>();
    public static VoicechatServerApi voicechatApi;
    private final ConcurrentHashMap<UUID, OpusDecoder> decoders = new ConcurrentHashMap<>();

    public static boolean hasJustMadeNoise(UUID uuid) {
        return justMadeNoise.contains(uuid);
    }

    public static void resetNoiseEachTick(MinecraftServer server) {
        justMadeNoise.clear();
    }

    @Override
    public String getPluginId() {
        return Spv_addon.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        decoders.clear();
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
        if (sender != null) {
            Object playerObj = sender.getPlayer().getPlayer();
            if (playerObj instanceof PlayerEntity player) {
                justMadeNoise.add(player.getUuid());
            }
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
            try {
                decoder.close();
            } catch (Exception ignored) {
            }
        }
        decoders.remove(uuid);
    }
}
