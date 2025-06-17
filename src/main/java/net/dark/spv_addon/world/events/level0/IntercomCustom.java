//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.dark.spv_addon.world.events.level0;

import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level0BackroomsLevel.LightState;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class IntercomCustom extends AbstractEvent {
    boolean friend = false;
    int duration = 200;

    public IntercomCustom() {
    }

    public void init(World world) {
        BackroomsLevel intercomCount = BackroomsLevels.getLevel(world);
        if (intercomCount instanceof Level0BackroomsLevel level) {
            int var6 = level.getIntercomCount();
            Random random = Random.create();
            if (var6 <= 1) {
                int rand = random.nextBetween(1, 2);
                if (rand == 1) {
                    playSoundWithRandLocation(world, net.dark.spv_addon.init.ModSounds.IC1, 25, 20);
                } else {
                    playSoundWithRandLocation(world, net.dark.spv_addon.init.ModSounds.IC2, 25, 20);
                }
            } else if (var6 == 2) {
                playSoundWithRandLocation(world, ModSounds.CARPET_RUN, 25, 20);
            } else {
                int rand = random.nextBetween(1, 1);
                if (rand == 1) {
                    playSoundWithRandLocation(world, ModSounds.POOLROOMS_DRIP1, 25, 20);
                    this.friend = true;
                    this.duration = 800;
                } else {
                    playSoundWithRandLocation(world, ModSounds.CREAKING2, 25, 20);
                }
            }

            level.addIntercomCount();
        }
    }

    public void ticks(int ticks, World world) {
        BackroomsLevel var4 = BackroomsLevels.getLevel(world);
        if (var4 instanceof Level0BackroomsLevel level) {
            if (this.friend) {
                if (ticks == 460) {
                    level.setLightState(LightState.FLICKER);
                } else if (ticks == 528) {
                    level.setLightState(LightState.OFF);
                    playSound(world, ModSounds.LIGHTS_OUT);
                } else if (ticks == 656) {
                    level.setLightState(LightState.ON);
                }
            }

        }
    }

    public int duration() {
        return this.duration;
    }
}
