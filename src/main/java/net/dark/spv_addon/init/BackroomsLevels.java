package net.dark.spv_addon.init;

import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.world.levels.custom.GlitchedBackroomsLevel;
import net.dark.spv_addon.world.levels.custom.Level188BackroomsLevel;
import net.dark.spv_addon.world.levels.custom.Level207BackroomsLevel;
import net.dark.spv_addon.world.levels.custom.LevelKittyBackroomsLevel;
import net.dark.spv_addon.world.levels.custom.LevelRUNBackroomsLevel;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BackroomsLevels {
    // === SPV DIMENSIONS ===
    public static final RegistryKey<World> LEVEL188_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "level188"));
    public static final RegistryKey<World> LEVELRUN_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "run"));
    public static final RegistryKey<World> LEVEL_IKEA_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "level_ikea"));
    public static final RegistryKey<World> LEVEL_KITTY_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "level_kitty"));
    public static final RegistryKey<World> LEVEL207_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "level207"));
    public static final RegistryKey<World> GLITCHED_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "glitched"));

    // === SPV LEVELs ===
    public static final BackroomsLevel LEVEL188_BACKROOMS_LEVEL = new Level188BackroomsLevel();
    public static final BackroomsLevel LEVELRUN_BACKROOMS_LEVEL = new LevelRUNBackroomsLevel();
    //public static final BackroomsLevel LEVEL_IKEA_BACKROOMS_LEVEL = new LevelIKEA();
    public static final BackroomsLevel LEVEL_KITTY_BACKROOMS_LEVEL = new LevelKittyBackroomsLevel();
    public static final BackroomsLevel LEVEL207_BACKROOMS_LEVEL = new Level207BackroomsLevel();
    public static final BackroomsLevel GLITCHED_BACKROOMS_LEVEL = new GlitchedBackroomsLevel();


    public static void init() {
        com.sp.init.BackroomsLevels.BACKROOMS_LEVELS.add(LEVEL188_BACKROOMS_LEVEL);
        com.sp.init.BackroomsLevels.BACKROOMS_LEVELS.add(LEVELRUN_BACKROOMS_LEVEL);
        //com.sp.init.BackroomsLevels.BACKROOMS_LEVELS.add(LEVEL_IKEA_BACKROOMS_LEVEL);
        com.sp.init.BackroomsLevels.BACKROOMS_LEVELS.add(LEVEL_KITTY_BACKROOMS_LEVEL);
        com.sp.init.BackroomsLevels.BACKROOMS_LEVELS.add(LEVEL207_BACKROOMS_LEVEL);
        com.sp.init.BackroomsLevels.BACKROOMS_LEVELS.add(GLITCHED_BACKROOMS_LEVEL);


        LEVEL188_BACKROOMS_LEVEL.register();
        LEVELRUN_BACKROOMS_LEVEL.register();
        //LEVEL_IKEA_BACKROOMS_LEVEL.register();
        LEVEL_KITTY_BACKROOMS_LEVEL.register();
        LEVEL207_BACKROOMS_LEVEL.register();
        GLITCHED_BACKROOMS_LEVEL.register();


    }
}
