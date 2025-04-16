package net.dark.spv_addon.init;

import com.sp.SPBRevamped;
import net.dark.spv_addon.Spv_addon;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.lang.reflect.Field;

import static com.sp.init.BackroomsLevels.*;


public class BackroomsLevels {
    public static final RegistryKey<World> LEVEL5_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "level5"));




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

        }else if(world == LEVEL5_WORLD_KEY){
            return mutable.set(1, 22, 1);
        }
        return null;
    }
}
