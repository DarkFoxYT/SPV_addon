package net.dark.spv_addon.init;

import com.sp.world.levels.BackroomsLevel;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.world.levels.custom.Level5BackroomsLevel;
import net.dark.spv_addon.world.levels.custom.LevelRUNBackroomsLevel;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BackroomsLevels {
    // === SPV DIMENSIONS ===
    public static final RegistryKey<World> LEVEL5_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "level5"));
    public static final RegistryKey<World> LEVELRUN_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "run"));

    // === SPV LEVEL OBJECTS ===
    public static final BackroomsLevel LEVEL5_BACKROOMS_LEVEL = new Level5BackroomsLevel();
    public static final BackroomsLevel LEVELRUN_BACKROOMS_LEVEL = new LevelRUNBackroomsLevel();

    public static void init() {
        com.sp.init.BackroomsLevels.BACKROOMS_LEVELS.add(LEVEL5_BACKROOMS_LEVEL);
        com.sp.init.BackroomsLevels.BACKROOMS_LEVELS.add(LEVELRUN_BACKROOMS_LEVEL);

        LEVEL5_BACKROOMS_LEVEL.register();
        LEVELRUN_BACKROOMS_LEVEL.register();
    }
}
