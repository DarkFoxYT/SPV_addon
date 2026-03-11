package net.dark.spv_addon.world.generation.level188;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dark.spv_addon.world.generation.framework.StructurePlacementHelper;
import net.dark.spv_addon.world.generation.framework.TemplateBackroomsChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
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
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.util.math.random.Random;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Level188ChunkGenerator extends TemplateBackroomsChunkGenerator {
    public static final Codec<Level188ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, Level188ChunkGenerator::new)
    );

    public Level188ChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;
        Random random = StructurePlacementHelper.chunkRandom(cx, cz, 0x31_38_38L);
        if (cx != 0 || cz != 0) return;

        Identifier id = new Identifier("spv_addon", "level188/root_64x64");
        Optional<StructureTemplate> tpl = StructurePlacementHelper.template(world, id);
        if (tpl.isEmpty()) return;

        BlockPos base = new BlockPos(0, 100, 0);
        StructurePlacementData data = new StructurePlacementData()
                .setMirror(BlockMirror.NONE)
                .setRotation(BlockRotation.NONE)
                .setIgnoreEntities(true);

        tpl.get().place(world, base, base, data, random, 2);
    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        if (x >= 0 && x < 64 && z >= 0 && z < 64) return 100;
        return 1;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        int h = world.getHeight();
        BlockState[] states = new BlockState[h];
        int bottom = world.getBottomY();
        for (int i = 0; i < h; i++) {
            int y = bottom + i;
            states[i] = (y == 0) ? Blocks.BEDROCK.getDefaultState() : Blocks.AIR.getDefaultState();
        }
        return new VerticalBlockSample(bottom, states);
    }

}
