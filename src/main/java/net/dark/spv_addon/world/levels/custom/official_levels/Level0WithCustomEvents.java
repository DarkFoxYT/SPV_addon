package net.dark.spv_addon.world.levels.custom.official_levels;

import com.sp.world.levels.custom.Level0BackroomsLevel;
import net.dark.spv_addon.world.events.level0.IntercomCustom;

public class Level0WithCustomEvents extends Level0BackroomsLevel {
    public Level0WithCustomEvents() {
        super();
        this.events.add(IntercomCustom::new);
    }
}