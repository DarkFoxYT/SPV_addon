package net.dark.spv_addon.world.events.levelkitty;

import com.sp.world.events.AbstractEvent;
import net.dark.spv_addon.init.ModSounds;
import net.dark.spv_addon.util.ServerTickScheduler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Mysterious event that creates phantom kitty sounds around the player
 * Creates an eerie but not harmful atmosphere
 */
public class PhantomKittyEvent extends AbstractEvent {
    private Random rand = new Random();

    public void trigger(World world, ServerPlayerEntity player) {
        // Play mysterious phantom sounds around the player
        int soundCount = 3 + rand.nextInt(4); // 3-6 phantom sounds

        for (int i = 0; i < soundCount; i++) {
            int delayTicks = i * 40; // 2 seconds between sounds at 20 TPS
            ServerTickScheduler.schedule(delayTicks, () -> {
                if (player.isRemoved() || !player.isAlive()) {
                    return;
                }
                switch (rand.nextInt(5)) {
                    case 0 -> player.playSound(ModSounds.SANITY_WHISPER_1, SoundCategory.AMBIENT, 0.2f, 0.8f);
                    case 1 -> player.playSound(ModSounds.SANITY_WHISPER_2, SoundCategory.AMBIENT, 0.15f, 0.9f);
                    case 2 -> player.playSound(ModSounds.SANITY_FOOTSTEPS_PHANTOM, SoundCategory.AMBIENT, 0.1f, 0.7f);
                    case 3 -> player.playSound(ModSounds.SANITY_STATIC, SoundCategory.AMBIENT, 0.1f, 1.1f);
                    case 4 -> player.playSound(ModSounds.ELEV, SoundCategory.AMBIENT, 0.1f, 0.6f);
                }
            });
        }

        // Send mysterious message
        String[] messages = {
            "You hear distant purring...",
            "Something moves in the shadows...",
            "The air feels different here...",
            "Whispers echo through the halls...",
            "A presence watches you..."
        };

        String message = messages[rand.nextInt(messages.length)];
        player.sendMessage(
            Text.literal(message).formatted(Formatting.GRAY, Formatting.ITALIC),
            false
        );
    }

    @Override
    public void init(World world) {

    }

    @Override
    public int duration() {
        return 30;
    }
}
