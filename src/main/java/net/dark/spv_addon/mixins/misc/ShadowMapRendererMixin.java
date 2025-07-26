package net.dark.spv_addon.mixins.misc;

import com.sp.render.PoolroomsDayCycle;
import com.sp.render.ShadowMapRenderer;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ShadowMapRenderer.class)
public class ShadowMapRendererMixin {

    /**
     * @author DarkFox Studios
     * @reason Add conditional shadow rotation based on world type
     */
    @Overwrite
    public static void rotateShadowModelView(Matrix4f shadowModelView, World world) {
        // Check if we're in poolrooms world
        if (world.getRegistryKey().equals(com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY)) {
            // Use the original poolrooms day cycle rotation
            shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(PoolroomsDayCycle.getSunAngle()));
        }
        // Check if we're in level188 world
        else if (world.getRegistryKey().equals(BackroomsLevels.LEVEL188_WORLD_KEY)) {
            // Set fixed rotation to 30 degrees for level188
            shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(30));
        }
        // For other worlds, use default rotation
        else {
            shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(140));
        }
    }
}
