package net.dark.spv_addon.world.generation.level0;

import net.dark.spv_addon.world.generation.Mega5RoomGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.List;
import java.util.Optional;

/**
 * This generates simple maze walls and rooms, skipping areas reserved by Mega5Room.
 */
public class AddonLevel0MazeGenerator {

    private final StructureWorldAccess world;
    private final String levelDirectory;

    public AddonLevel0MazeGenerator(StructureWorldAccess world, String levelDirectory) {
        this.world = world;
        this.levelDirectory = levelDirectory;
    }

    public void generate(int startX, int startZ, int sizeX, int sizeZ) {
        StructureTemplateManager templateManager = world.getServer().getStructureTemplateManager();
        Random random = Random.create();

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = 0; x < sizeX; x++) {
            for (int z = 0; z < sizeZ; z++) {

                int blockX = startX + (x * 5);
                int blockZ = startZ + (z * 5);
                mutable.set(blockX, 18, blockZ);

                if (!isPlaceable(mutable)) {
                    continue;
                }

                // Check Mega5Room reserved areas
                if (isInMega5Room(mutable)) {
                    continue; // Skip if inside reserved Mega5Room
                }

                // Pick a random room template
                int roomNumber = random.nextBetween(1, 8);
                Identifier roomId = new Identifier("spv_addon", levelDirectory + "/small_room_" + roomNumber);

                Optional<StructureTemplate> optional = templateManager.getTemplate(roomId);
                if (optional.isEmpty()) {
                    continue; // No template? Skip silently
                }

                StructureTemplate template = optional.get();
                StructurePlacementData placementData = new StructurePlacementData().setIgnoreEntities(true);

                template.place(world, mutable, mutable, placementData, random, 2);
            }
        }
    }

    private boolean isPlaceable(BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.isOf(Blocks.AIR);
    }

    private boolean isInMega5Room(BlockPos pos) {
        List<BlockBox> reserved = Mega5RoomGenerator.getReservedRooms();
        for (BlockBox box : reserved) {
            if (box.contains(pos)) {
                return true;
            }
        }
        return false;
    }
}
