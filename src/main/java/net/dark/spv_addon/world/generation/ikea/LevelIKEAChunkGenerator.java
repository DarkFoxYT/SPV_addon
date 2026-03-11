package net.dark.spv_addon.world.generation.ikea;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.world.generation.framework.StructurePlacementHelper;
import net.dark.spv_addon.world.generation.framework.TemplateBackroomsChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
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

public final class LevelIKEAChunkGenerator extends TemplateBackroomsChunkGenerator {
    public static final Codec<LevelIKEAChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, LevelIKEAChunkGenerator::new)
    );

    private static final int CELL_SIZE = 32;

    public LevelIKEAChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;
        int chunkStartX = chunk.getPos().getStartX();
        int chunkStartZ = chunk.getPos().getStartZ();

        int anchorX = Math.floorDiv(chunkStartX, CELL_SIZE) * CELL_SIZE;
        int anchorZ = Math.floorDiv(chunkStartZ, CELL_SIZE) * CELL_SIZE;
        if (chunkStartX != anchorX || chunkStartZ != anchorZ) {
            return;
        }

        int cellX = Math.floorDiv(anchorX, CELL_SIZE);
        int cellZ = Math.floorDiv(anchorZ, CELL_SIZE);
        Random random = StructurePlacementHelper.chunkRandom(chunkX, chunkZ, 0x49_4B_45_41L);

        placeMainCell(world, random, anchorX, anchorZ, cellX, cellZ);
        placeDecorCell(world, random, anchorX, anchorZ, cellX, cellZ);
        placeRoofPatch(world, random, chunkStartX, chunkStartZ);
    }

    private void placeMainCell(StructureWorldAccess world, Random random, int anchorX, int anchorZ, int cellX, int cellZ) {
        int variant = 1 + Math.floorMod(cellX * 31 + cellZ * 17, 4);
        Identifier pathId = new Identifier(Spv_addon.MOD_ID, "ikea/pathway_room" + variant);
        Optional<StructureTemplate> template = StructurePlacementHelper.template(world, pathId);
        if (template.isEmpty()) {
            return;
        }

        BlockPos pos = new BlockPos(anchorX, 0, anchorZ);
        StructurePlacementData data = new StructurePlacementData()
                .setMirror(BlockMirror.NONE)
                .setIgnoreEntities(true);
        template.get().place(world, pos, pos, data, random, 2);
    }

    private void placeDecorCell(StructureWorldAccess world, Random random, int anchorX, int anchorZ, int cellX, int cellZ) {
        int side = Math.floorMod(cellX + cellZ, 2) == 0 ? 1 : -1;
        int decorX = anchorX + side * CELL_SIZE;
        int decorZ = anchorZ;
        int decorVariant = 1 + Math.floorMod(cellX * 13 + cellZ * 7 + 2, 4);
        Identifier decorId = new Identifier(Spv_addon.MOD_ID, "ikea/misc_room" + decorVariant);
        Optional<StructureTemplate> template = StructurePlacementHelper.template(world, decorId);
        if (template.isEmpty()) {
            return;
        }

        BlockPos pos = new BlockPos(decorX, 0, decorZ);
        StructurePlacementData data = new StructurePlacementData()
                .setMirror(BlockMirror.NONE)
                .setRotation(BlockRotation.values()[Math.floorMod(cellX + cellZ, BlockRotation.values().length)])
                .setIgnoreEntities(true);
        template.get().place(world, pos, pos, data, random, 2);
    }

    private void placeRoofPatch(StructureWorldAccess world, Random random, int chunkStartX, int chunkStartZ) {
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                String roofName = random.nextBoolean() ? "ikea/roof1" : "ikea/roof2";
                Identifier roofId = new Identifier(Spv_addon.MOD_ID, roofName);
                Optional<StructureTemplate> roofTemplate = StructurePlacementHelper.template(world, roofId);
                if (roofTemplate.isEmpty()) {
                    continue;
                }

                int px = chunkStartX + 8 * i;
                int pz = chunkStartZ + 8 * j;
                BlockPos roofPos = new BlockPos(px, 10, pz);

                StructurePlacementData roofData = new StructurePlacementData()
                        .setMirror(BlockMirror.NONE)
                        .setRotation(BlockRotation.NONE)
                        .setIgnoreEntities(true);

                roofTemplate.get().place(world, roofPos, roofPos, roofData, random, 16);
            }
        }
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
}
