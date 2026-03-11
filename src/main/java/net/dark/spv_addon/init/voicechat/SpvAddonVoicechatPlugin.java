package net.dark.spv_addon.init.voicechat;

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
        if (sender == null) {
            return;
        }

        Object playerObj = sender.getPlayer().getPlayer();
        if (playerObj instanceof PlayerEntity player) {
            justMadeNoise.add(player.getUuid());
            float activity = decodePacketActivity(player.getUuid(), event.getPacket().getOpusEncodedData());
            if (event.getPacket().isWhispering()) {
                activity *= 0.55f;
            }
            VoiceActivityTracker.recordVoice(player, activity);
        }
    }

    private void onServerStart(VoicechatServerStartedEvent event) {
        voicechatApi = event.getVoicechat();
    }

    private void onServerStop(VoicechatServerStoppedEvent event) {
        decoders.forEach(this::removePlayerDecoder);
        decoders.clear();
        justMadeNoise.clear();
        voicechatApi = null;
    }

    private void onPlayerDisconnect(PlayerDisconnectedEvent event) {
        UUID uuid = event.getPlayerUuid();
        removePlayerDecoder(uuid, decoders.get(uuid));
        justMadeNoise.remove(uuid);
    }

    private float decodePacketActivity(UUID uuid, byte[] opusData) {
        if (voicechatApi == null || opusData == null || opusData.length == 0) {
            return 0.18f;
        }

        OpusDecoder decoder = decoders.computeIfAbsent(uuid, ignored -> voicechatApi.createDecoder());
        if (decoder == null || decoder.isClosed()) {
            return 0.18f;
        }

        try {
            short[] pcm = decoder.decode(opusData);
            if (pcm == null || pcm.length == 0) {
                return 0.18f;
            }

            double squared = 0.0;
            int peak = 0;
            int samples = 0;
            for (int i = 0; i < pcm.length; i += 2) {
                int sample = Math.abs(pcm[i]);
                squared += sample * (double) sample;
                peak = Math.max(peak, sample);
                samples++;
            }

            if (samples == 0) {
                return 0.18f;
            }

            float rms = (float) Math.sqrt(squared / samples) / Short.MAX_VALUE;
            float peakNorm = peak / (float) Short.MAX_VALUE;
            return clamp01(rms * 0.9f + peakNorm * 0.55f);
        } catch (Exception ignored) {
            return 0.18f;
        }
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

    private float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}
