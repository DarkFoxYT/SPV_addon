// ModChunkGenerators.java
package net.dark.spv_addon.registry;

import net.dark.spv_addon.world.generation.Level5ChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ModChunkGenerators {
    public static final Identifier LEVEL5_ID = new Identifier("spv_addon", "level5_chunk_generator");

    public static void registerChunkGenerators() {
        Registry.register(Registries.CHUNK_GENERATOR, LEVEL5_ID, Level5ChunkGenerator.CODEC);
    }
}
