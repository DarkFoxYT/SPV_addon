package net.dark.spv_addon.world.levels.custom.events;

import com.sp.world.events.AbstractEvent;
import net.minecraft.world.World;

public class HaHvavCustomEvent extends AbstractEvent {
    @Override
    public void init(World world) {

    }

    @Override
    public void ticks(int ticks, World world) {


        super.ticks(ticks, world);
    }

    @Override
    public int duration() {
        return 0;
    }

    @Override
    public void reset(World world) {
        super.reset(world);
    }
}
