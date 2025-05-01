package net.dark.spv_addon.init;

import net.dark.spv_addon.Spv_addon;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Map;

import static com.sp.init.BackroomsLevels.*;


public class BackroomsLevels {
    public static final RegistryKey<World> LEVEL5_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "level5"));
    public static final RegistryKey<World> LEVELRUN_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "run"));


    public static final Map<String, RegistryKey<World>> definitions = Map.of(
            // example entries:
            "LEVEL5_WORLD", LEVEL5_WORLD_KEY,
            "LEVELRUN_WORLD", LEVELRUN_WORLD_KEY
            // … add more as needed
    );

    public static BlockPos getCurrentLevelsOrigin(RegistryKey<World> world){
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        if(world == LEVEL0_WORLD_KEY){
            return mutable.set(1,22,1);

        } else if(world == LEVEL1_WORLD_KEY){
            return mutable.set(6,22,3);

        } else if(world == LEVEL2_WORLD_KEY){
            return mutable.set(0,21,8);

        } else if(world == POOLROOMS_WORLD_KEY){
            return mutable.set(15,104,16);

        } else if(world == LEVEL5_WORLD_KEY){
            return mutable.set(13, 20, 15);

        }else if(world == LEVELRUN_WORLD_KEY){
            return mutable.set(0, 1, 0);
        }
        return null;
    }
}
