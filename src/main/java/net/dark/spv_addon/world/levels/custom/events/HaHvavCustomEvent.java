package net.dark.spv_addon.world.levels.custom.events;

import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.world.World;
import net.minecraft.util.math.random.Random;

public class HaHvavCustomEvent extends AbstractEvent {

    @Override
    public void init(World world) {
        Random random = Random.create();
        boolean far = random.nextBoolean();

        if(far){
            playDistantSound(world, ModSounds.AMBIENCE);
        } else {
            playSound(world, ModSounds.INTERCOM_BASIC1);
        }
    }

    @Override
    public int duration() {
        return 200;
    }
}
