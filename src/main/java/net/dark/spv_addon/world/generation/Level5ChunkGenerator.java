// File: net.dark.spv_addon.world.generation.Level5ChunkGenerator.java

package net.dark.spv_addon.world.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.List;

public class Level5ChunkGenerator extends ChunkGenerator {
    public static final Codec<Level5ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource)
            ).apply(instance, instance.stable(Level5ChunkGenerator::new))
    );
    public Level5ChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig config, StructureAccessor accessor, Chunk chunk) {
        this.generateMaze((StructureWorldAccess) chunk.getPos(), chunk);
        return CompletableFuture.completedFuture(chunk);
    }

    public void generateMaze(StructureWorldAccess world, Chunk chunk) {
        int x = chunk.getPos().getStartX();
        int z = chunk.getPos().getStartZ();
        Random random = Random.create();
        MinecraftServer server = world.getServer();
        if (server == null) return;

        StructureTemplateManager manager = server.getStructureTemplateManager();
        StructurePlacementData placement = new StructurePlacementData()
                .setMirror(BlockMirror.NONE)
                .setRotation(BlockRotation.NONE)
                .setIgnoreEntities(true);

        Identifier id = new Identifier("spv_addon", "level5/room" + random.nextBetween(1, 8));
        Optional<StructureTemplate> template = manager.getTemplate(id);

        BlockPos placePos = new BlockPos(x, 20, z);
        template.ifPresent(t -> t.place(world, placePos, placePos, placement, random, 2));
    }

    @Override public int getSeaLevel() { return 0; }
    @Override public int getMinimumY() { return 0; }
    @Override public int getHeight(int x, int z, Heightmap.Type type, HeightLimitView view, NoiseConfig config) {
        return getWorldHeight();
    }
    @Override public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView view, NoiseConfig config) {
        return new VerticalBlockSample(0, new BlockState[view.getHeight()]);
    }
    @Override public void getDebugHudText(List<String> text, NoiseConfig config, BlockPos pos) {}
    @Override public int getWorldHeight() { return 384; }
    @Override public void carve(ChunkRegion region, long seed, NoiseConfig config, BiomeAccess access, StructureAccessor accessor, Chunk chunk, GenerationStep.Carver step) {}
    @Override public void buildSurface(ChunkRegion region, StructureAccessor accessor, NoiseConfig config, Chunk chunk) {}
    @Override public void populateEntities(ChunkRegion region) {}
}
