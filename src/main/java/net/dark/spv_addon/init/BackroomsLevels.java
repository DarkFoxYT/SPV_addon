package net.dark.spv_addon.init;

import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.InfiniteGrassBackroomsLevel;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import com.sp.world.levels.custom.OverworldRepresentingBackroomsLevel;
import com.sp.world.levels.custom.PoolroomsBackroomsLevel;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.world.Levels.custom.Level5BackroomsLevel;
import net.dark.spv_addon.world.Levels.custom.LevelRUNBackroomsLevel;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackroomsLevels {
    // === DIM TYPES ===
    public static final RegistryKey<DimensionType> LEVEL0_DIM_TYPE =
            RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier("spb-revamped", "level0_type"));

    // === SPB DIMENSIONS ===
    public static final RegistryKey<World> LEVEL0_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier("spb-revamped", "level0"));
    public static final RegistryKey<World> LEVEL1_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier("spb-revamped", "level1"));
    public static final RegistryKey<World> LEVEL2_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier("spb-revamped", "level2"));
    public static final RegistryKey<World> POOLROOMS_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier("spb-revamped", "poolrooms"));
    public static final RegistryKey<World> INFINITE_FIELD_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier("spb-revamped", "infinite_field"));

    // === SPV DIMENSIONS ===
    public static final RegistryKey<World> LEVEL5_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "level5"));
    public static final RegistryKey<World> LEVELRUN_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Spv_addon.MOD_ID, "run"));

    // === LEVEL OBJECTS ===
    public static final BackroomsLevel LEVEL0_BACKROOMS_LEVEL = new Level0BackroomsLevel();
    public static final BackroomsLevel LEVEL1_BACKROOMS_LEVEL = new Level1BackroomsLevel();
    public static final BackroomsLevel LEVEL2_BACKROOMS_LEVEL = new Level2BackroomsLevel();
    public static final BackroomsLevel POOLROOMS_BACKROOMS_LEVEL = new PoolroomsBackroomsLevel();
    public static final BackroomsLevel INFINITE_FIELD_BACKROOMS_LEVEL = new InfiniteGrassBackroomsLevel();
    public static final BackroomsLevel OVERWORLD_REPRESENTING_BACKROOMS_LEVEL = new OverworldRepresentingBackroomsLevel();

    // === SPV LEVEL OBJECTS ===
    public static final BackroomsLevel LEVEL5_BACKROOMS_LEVEL = new Level5BackroomsLevel();
    public static final BackroomsLevel LEVELRUN_BACKROOMS_LEVEL = new LevelRUNBackroomsLevel();
    // add more addon levels here...

    public static final List<BackroomsLevel> BACKROOMS_LEVELS = new ArrayList<>();
    public static final Map<String, RegistryKey<World>> definitions = new HashMap<>();

    public static void init() {
        // Register SPB Levels
        BACKROOMS_LEVELS.add(LEVEL0_BACKROOMS_LEVEL);
        BACKROOMS_LEVELS.add(LEVEL1_BACKROOMS_LEVEL);
        BACKROOMS_LEVELS.add(LEVEL2_BACKROOMS_LEVEL);
        BACKROOMS_LEVELS.add(POOLROOMS_BACKROOMS_LEVEL);
        BACKROOMS_LEVELS.add(INFINITE_FIELD_BACKROOMS_LEVEL);
        BACKROOMS_LEVELS.add(OVERWORLD_REPRESENTING_BACKROOMS_LEVEL);

        // Register SPV Levels
        BACKROOMS_LEVELS.add(LEVEL5_BACKROOMS_LEVEL);
        // add new levels to list...

        // Register transitions
        for (BackroomsLevel level : BACKROOMS_LEVELS) {
            level.register();
        }

        // World key mapping
        definitions.put("LEVEL0", LEVEL0_WORLD_KEY);
        definitions.put("LEVEL1", LEVEL1_WORLD_KEY);
        definitions.put("LEVEL2", LEVEL2_WORLD_KEY);
        definitions.put("POOLROOMS", POOLROOMS_WORLD_KEY);
        definitions.put("INFINITE_FIELD", INFINITE_FIELD_WORLD_KEY);
        definitions.put("LEVEL5_WORLD", LEVEL5_WORLD_KEY);
        definitions.put("LEVELRUN_WORLD", LEVELRUN_WORLD_KEY);
    }

    public static BackroomsLevel getLevel(World world) {
        for (BackroomsLevel level : BACKROOMS_LEVELS) {
            if (level.getWorldKey().equals(world.getRegistryKey())) {
                return level;
            }
        }
        return null;
    }

    public static boolean isInBackrooms(RegistryKey<World> world) {
        return BACKROOMS_LEVELS.stream()
                .anyMatch(level -> level.getWorldKey().equals(world) && !level.getWorldKey().equals(World.OVERWORLD));
    }

    public static Vec3d getCurrentLevelsOrigin(RegistryKey<World> world) {
        for (BackroomsLevel level : BACKROOMS_LEVELS) {
            if (level.getWorldKey().equals(world)) {
                return level.getSpawnPos();
            }
        }
        return null;
    }
}
