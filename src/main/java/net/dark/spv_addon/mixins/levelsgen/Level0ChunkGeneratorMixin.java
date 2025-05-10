// File: net/dark/spv_addon/mixins/levelsgen/Level0ChunkGeneratorMixin.java
package net.dark.spv_addon.mixins.levelsgen;

import com.sp.world.generation.Level0ChunkGenerator;
import net.dark.spv_addon.world.generation.Mega5RoomGenerator;
import net.dark.spv_addon.world.generation.level0.AddonLevel0MazeGenerator;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level0ChunkGenerator.class)
public class Level0ChunkGeneratorMixin {

    @Inject(method = "generate", at = @At("TAIL"))
    private void spv_addon$injectCustomRooms(StructureWorldAccess world, Chunk chunk, CallbackInfo ci) {
        int startX = chunk.getPos().x * 16;
        int startZ = chunk.getPos().z * 16;

        // 1. Custom Addon Maze (Level0)
        AddonLevel0MazeGenerator customMaze = new AddonLevel0MazeGenerator(world, "level0");
        customMaze.generate(startX, startZ, 8, 8);

        // 2. Custom MegaRoom
        Mega5RoomGenerator.placeMega5Room(world, chunk.getPos().x, chunk.getPos().z);
    }
}
