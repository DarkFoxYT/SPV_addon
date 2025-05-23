package net.dark.spv_addon.world.generation.kitty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevampedClient;
import net.dark.spv_addon.Spv_addon;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
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

public final class KittyChunkGenerator extends ChunkGenerator {
    public static final Codec<KittyChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, KittyChunkGenerator::new)
    );

    private final RegistryEntry<ChunkGeneratorSettings> settings;
    private final Random random = Random.create();

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    public KittyChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;
        SPBRevampedClient.setInBackrooms(true);
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;

        // Only top-left chunk of each 2x2 room
        if (cx % 2 == 0 && cz % 2 == 0) {
            int rx = cx / 2;
            int rz = cz / 2;

            Identifier roomId;
            if (rx == 0 && rz == 0) {
                roomId = new Identifier(Spv_addon.MOD_ID, "kitty/entrance");
            } else {
                int variant = random.nextBetween(1, 20);
                roomId = new Identifier(Spv_addon.MOD_ID, "kitty/room" + variant);
            }

            MinecraftServer server = world.getServer();
            if (server == null) return;
            StructureTemplateManager mgr = server.getStructureTemplateManager();
            Optional<StructureTemplate> optTpl = mgr.getTemplate(roomId);
            if (optTpl.isEmpty()) return;

            int bx = chunk.getPos().getStartX();
            int bz = chunk.getPos().getStartZ();
            BlockPos.Mutable basePos = new BlockPos.Mutable(bx, 0, bz);

            StructurePlacementData placeData = new StructurePlacementData()
                    .setMirror(BlockMirror.NONE)
                    .setRotation(BlockRotation.NONE)
                    .setIgnoreEntities(true);

            // Place main room structure at Y=0
            optTpl.get().place(world, basePos, basePos, placeData, random, 2);
        }

        // ==== GENERATION SÛRE DU TOIT ====
        // 8x8 roof, always in current chunk, at y=6.
        {
            MinecraftServer server = world.getServer();
            if (server == null) return;
            StructureTemplateManager mgr = server.getStructureTemplateManager();
            // Variant random for every chunk
            String roofName = random.nextBoolean() ? "kitty/roof1" : "kitty/roof2";
            Identifier roofId = new Identifier(Spv_addon.MOD_ID, roofName);
            Optional<StructureTemplate> optRoof = mgr.getTemplate(roofId);
            if (optRoof.isEmpty()) return;

            // Pick a random offset within chunk so the roof is always inside (0-8 max for 8x8 structure in 16x16 chunk)
            int bx = chunk.getPos().getStartX();
            int bz = chunk.getPos().getStartZ();
            int maxOffset = 8; // 16 - 8
            int rx = random.nextInt(maxOffset + 1);
            int rz = random.nextInt(maxOffset + 1);
            int px = bx + rx;
            int pz = bz + rz;

            BlockPos.Mutable roofPos = new BlockPos.Mutable(px, 6, pz);

            StructurePlacementData placeData = new StructurePlacementData()
                    .setMirror(BlockMirror.NONE)
                    .setRotation(BlockRotation.NONE)
                    .setIgnoreEntities(true);

            // Place one roof per chunk, Y=6, never out of chunk bounds
            optRoof.get().place(world, roofPos, roofPos, placeData, random, 2);
        }
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
            states[i] = Blocks.AIR.getDefaultState();
        }
        return new VerticalBlockSample(0, states);
    }

    @Override public void carve(ChunkRegion region, long seed,
                                NoiseConfig noiseConfig, net.minecraft.world.biome.source.BiomeAccess biomeAccess,
                                StructureAccessor structAcc, Chunk chunk,
                                GenerationStep.Carver carverStep) {}

    @Override public void buildSurface(ChunkRegion region,
                                       StructureAccessor structAcc,
                                       NoiseConfig noiseConfig,
                                       Chunk chunk) {}

    @Override public void populateEntities(ChunkRegion region) {}
    @Override public void getDebugHudText(java.util.List<String> text,
                                          NoiseConfig noiseConfig,
                                          BlockPos pos) {}
}
