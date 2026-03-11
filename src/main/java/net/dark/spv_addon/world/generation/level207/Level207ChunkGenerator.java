package net.dark.spv_addon.world.generation.level207;

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

public final class Level207ChunkGenerator extends TemplateBackroomsChunkGenerator {
    public static final Codec<Level207ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, Level207ChunkGenerator::new)
    );

    public Level207ChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
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
        Random random = StructurePlacementHelper.chunkRandom(cx, cz, 0x32_30_37L);

        if (cx % 2 == 0 && cz % 2 == 0) {
            int rx = cx / 2;
            int rz = cz / 2;

            Identifier roomId;
            if (rx == 0 && rz == 0) {
                roomId = new Identifier(Spv_addon.MOD_ID, "level207/entrance");
            } else {
                int variant = 1 + random.nextInt(3); // 1-3 inclus
                roomId = new Identifier(Spv_addon.MOD_ID, "level207/grave" + variant);
            }

            Optional<StructureTemplate> optTpl = StructurePlacementHelper.template(world, roomId);
            if (optTpl.isEmpty()) return;

            StructureTemplate template = optTpl.get();

            int limeYOffset = StructurePlacementHelper.markerYOffset(roomId, template, Blocks.LIME_WOOL);

            int bx = chunk.getPos().getStartX();
            int bz = chunk.getPos().getStartZ();
            int baseY = 20 - limeYOffset;

            BlockPos.Mutable basePos = new BlockPos.Mutable(bx, baseY, bz);
            StructurePlacementData placeData = new StructurePlacementData()
                    .setMirror(BlockMirror.NONE)
                    .setRotation(BlockRotation.NONE)
                    .setIgnoreEntities(true);

            template.place(world, basePos, basePos, placeData, random, 2);
        }
    }

}
