// path: net.dark.spv_addon.world.generation.maze.Level5MazeGenerator.java

package net.dark.spv_addon.world.generation.maze;

import com.sp.block.custom.WallBlock;
import com.sp.init.ModBlocks;
import com.sp.world.generation.maze_generator.HighVarCell;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.sp.block.custom.WallBlock.BOTTOM;

public class Level5MazeGenerator {
    int cols, rows, size;
    HighVarCell[][] grid;
    HighVarCell currentCell;
    Stack<HighVarCell> stack = new Stack<>();
    int originX, originY;
    String levelDirectory;

    public Level5MazeGenerator(int size, int rows, int cols, int originX, int originY, String dir) {
        this.size = size;
        this.rows = rows;
        this.cols = cols;
        this.originX = originX - 32;
        this.originY = originY - 32;
        this.levelDirectory = dir;
        this.grid = new HighVarCell[rows][cols];
    }

    public void setup(StructureWorldAccess world) {
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.cols; x++) {
                pos.set(x + ((this.size - 1) * x) + originX, 19, y + ((this.size - 1) * y) + originY);
                if (world.getBlockState(pos) == Blocks.AIR.getDefaultState()) {
                    grid[x][y] = new HighVarCell(
                            y + ((this.size - 1) * y) + originY,
                            x + ((this.size - 1) * x) + originX,
                            this.size,
                            ModBlocks.WallBlock.getDefaultState().with(BOTTOM, false),
                            y,
                            x
                    );
                }
            }
        }

        currentCell = grid[0][0];
        currentCell.setVisited(true);
        stack.push(currentCell);

        while (!stack.isEmpty()) {
            HighVarCell neighbor = checkNeighbors(world, currentCell.getGridPosY(), currentCell.getGridPosX());
            while (neighbor != null) {
                neighbor.setVisited(true);
                removeWalls(currentCell, neighbor);
                currentCell = neighbor;
                stack.push(currentCell);
                neighbor = checkNeighbors(world, currentCell.getGridPosY(), currentCell.getGridPosX());
            }
            currentCell = stack.pop();
        }

        for (HighVarCell[] row : grid)
            for (HighVarCell cell : row)
                if (cell != null) cell.drawWalls(world, levelDirectory);
    }

    public HighVarCell checkNeighbors(StructureWorldAccess world, int y, int x) {
        List<HighVarCell> neighbors = new ArrayList<>();

        HighVarCell north = (y + 1 < rows) ? grid[x][y + 1] : null;
        HighVarCell west = (x + 1 < cols) ? grid[x + 1][y] : null;
        HighVarCell south = (y - 1 >= 0) ? grid[x][y - 1] : null;
        HighVarCell east = (x - 1 >= 0) ? grid[x - 1][y] : null;

        if (north != null && !north.isVisited()) neighbors.add(north);
        if (west != null && !west.isVisited()) neighbors.add(west);
        if (south != null && !south.isVisited()) neighbors.add(south);
        if (east != null && !east.isVisited()) neighbors.add(east);

        if (!neighbors.isEmpty()) {
            Random rand = Random.create();
            return neighbors.get(rand.nextBetween(0, neighbors.size() - 1));
        }
        return null;
    }

    public void removeWalls(HighVarCell current, HighVarCell neighbor) {
        int dx = current.getGridPosX() - neighbor.getGridPosX();
        int dy = current.getGridPosY() - neighbor.getGridPosY();

        if (dx > 0) {
            current.setEast(false);
            neighbor.setWest(false);
        } else if (dx < 0) {
            current.setWest(false);
            neighbor.setEast(false);
        }

        if (dy > 0) {
            current.setSouth(false);
            neighbor.setNorth(false);
        } else if (dy < 0) {
            current.setNorth(false);
            neighbor.setSouth(false);
        }
    }
}
