package net.dark.spv_addon.world.generation;

import net.dark.spv_addon.Spv_addon;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Mega5RoomGenerator {

    private static final List<BlockBox> reservedRooms = new ArrayList<>();

    public static List<BlockBox> getReservedRooms() {
        return reservedRooms;
    }

    /**
     * Spawns a random mega5room at given chunk position, with safer rules.
     */
    public static void placeMega5Room(StructureWorldAccess world, int chunkX, int chunkZ) {
        Random random = Random.create();

        int blockX = chunkX * 16;
        int blockZ = chunkZ * 16;
        double distanceFromSpawn = Math.sqrt(blockX * blockX + blockZ * blockZ);

        if (distanceFromSpawn < 100) {
            return; // Too close to (0,0), skip
        }

        if (random.nextBetween(0, 14) != 0) {
            return; // Only generate about 1/15 chunks randomly
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable(blockX, 18, blockZ);
        StructureTemplateManager templateManager = world.getServer().getStructureTemplateManager();

        String[] sizes = {"small", "medium", "large"};
        String chosenSize = sizes[random.nextInt(sizes.length)];
        int roomNumber = random.nextBetween(1, 5);

        Identifier structureId = new Identifier(Spv_addon.MOD_ID, "level0/mega5room_" + chosenSize + "_" + roomNumber);

        Optional<StructureTemplate> optionalTemplate = templateManager.getTemplate(structureId);

        if (optionalTemplate.isEmpty()) {
            return; // Missing structure? Quietly skip.
        }

        StructureTemplate template = optionalTemplate.get();
        StructurePlacementData placementData = new StructurePlacementData().setIgnoreEntities(true);

        BlockPos placementPos;
        int halfSize;

        switch (chosenSize) {
            case "small" -> {
                placementPos = mutable;
                halfSize = 8; // 16x16/2
            }
            case "medium" -> {
                placementPos = mutable.add(-4, 0, -4);
                halfSize = 12; // 24x24/2
            }
            case "large" -> {
                placementPos = mutable.add(-16, 0, -16);
                halfSize = 24; // 48x48/2
            }
            default -> {
                return;
            }
        }

        template.place(world, placementPos, placementPos, placementData, random, 2);

        // Reserve this area so Maze won't generate here
        reservedRooms.add(new BlockBox(
                placementPos.getX() - halfSize, placementPos.getY(), placementPos.getZ() - halfSize,
                placementPos.getX() + halfSize, placementPos.getY() + 20, placementPos.getZ() + halfSize
        ));
    }
}
