package net.dark.spv_addon.world.levels.custom.official_levels.transitions;

import com.sp.init.BackroomsLevels;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level2BackroomsLevel;

import java.util.ArrayList;
import java.util.List;

public class Level2ToIkeaBackroomsLevel extends Level2BackroomsLevel {
    public Level2ToIkeaBackroomsLevel() {
        super();
        this.unregisterTransition("level2 -> poolrooms");
        this.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.CrossDimensionTeleport> playerList = new ArrayList();
            int exitRadius = com.sp.compat.modmenu.ConfigStuff.exitSpawnRadius;
            if (from instanceof Level2BackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius && playerComponent.player.getWorld().getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                playerList.add(new BackroomsLevel.CrossDimensionTeleport(
                        playerComponent.player.getWorld(),
                        playerComponent,
                        this.getSpawnPos(),
                        BackroomsLevels.LEVEL2_BACKROOMS_LEVEL,
                        net.dark.spv_addon.init.BackroomsLevels.LEVEL_IKEA_BACKROOMS_LEVEL
                ));
            }
            return playerList;
        }, "level2 -> level_ikea");
    }
}