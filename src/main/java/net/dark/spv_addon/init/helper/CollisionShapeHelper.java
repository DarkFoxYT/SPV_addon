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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CollisionShapeHelper {

    /**
     * Loads collision shapes grouped by direction prefix with numbered suffixes in the JSON model.
     * Only names like "collision_n1", "collision_e2", "collision_s3", "collision_w4" are accepted.
     *
     * Returns a map from Direction (N, E, S, W) to combined VoxelShape for that direction.
     * If no collisions found for a direction, returns empty shape.
     */
    public static Map<Direction, VoxelShape> loadDirectionalCollisionsFromModelJson(String namespace, String path) {
        Map<Direction, List<VoxelShape>> shapeMap = new EnumMap<>(Direction.class);
        // Initialize lists
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

                // Only accept names like collision_n1, collision_e2, collision_s10, collision_w3, etc.
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
            e.printStackTrace();
            // fallback to empty shapes for all directions
            Map<Direction, VoxelShape> fallback = new EnumMap<>(Direction.class);
            for (Direction d : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
                fallback.put(d, VoxelShapes.empty());
            }
            return fallback;
        }

        // Combine lists into single shapes per direction
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

        return combinedMap;
    }
}
