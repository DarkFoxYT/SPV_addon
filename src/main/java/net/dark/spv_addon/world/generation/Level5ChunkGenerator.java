package net.dark.spv_addon.world.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevamped;
import com.sp.init.ModBlocks;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.world.generation.maze.Level5MazeGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Level5ChunkGenerator extends ChunkGenerator {
    public static final Codec<Level5ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource)
            ).apply(instance, instance.stable(Level5ChunkGenerator::new)));


    public Level5ChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }



    public void generateMaze(StructureWorldAccess world, Chunk chunk) {
        int x = chunk.getPos().getStartX();
        int z = chunk.getPos().getStartZ();
        Random random = Random.create();

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        MinecraftServer server = world.getServer();

        if (server != null) {
            StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
            Optional<StructureTemplate> optional;

            int megaRooms = random.nextBetween(1, 2);

            Identifier roomIdentifier;
            StructurePlacementData structurePlacementData = new StructurePlacementData();

            //Spawn Point
            if((float) chunk.getPos().x == 0 && (float) chunk.getPos().z  == 0){
                for(int i = 0; i < 16; i++) {
                    for(int j = 0; j < 16; j++){
                        if(i == 0 && j == 0){
                            world.setBlockState(mutable.set(i, 25, j), ModBlocks.GhostCeilingTile.getDefaultState(), 16);
                        } else {
                            world.setBlockState(mutable.set(i, 25, j), ModBlocks.CeilingTile.getDefaultState(), 16);
                        }
                    }
                }
                world.setBlockState(mutable.set(0, 25, 0), ModBlocks.GhostCeilingTile.getDefaultState(), 16);

                roomIdentifier = new Identifier(Spv_addon.MOD_ID, "level5/mega5room1");
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
                optional = structureTemplateManager.getTemplate(roomIdentifier);

                if (optional.isPresent()) {
                    optional.get().place(
                            world,
                            mutable.set(x - 32, 18, z - 32),
                            mutable.set(x - 32, 18, z - 32),
                            structurePlacementData, random, 2);
                    optional.get().place(
                            world,
                            mutable.set(x, 18, z - 32),
                            mutable.set(x, 18, z - 32),
                            structurePlacementData, random, 2);
                    optional.get().place(
                            world,
                            mutable.set(x - 32, 18, z),
                            mutable.set(x - 32, 18, z),
                            structurePlacementData, random, 2);
                    optional.get().place(
                            world,
                            mutable.set(x, 18, z),
                            mutable.set(x, 18, z),
                            structurePlacementData, random, 2);
                }
            } else if (((float) chunk.getPos().x) % SPBRevamped.finalMazeSize == 0 && ((float) chunk.getPos().z) % SPBRevamped.finalMazeSize == 0) {

                if(!chunk.getPos().getBlockPos(0,20,0).isWithinDistance(new Vec3i(0,20,0), 1000)) {
                    if(megaRooms != 1){
//                        exit = random.nextBetween(1,1);
//                        if(exit == 1){

                        roomIdentifier = new Identifier(Spv_addon.MOD_ID, "level5/exit_1");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
                        optional = structureTemplateManager.getTemplate(roomIdentifier);

                        if (optional.isPresent()) {
                            optional.get().place(
                                    world,
                                    mutable.set(x + 15,4,z + 15),
                                    mutable.set(x + 15,4,z + 15),
                                    structurePlacementData, random, 2
                            );
                        }

//                        }
                    }
                }

                if (megaRooms == 1) {
                    if (!isNearMegaRooms(x, z, world)) {

                        megaRooms = random.nextBetween(1, 6);
                        roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "level5/mega5room" + megaRooms);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
                        optional = structureTemplateManager.getTemplate(roomIdentifier);

                        if (optional.isPresent()) {
                            if (megaRooms == 1 || megaRooms == 2) {
                                optional.get().place(
                                        world,
                                        mutable.set(x - 32, 18, z - 32),
                                        mutable.set(x - 32, 18, z - 32),
                                        structurePlacementData, random, 2);
                                optional.get().place(
                                        world,
                                        mutable.set(x, 18, z - 32),
                                        mutable.set(x, 18, z - 32),
                                        structurePlacementData, random, 2);
                                optional.get().place(
                                        world,
                                        mutable.set(x - 32, 18, z),
                                        mutable.set(x - 32, 18, z),
                                        structurePlacementData, random, 2);
                                optional.get().place(
                                        world,
                                        mutable.set(x, 18, z),
                                        mutable.set(x, 18, z),
                                        structurePlacementData, random, 2);
                            } else {
                                optional.get().place(
                                        world,
                                        mutable.set(x - 16, 18, z - 16),
                                        mutable.set(x - 16, 18, z - 16),
                                        structurePlacementData, random, 2);
                                Level5MazeGenerator level5MazeGenerator = new Level5MazeGenerator(16, 5, 5, x, z, "level5");
                                level5MazeGenerator.setup(world);
                            }
                        }
                    } else {

                        Level5MazeGenerator level5MazeGenerator = new Level5MazeGenerator(16, 5, 5, x, z, "level5");
                        level5MazeGenerator.setup(world);

                    }
                } else {

                    Level5MazeGenerator level5MazeGenerator = new Level5MazeGenerator(16, 5, 5, x, z, "level5");
                    level5MazeGenerator.setup(world);

                }


            }


            ////Code for 8 x 8 Roof////
            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < 2; j++) {
                    roomIdentifier = this.getRoof();
                    structurePlacementData = this.randRotation();
                    optional = structureTemplateManager.getTemplate(roomIdentifier);

                    if (optional.isPresent()) {
                        if (world.getBlockState(mutable.set(x + 8 * i, 18, z + 8 * j)) != Blocks.CYAN_WOOL.getDefaultState() && world.getBlockState(mutable.set(x + 8 * i, 25, z + 8 * j)) == Blocks.AIR.getDefaultState() ){
                            if (structurePlacementData.getRotation() == BlockRotation.CLOCKWISE_90) {
                                optional.get().place(world, new BlockPos((x + 7) + 8 * i, 25, (z) + 8 * j), mutable.set((x + 7) + 8 * i, 25, (z) + 8 * j), structurePlacementData, random, 16);
                            } else {
                                optional.get().place(world, new BlockPos((x) + 8 * i, 25, (z) + 8 * j), mutable.set((x) + 8 * i, 25, (z) + 8 * j), structurePlacementData, random, 16);
                            }
                        }
                    }
                }
            }

        }

    }

    public boolean isNearMegaRooms(int x, int z,StructureWorldAccess world){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        boolean near = false;

        for(int i = -80; i <= 80; i += 80){
            for(int j = -80; j <= 80; j += 80){
                BlockState blockState = world.getBlockState(mutable.set(x + i, 19, z + j));
                if (blockState == Blocks.RED_WOOL.getDefaultState()){
                    near = true;
                    break;
                }
            }
        }

        return near;
    }

    public Identifier getRoof(){
        Random random = Random.create();
        int roofNumber = random.nextBetween(1,5);

        if (roofNumber == 1){
            return new Identifier(Spv_addon.MOD_ID, "level5/hotel_roof2");
        }
        else {
            return new Identifier(Spv_addon.MOD_ID, "level5/hotel_roof1");
        }


    }

    public StructurePlacementData randRotation(){
        StructurePlacementData structurePlacementData = new StructurePlacementData();
        Random random = Random.create();
        int rot = random.nextBetween(1,2);

        if(rot == 1){
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        }else{
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        }
        return structurePlacementData;
    }









    /* this method builds the shape of the terrain. it places stone everywhere, which will later be overwritten with grass, terracotta, snow, sand, etc.
         by the buildSurface method. it also is responsible for putting the water in oceans. it returns a CompletableFuture-- you'll likely want this to be delegated to worker threads. */
    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }


    @Override
    public int getSeaLevel() {
        return 0;
    }

    /* the lowest value that blocks can be placed in the world. in a vanilla world, this is -64. */
    @Override
    public int getMinimumY() {
        return 0;
    }

    /* this method returns the height of the terrain at a given coordinate. it's used for structure generation */
    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.getWorldHeight();
    }

    /* this method returns a "core sample" of the world at a given coordinate. it's used for structure generation */
    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        BlockState[] states = new BlockState[world.getHeight()];

        for (int i = 0; i < states.length; i++) {
            states[i] = Blocks.AIR.getDefaultState();
        }

        return new VerticalBlockSample(0, states);
    }

    /* this method adds text to the f3 menu. for NoiseChunkGenerator, it's the NoiseRouter line */
    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    /* the distance between the highest and lowest points in the world. in vanilla, this is 384 (64+325) */
    @Override
    public int getWorldHeight() {
        return 384;
    }




    /* the method that creates non-noise caves (i.e., all the caves we had before the caves and cliffs update) */
    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
    }

    /* the method that places grass, dirt, and other things on top of the world, as well as handling the bedrock and deepslate layers,
    as well as a few other miscellaneous things. without this method, your world is just a blank stone (or whatever your default block is) canvas (plus any ores, etc.) */
    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {

    }

    /* this method spawns entities in the world */
    @Override
    public void populateEntities(ChunkRegion region) {

    }
}
