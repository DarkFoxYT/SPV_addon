package net.dark.spv_addon.world.levels.custom.events;

import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class HaHvavCustomEvent extends AbstractEvent {

    @Override
    public void init(World world) {
        Random random = Random.create();
        boolean far = random.nextBoolean();

        if(far){
            playDistantSound(world, ModSounds.AMBIENCE);
        } else {
            playSound(world, ModSounds.CARPET_RUN);
        }
    }

    @Override
    public int duration() {
        return 200;
    }
}
