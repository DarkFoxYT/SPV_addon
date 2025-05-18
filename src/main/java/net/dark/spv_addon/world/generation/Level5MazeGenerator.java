package net.dark.spv_addon.world.generation;

import com.sp.world.generation.maze_generator.MazeGenerator;
import com.sp.world.generation.maze_generator.cells.CellWDoor;
import com.sp.world.generation.maze_generator.cells.HighVarCell;
import com.sp.world.generation.maze_generator.cells.LowVarCell;
import net.dark.spv_addon.Spv_addon;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.Optional;

public class Level5MazeGenerator extends MazeGenerator {
    // Adapté pour Level 5
    // size: taille de chaque salle
    // rows/cols: nb de salles
    int cols, rows, size;
    int originX, originY;
    String levelDirectory;

    public Level5MazeGenerator(int size, int rows, int cols, int originX, int originY, String levelDirectory) {
        this.size = size;
        this.rows = rows;
        this.cols = cols;
        this.originX = originX - 32;
        this.originY = originY - 32;
        this.levelDirectory = levelDirectory;
    }

    @Override
    public void setup(StructureWorldAccess world, boolean sky, boolean megaRooms, boolean spawnRandomRooms) {
        Random random = Random.create();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        StructureTemplateManager manager = world.getServer().getStructureTemplateManager();

        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.cols; x++) {
                // Exemple : rooms d'hôtel alternées (hotel_room1/hallway/hotel_room2...)
                String structName = (x + y) % 2 == 0 ? "room1" : "hallway1";
                Identifier id = new Identifier(Spv_addon.MOD_ID, "level5/" + structName);

                Optional<StructureTemplate> template = manager.getTemplate(id);
                if (template.isPresent()) {
                    int px = this.originX + x * this.size;
                    int pz = this.originY + y * this.size;
                    StructurePlacementData data = randomRotation();
                    template.get().place(world, mutable.set(px, 18, pz), mutable.set(px, 18, pz), data, random, 2);
                }
            }
        }
    }

    @Override
    public void drawWalls(StructureWorldAccess structureWorldAccess, String s) {

    }

    @Override
    public void removeWalls(HighVarCell highVarCell, HighVarCell highVarCell1) {

    }

    @Override
    public void removeWalls(LowVarCell lowVarCell, LowVarCell lowVarCell1) {

    }

    @Override
    public void removeWalls(CellWDoor cellWDoor, CellWDoor cellWDoor1) {

    }

    private StructurePlacementData randomRotation() {
        StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
        data.setRotation(Random.create().nextBetween(1, 2) == 1 ? BlockRotation.NONE : BlockRotation.CLOCKWISE_90);
        return data;
    }
}
