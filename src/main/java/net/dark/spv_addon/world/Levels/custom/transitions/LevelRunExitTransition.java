package net.dark.spv_addon.world.Levels.custom.transitions;

import com.sp.cca_stuff.PlayerComponent;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.BackroomsLevel.CrossDimensionTeleport;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class LevelRunExitTransition implements BackroomsLevel.LevelTransition {
    @Override
    public List<CrossDimensionTeleport> predicate(World world, PlayerComponent playerComponent, BackroomsLevel from) {
        List<CrossDimensionTeleport> teleports = new ArrayList<>();
        BlockPos playerPos = playerComponent.player.getBlockPos();

        // example: red_concrete block marks an exit trigger
        BlockPos checkPos = playerPos.down(); // check directly below player
        if (world.getBlockState(checkPos).isOf(Blocks.RED_CONCRETE)) {
            teleports.add(new CrossDimensionTeleport(
                    world,
                    playerComponent,
                    BackroomsLevels.getCurrentLevelsOrigin(BackroomsLevels.LEVELRUN_WORLD_KEY),
                    from,
                    BackroomsLevels.LEVELRUN_BACKROOMS_LEVEL // <-- you'll need to declare this in BackroomsLevels
            ));
        }
        return teleports;
    }
}
