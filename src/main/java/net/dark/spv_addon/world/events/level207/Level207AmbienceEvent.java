package net.dark.spv_addon.world.events.level207;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class Level207AmbienceEvent {
    private static final Identifier LEVEL_207_ID   = new Identifier("spv_addon", "level207");
    private static final Identifier AMBIENT_SOUND  = new Identifier("spv_addon", "stuff.207_ambiance");

    private static PositionedSoundInstance current;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.isPaused()) return;

            boolean in207 = client.world != null && client.world.getRegistryKey().getValue().equals(LEVEL_207_ID);

            if (!in207) {
                // stop si on quitte
                if (current != null) {
                    client.getSoundManager().stop(current);
                    current = null;
                }
                return;
            }

            // si pas en cours, on (re)lance instantanément
            if (current == null || !client.getSoundManager().isPlaying(current)) {
                SoundEvent ev = Registries.SOUND_EVENT.get(AMBIENT_SOUND);
                if (ev != null) {
                    current = PositionedSoundInstance.master(ev, 1.0f, 1.0f); // volume=1, pitch=1
                    client.getSoundManager().play(current);
                }
            }
        });
    }
}
