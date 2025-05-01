package net.dark.spv_addon.mixins.levelsgen;

import com.sp.world.generation.maze_generator.Level0MazeGenerator;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(Level0MazeGenerator.class)
public class Level0MazeGeneratorMixin {

    /**
     * Patch to safely handle missing starting cell in Level0MazeGenerator.setup()
     * Prevents crashes when no valid maze cell is found.
     */
    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lcom/sp/world/generation/maze_generator/LowVarCell;setVisited(Z)V"), cancellable = true)
    private void spv_addon$preventCrashIfNoCurrentCell(StructureWorldAccess world, CallbackInfo ci) {
        Level0MazeGenerator self = (Level0MazeGenerator) (Object) this;

        try {
            Field currentCellField = Level0MazeGenerator.class.getDeclaredField("currentCell");
            currentCellField.setAccessible(true);
            Object currentCell = currentCellField.get(self);

            if (currentCell == null) {
                System.out.println("[SPV Addon] [Patch] Warning: No starting maze cell found. Skipping maze setup safely.");
                ci.cancel();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("[SPV Addon] [Patch] Critical error accessing Level0MazeGenerator.currentCell: " + e);
        }

    }
}
