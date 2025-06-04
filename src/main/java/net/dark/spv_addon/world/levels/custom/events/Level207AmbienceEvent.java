package net.dark.spv_addon.world.levels.custom.events;

import com.sp.world.events.AbstractEvent;
import net.dark.spv_addon.init.ModSounds; // Replace with your sound registry class
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class Level207AmbienceEvent extends AbstractEvent {
    private int ticks = 0;
    private static final int AMBIENCE_INTERVAL = 400; // duration in ticks (20 ticks = 1 second, 400 = 20 sec)

    @Override
    public void init(World world) {
        playAmbience(world);
        this.ticks = 0;
    }

    @Override
    public void ticks(int ticks, World world) {
        this.ticks++;
        // Loop sound every interval
        if (this.ticks % AMBIENCE_INTERVAL == 0) {
            playAmbience(world);
        }
    }

    private void playAmbience(World world) {
        // Play for all players in the dimension
        world.getPlayers().forEach(player -> {
            player.playSound(ModSounds.LEVEL_207_AMBIANCE, SoundCategory.AMBIENT, 1.0F, 1.0F);
        });
    }

    @Override
    public int duration() {
        return Integer.MAX_VALUE; // Run "forever" (until the player leaves or level changes)
    }
}
