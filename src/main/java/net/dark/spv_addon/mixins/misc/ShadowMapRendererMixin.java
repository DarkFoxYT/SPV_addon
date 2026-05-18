package net.dark.spv_addon.mixins.misc;

import com.sp.render.PoolroomsDayCycle;
import com.sp.render.ShadowMapRenderer;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ShadowMapRenderer.class)
public class ShadowMapRendererMixin {
    private static final float SHADOW_LOCK_PERIOD = 2.0F;
    private static final float SHADOW_HORIZONTAL_SNAP = 1.0F;

    /**
     * @author DarkFox Studios
     * @reason Stabilize custom Backrooms shadow projection so camera bob and sub-block movement do not make shadows swim.
     */
    @Overwrite(remap = false)
    public static MatrixStack createShadowModelView(double x, double y, double z, World world, boolean lockToCamera) {
        MatrixStack matrices = createBaseShadowModelView();
        rotateShadowModelView(matrices.peek().getPositionMatrix(), world);
        if (lockToCamera) {
            applyStableCameraLock(matrices.peek().getPositionMatrix(), x, y, z);
        }
        return matrices;
    }

    /**
     * @author DarkFox Studios
     * @reason Keep the vanilla SPB overload stable for any renderer path that does not pass a world.
     */
    @Overwrite(remap = false)
    public static MatrixStack createShadowModelView(double x, double y, double z, boolean lockToCamera) {
        MatrixStack matrices = createBaseShadowModelView();
        rotateShadowModelView(matrices.peek().getPositionMatrix());
        if (lockToCamera) {
            applyStableCameraLock(matrices.peek().getPositionMatrix(), x, y, z);
        }
        return matrices;
    }

    /**
     * @author DarkFox Studios
     * @reason Add conditional shadow rotation based on world.
     */
    @Overwrite(remap = false)
    public static void rotateShadowModelView(Matrix4f shadowModelView) {
        shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(90));
    }

    /**
     * @author DarkFox Studios
     * @reason Add conditional shadow rotation based on world.
     */
    @Overwrite(remap = false)
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

    private static MatrixStack createBaseShadowModelView() {
        MatrixStack matrices = new MatrixStack();
        matrices.peek().getNormalMatrix().identity();
        matrices.peek().getPositionMatrix().identity();
        matrices.peek().getPositionMatrix().translate(0.0F, 0.0F, -100.0F);
        return matrices;
    }

    private static void applyStableCameraLock(Matrix4f shadowModelView, double x, double y, double z) {
        shadowModelView.translate(
                stableHorizontalOffset(x),
                stableVerticalOffset(y),
                stableHorizontalOffset(z)
        );
    }

    private static float stableHorizontalOffset(double value) {
        double snapped = Math.floor(value / SHADOW_HORIZONTAL_SNAP) * SHADOW_HORIZONTAL_SNAP;
        return centeredPositiveModulo(snapped, SHADOW_LOCK_PERIOD);
    }

    private static float stableVerticalOffset(double value) {
        return centeredPositiveModulo(Math.floor(value), SHADOW_LOCK_PERIOD);
    }

    private static float centeredPositiveModulo(double value, float period) {
        double mod = value % period;
        if (mod < 0.0D) {
            mod += period;
        }
        return (float) mod - period * 0.5F;
    }
}
