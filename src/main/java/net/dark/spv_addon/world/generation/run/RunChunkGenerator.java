package net.dark.spv_addon.world.generation.run;



import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevampedClient;
import net.dark.spv_addon.Spv_addon;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class RunChunkGenerator extends ChunkGenerator {
    public static final Codec<RunChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, RunChunkGenerator::new)
    );
    public static BlockPos EXIT_ROOM_CENTER = null; // Sera set dynamiquement à la génération
    public static final int EXIT_ROOM_RADIUS = 8; // à ajuster selon ta room



    private final RegistryEntry<ChunkGeneratorSettings> settings;
    private final Random random = Random.create();
    private final int corridorLength;


    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    public RunChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;
        SPBRevampedClient.setInBackrooms(true);
        this.corridorLength = random.nextBetween(200, 500);
    }
    public int getCorridorLength() {
        return corridorLength;
    }

    /**  zero‐based index of the chunk which holds the exit room  */
    public int getExitChunkIndex() {
        // (corridorLength−1)/16 is how you computed it before
        return (corridorLength - 1) / 16;
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;
        if (cz != 0) {
            SPBRevampedClient.setInBackrooms(false);
            return;
        }
        int exitChunk = (corridorLength - 1) / 16;
        Identifier roomId;
        if (cx == 0) {
            roomId = new Identifier(Spv_addon.MOD_ID, "run/entrance");
        } else if (cx == exitChunk) {
            roomId = new Identifier(Spv_addon.MOD_ID, "run/exit");
        } else if (cx > 0 && cx < exitChunk) {
            int variant = random.nextBetween(0, 4);
            String variantName = variant == 0 ? "hallway" : "hallway" + variant;
            roomId = new Identifier(Spv_addon.MOD_ID, "run/" + variantName);
        } else {
            return;
        }

        MinecraftServer server = world.getServer();
        if (server == null) return;
        StructureTemplateManager mgr = server.getStructureTemplateManager();

        // Random roof between run_roof1 and run_roof2
        String roofName = random.nextBoolean() ? "run/run_roof1" : "run/run_roof2";
        Identifier roofId = new Identifier(Spv_addon.MOD_ID, roofName);
        Optional<StructureTemplate> optRoof = mgr.getTemplate(roofId);
        if (optRoof.isEmpty()) return;

        int bx = chunk.getPos().getStartX();
        int bz = chunk.getPos().getStartZ();

        // Place roof so that it covers the entire chunk at y=6
        BlockPos.Mutable roofPos = new BlockPos.Mutable(bx, 6, bz);

        StructurePlacementData roofData = new StructurePlacementData()
                .setMirror(BlockMirror.NONE)
                .setRotation(BlockRotation.CLOCKWISE_90)
                .setIgnoreEntities(true);

        optRoof.get().place(world, roofPos, roofPos, roofData, random, 2);
    }


    @Override public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender,
                                                            NoiseConfig noiseConfig,
                                                            StructureAccessor structureAccessor,
                                                            Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override public int getSeaLevel() { return 0; }
    @Override public int getMinimumY() { return 0; }
    @Override public int getWorldHeight() { return 256; }

    @Override
    public int getHeight(int x, int z, net.minecraft.world.Heightmap.Type type,
                         net.minecraft.world.HeightLimitView view,
                         NoiseConfig noiseConfig) {
        return getWorldHeight();
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z,
                                               net.minecraft.world.HeightLimitView view,
                                               NoiseConfig noiseConfig) {
        var states = new net.minecraft.block.BlockState[getWorldHeight()];
        for (int i = 0; i < states.length; i++) {
            states[i] = net.minecraft.block.Blocks.AIR.getDefaultState();
        }
        return new VerticalBlockSample(0, states);
    }

    @Override public void carve(ChunkRegion region, long seed,
                                NoiseConfig noiseConfig, net.minecraft.world.biome.source.BiomeAccess biomeAccess,
                                StructureAccessor structAcc, Chunk chunk,
                                GenerationStep.Carver carverStep) {}
    public static boolean isPlayerInExit(ServerPlayerEntity player) {
        if (EXIT_ROOM_CENTER == null) return false;
        BlockPos p = player.getBlockPos();
        // Test sur une sphère/cube
        return Math.abs(p.getX() - EXIT_ROOM_CENTER.getX()) <= EXIT_ROOM_RADIUS &&
                Math.abs(p.getZ() - EXIT_ROOM_CENTER.getZ()) <= EXIT_ROOM_RADIUS &&
                (p.getY() >= EXIT_ROOM_CENTER.getY() && p.getY() <= EXIT_ROOM_CENTER.getY() + 6); // hauteur à adapter
    }
    @Override public void buildSurface(ChunkRegion region,
                                       StructureAccessor structAcc,
                                       NoiseConfig noiseConfig,
                                       Chunk chunk) {}

    @Override public void populateEntities(ChunkRegion region) {}
    @Override public void getDebugHudText(java.util.List<String> text,
                                          NoiseConfig noiseConfig,
                                          BlockPos pos) {}
}
