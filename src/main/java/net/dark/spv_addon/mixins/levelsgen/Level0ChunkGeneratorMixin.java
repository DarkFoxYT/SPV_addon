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

    /**
     * Injects extra generation at the end of generateMaze().
     */
    @Inject(method = "generateMaze", at = @At("TAIL"))
    private void afterGenerateMaze(StructureWorldAccess world, Chunk chunk, CallbackInfo ci) {
        AddonLevel0MazeGenerator addonMaze = new AddonLevel0MazeGenerator(world, "level0");

        int startX = chunk.getPos().x * 16;
        int startZ = chunk.getPos().z * 16;
        int sizeX = 8; // 8*5 = 40 blocks width
        int sizeZ = 8; // 8*5 = 40 blocks depth

        addonMaze.generate(startX, startZ, sizeX, sizeZ);

        Mega5RoomGenerator.placeMega5Room(world, chunk.getPos().x, chunk.getPos().z);
    }
}
