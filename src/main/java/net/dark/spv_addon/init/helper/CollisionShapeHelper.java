package net.dark.spv_addon.init.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CollisionShapeHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollisionShapeHelper.class);
    private static final Map<String, Map<Direction, VoxelShape>> DIRECTIONAL_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, VoxelShape> UNROTATED_CACHE = new ConcurrentHashMap<>();

    /**
     * Loads collision shapes grouped by direction prefix with numbered suffixes in the JSON model.
     * Only names like "collision_n1", "collision_e2", "collision_s3", "collision_w4" are accepted.
     * Returns a map from Direction (N, E, S, W) to combined VoxelShape for that direction.
     * If no collisions found for a direction, returns empty shape.
     */
    public static Map<Direction, VoxelShape> loadDirectionalCollisionsFromModelJson(String namespace, String path) {
        String cacheKey = namespace + ":" + path;
        Map<Direction, VoxelShape> cached = DIRECTIONAL_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Map<Direction, List<VoxelShape>> shapeMap = new EnumMap<>(Direction.class);
        for (Direction dir : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
            shapeMap.put(dir, new ArrayList<>());
        }

        try {
            Identifier id = new Identifier(namespace, "models/block/" + path + ".json");
            InputStream stream = CollisionShapeHelper.class.getClassLoader().getResourceAsStream("assets/" + id.getNamespace() + "/" + id.getPath());
            if (stream == null) {
                throw new RuntimeException("Model file not found: " + id);
            }

            JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
            JsonArray elements = json.getAsJsonArray("elements");

            for (JsonElement element : elements) {
                JsonObject obj = element.getAsJsonObject();
                if (!obj.has("name")) continue;

                String name = obj.get("name").getAsString().toLowerCase(Locale.ROOT);

                Direction dir = null;
                if (name.matches("collision_n\\d+")) dir = Direction.NORTH;
                else if (name.matches("collision_e\\d+")) dir = Direction.EAST;
                else if (name.matches("collision_s\\d+")) dir = Direction.SOUTH;
                else if (name.matches("collision_w\\d+")) dir = Direction.WEST;

                if (dir == null) continue;

                JsonArray from = obj.getAsJsonArray("from");
                JsonArray to = obj.getAsJsonArray("to");

                double x1 = from.get(0).getAsDouble();
                double y1 = from.get(1).getAsDouble();
                double z1 = from.get(2).getAsDouble();

                double x2 = to.get(0).getAsDouble();
                double y2 = to.get(1).getAsDouble();
                double z2 = to.get(2).getAsDouble();

                VoxelShape shape = Block.createCuboidShape(x1, y1, z1, x2, y2, z2);
                shapeMap.get(dir).add(shape);
            }

        } catch (Exception e) {
            LOGGER.warn("Unable to load directional collision model {}:{}", namespace, path, e);
            Map<Direction, VoxelShape> fallback = new EnumMap<>(Direction.class);
            for (Direction d : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
                fallback.put(d, VoxelShapes.empty());
            }
            DIRECTIONAL_CACHE.put(cacheKey, fallback);
            return fallback;
        }

        Map<Direction, VoxelShape> combinedMap = new EnumMap<>(Direction.class);
        for (Direction d : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
            List<VoxelShape> list = shapeMap.get(d);
            if (list.isEmpty()) {
                combinedMap.put(d, VoxelShapes.empty());
            } else {
                VoxelShape combined = list.get(0);
                for (int i = 1; i < list.size(); i++) {
                    combined = VoxelShapes.union(combined, list.get(i));
                }
                combinedMap.put(d, combined);
            }
        }

        DIRECTIONAL_CACHE.put(cacheKey, combinedMap);
        return combinedMap;
    }

    /**
     * Loads all collision boxes named like "collision_1", "collision_2", etc.
     * Ignores direction, doesn't rotate anything.
     */
    public static VoxelShape loadUnrotatedCollisionFromModelJson(String namespace, String path) {
        String cacheKey = namespace + ":" + path;
        VoxelShape cached = UNROTATED_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<VoxelShape> shapes = new ArrayList<>();

        try {
            Identifier id = new Identifier(namespace, "models/block/" + path + ".json");
            InputStream stream = CollisionShapeHelper.class.getClassLoader().getResourceAsStream("assets/" + id.getNamespace() + "/" + id.getPath());
            if (stream == null) throw new RuntimeException("Model file not found: " + id);

            JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
            JsonArray elements = json.getAsJsonArray("elements");

            for (JsonElement element : elements) {
                JsonObject obj = element.getAsJsonObject();
                if (!obj.has("name")) continue;

                String name = obj.get("name").getAsString().toLowerCase(Locale.ROOT);
                if (!name.matches("collision\\d+")) continue;

                JsonArray from = obj.getAsJsonArray("from");
                JsonArray to = obj.getAsJsonArray("to");

                double x1 = from.get(0).getAsDouble();
                double y1 = from.get(1).getAsDouble();
                double z1 = from.get(2).getAsDouble();

                double x2 = to.get(0).getAsDouble();
                double y2 = to.get(1).getAsDouble();
                double z2 = to.get(2).getAsDouble();

                VoxelShape shape = Block.createCuboidShape(x1, y1, z1, x2, y2, z2);
                shapes.add(shape);
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to load collision model {}:{}", namespace, path, e);
            UNROTATED_CACHE.put(cacheKey, VoxelShapes.empty());
            return VoxelShapes.empty();
        }

        if (shapes.isEmpty()) {
            UNROTATED_CACHE.put(cacheKey, VoxelShapes.empty());
            return VoxelShapes.empty();
        }
        VoxelShape combined = shapes.get(0);
        for (int i = 1; i < shapes.size(); i++) {
            combined = VoxelShapes.union(combined, shapes.get(i));
        }

        UNROTATED_CACHE.put(cacheKey, combined);
        return combined;
    }

}
