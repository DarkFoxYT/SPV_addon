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
     * @reason Add conditional shadow rotation based on world.
     * note : noon 85°, day 0°, sunrise 20°, midnight 70°
     */
    @Overwrite
    public static void rotateShadowModelView(Matrix4f shadowModelView, World world) {
        if (world.getRegistryKey().equals(com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY)) {
            shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(PoolroomsDayCycle.getSunAngle()));
        } else if (world.getRegistryKey().equals(BackroomsLevels.LEVEL188_WORLD_KEY)) {
            shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(30));
            shadowModelView.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(10));
        } else {
            shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(140));
        }
    }
}
