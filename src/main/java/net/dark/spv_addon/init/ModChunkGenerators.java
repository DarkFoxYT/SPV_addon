// File: net.dark.spv_addon.init.ModChunkGenerators.java

package net.dark.spv_addon.init;

import net.dark.spv_addon.world.generation.Level5ChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ModChunkGenerators {
    public static void register() {
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier("spv_addon", "level5_chunk_generator"), Level5ChunkGenerator.CODEC);
    }
}
