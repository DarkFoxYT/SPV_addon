package net.dark.spv_addon.world.generation.maze;

import com.sp.SPBRevamped;
import net.minecraft.block.BlockState;
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

public class LowVarCell {
    private int y;
    private int x;

    private int gridPosX;
    private int gridPosY;

    private int cellSize;
    private boolean north;
    private boolean west;
    private boolean south;
    private boolean east;
    private boolean visited;
    String type;

    BlockState blockState;

    public LowVarCell(int y, int x, int cellSize, BlockState blockState, int gridPosY, int gridPosX){
        this.x = x;
        this.y = y;
        this.gridPosX = gridPosX;
        this.gridPosY = gridPosY;
        this.cellSize = cellSize;
        this.north = true;
        this.west = true;
        this.south = true;
        this.east = true;
        this.blockState = blockState;
        this.visited = false;
    }




    public void drawWalls(StructureWorldAccess world, String level){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
        Optional<StructureTemplate> optional;
        StructurePlacementData structurePlacementData = new StructurePlacementData();

        Identifier roomId;
        Random random = Random.create();
        int roomNumber = random.nextBetween(1, 8);

        if(!north && !west && !south && !east){
            type = "╬";
            roomId = new Identifier("spv_addon", level + "/hallway" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.NONE);
        }
        else if(north && !west && !south && !east){
            type = "╦";
            roomId = new Identifier("spv_addon", level + "/chall" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.CLOCKWISE_180);
        }
        else if(!north && west && !south && !east){
            type = "╠";
            roomId = new Identifier("spv_addon", level + "/chall" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.CLOCKWISE_90);
        }
        else if(!north && !west && south && !east){
            type = "╩";
            roomId = new Identifier("spv_addon", level + "/chall" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.NONE);
        }
        else if(!north && !west && !south && east){
            type = "╣";
            roomId = new Identifier("spv_addon", level + "/chall" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.COUNTERCLOCKWISE_90);
        }
        else if(north && west && !south && !east){
            type = "╔";
            roomId = new Identifier("spv_addon", level + "/dhall" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.CLOCKWISE_90);
        }
        else if(north && !west && south && !east){
            type = "═";
            roomId = new Identifier("spv_addon", level + "/dhall" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.CLOCKWISE_90);
        }
        else if(north && !west && !south && east){
            type = "╗";
            roomId = new Identifier("spv_addon", level + "/dhall" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.CLOCKWISE_180);
        }
        else if(!north && west && south && !east){
            type = "╚";
            roomId = new Identifier("spv_addon", level + "/dhall" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.NONE);
        }
        else if(!north && west && !south && east){
            type = "║";
            roomId = new Identifier("spv_addon", level + "/dhall" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.NONE);
        }
        else if(!north && !west && south && east){
            type = "╝";
            roomId = new Identifier("spv_addon", level + "/dhall" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.COUNTERCLOCKWISE_90);
        }
        else if(north && west && south && !east){
            type = "╞";
            roomId = new Identifier("spv_addon", level + "/hallway" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.CLOCKWISE_90);
        }
        else if(!north && west && south && east){
            type = "╨";
            roomId = new Identifier("spv_addon", level + "/hallway" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.NONE);
        }
        else if(north && !west && south && east){
            type = "╡";
            roomId = new Identifier("spv_addon", level + "/hallway" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.COUNTERCLOCKWISE_90);
        }
        else if(north && west && !south && east){
            type = "╥";
            roomId = new Identifier("spv_addon", level + "/hallway" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.CLOCKWISE_180);
        }
        else {
            roomId = new Identifier("spv_addon", level + "/hallway" + roomNumber);
            structurePlacementData.setRotation(BlockRotation.NONE);
        }

        structurePlacementData.setMirror(BlockMirror.NONE).setIgnoreEntities(true);
        optional = structureTemplateManager.getTemplate(roomId);

        BlockPos pos = switch (structurePlacementData.getRotation()) {
            case NONE -> new BlockPos(getX(), 20, getY());
            case CLOCKWISE_90 -> new BlockPos(getX() + (cellSize - 1), 20, getY());
            case COUNTERCLOCKWISE_90 -> new BlockPos(getX(), 20, getY() + (cellSize - 1));
            case CLOCKWISE_180 -> new BlockPos(getX() + (cellSize - 1), 20, getY() + (cellSize - 1));
        };

        optional.ifPresent(template -> template.place(world, pos, pos, structurePlacementData, random, 2));
    }


    public int getGridPosX() {
        return this.gridPosX;
    }

    public int getGridPosY() {
        return this.gridPosY;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setEast(boolean east) {
        this.east = east;
    }

    public void setSouth(boolean south) {
        this.south = south;
    }

    public void setWest(boolean west) {
        this.west = west;
    }

    public void setNorth(boolean north) {
        this.north = north;
    }

    public boolean isNorth() {
        return this.north;
    }

    public boolean isWest() {
        return this.west;
    }

    public boolean isSouth() {
        return this.south;
    }

    public boolean isEast() {
        return this.east;
    }


}
