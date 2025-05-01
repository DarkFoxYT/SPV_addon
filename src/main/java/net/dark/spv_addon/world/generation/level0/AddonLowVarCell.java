package net.dark.spv_addon.world.generation.level0;

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

public class AddonLowVarCell {
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

    public AddonLowVarCell(int y, int x, int cellSize, BlockState blockState, int gridPosY, int gridPosX){
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
        Random random = Random.create();
        int roomNumber = random.nextBetween(1,8);
        Identifier roomId;

        if (!this.north && !this.west && !this.south && !this.east) {
            this.type = "╬";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        } else if (this.north && !this.west && !this.south && !this.east) {
            this.type = "╦";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
        } else if (!this.north && this.west && !this.south && !this.east) {
            this.type = "╠";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        } else if (!this.north && !this.west && this.south && !this.east) {
            this.type = "╩";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        } else if (!this.north && !this.west && !this.south && this.east) {
            this.type = "╣";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
        } else if (this.north && this.west && !this.south && !this.east) {
            this.type = "╔";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        } else if (this.north && !this.west && this.south && !this.east) {
            this.type = "═";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        } else if (this.north && !this.west && !this.south && this.east) {
            this.type = "╗";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
        } else if (!this.north && this.west && this.south && !this.east) {
            this.type = "╚";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        } else if (!this.north && this.west && !this.south && this.east) {
            this.type = "║";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        } else if (!this.north && !this.west && this.south && this.east) {
            this.type = "╝";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
        } else if (this.north && this.west && this.south && !this.east) {
            this.type = "╞";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        } else if (!this.north && this.west && this.south && this.east) {
            this.type = "╨";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        } else if (this.north && !this.west && this.south && this.east) {
            this.type = "╡";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
        } else if (this.north && this.west && !this.south && this.east) {
            this.type = "╥";
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
        } else {
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom" + roomNumber);
        }

        optional = structureTemplateManager.getTemplate(roomId);

        optional.ifPresent(structureTemplate -> {
            BlockRotation rotation = structurePlacementData.getRotation();
            if (rotation == BlockRotation.NONE) {
                structureTemplate.place(world, mutable.set(this.getX(), 20, this.getY()), mutable, structurePlacementData, random, 2);
            } else if (rotation == BlockRotation.CLOCKWISE_90) {
                structureTemplate.place(world, mutable.set(this.getX() + (this.cellSize - 1), 20, this.getY()), mutable, structurePlacementData, random, 2);
            } else if (rotation == BlockRotation.COUNTERCLOCKWISE_90) {
                structureTemplate.place(world, mutable.set(this.getX(), 20, this.getY() + (this.cellSize - 1)), mutable, structurePlacementData, random, 2);
            } else if (rotation == BlockRotation.CLOCKWISE_180) {
                structureTemplate.place(world, mutable.set(this.getX() + (this.cellSize - 1), 20, this.getY() + (this.cellSize - 1)), mutable, structurePlacementData, random, 2);
            }
        });
    }

    public int getGridPosX() { return this.gridPosX; }
    public int getGridPosY() { return this.gridPosY; }
    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public void setEast(boolean east) { this.east = east; }
    public void setSouth(boolean south) { this.south = south; }
    public void setWest(boolean west) { this.west = west; }
    public void setNorth(boolean north) { this.north = north; }
}
