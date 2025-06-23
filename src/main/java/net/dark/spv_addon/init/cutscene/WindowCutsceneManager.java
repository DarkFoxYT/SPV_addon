package net.dark.spv_addon.init.cutscene;

import foundry.veil.api.client.anim.Frame;
import foundry.veil.api.client.anim.Keyframe;
import foundry.veil.api.client.anim.Path;
import foundry.veil.api.client.util.Easings.Easing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class WindowCutsceneManager {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private Entity camera;
    private final Path cameraPathPos;
    private final Path cameraPathRot;
    private long startTime;
    private final int duration;
    public float cameraRotZ = 0.0F;

    public WindowCutsceneManager(Path cameraPathPos, Path cameraPathRot, int durationMs) {
        this.cameraPathPos = cameraPathPos;
        this.cameraPathRot = cameraPathRot;
        this.duration = durationMs;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.initCamera();
    }

    public void tick() {
        float timer = (float)(System.currentTimeMillis() - this.startTime) / (float)this.duration;
        if (timer > 1.0F) timer = 1.0F;

        Vec3d pos = lerpedCameraPos(timer);
        Vec3d rot = lerpedCameraRot(timer);

        this.camera.refreshPositionAndAngles(pos.x, pos.y, pos.z, (float)rot.y, (float)rot.x);
        this.cameraRotZ = (float)rot.z;
        this.client.cameraEntity = this.camera;
    }

    private void initCamera() {
        this.camera = new ItemEntity(this.client.world, 0, 0, 0, ItemStack.EMPTY);
        this.camera.refreshPositionAndAngles(0, 0, 0, 0, 0);
    }

    private Vec3d lerpedCameraPos(float timer) {
        Frame frame = this.cameraPathPos.frameAtProgress(timer);
        return frame.getPosition();
    }

    private Vec3d lerpedCameraRot(float timer) {
        Frame frame = this.cameraPathRot.frameAtProgress(timer);
        return frame.getRotation();
    }

    public static WindowCutsceneManager createExample() {
        List<Keyframe> posFrames = List.of(
                new Keyframe(new Vec3d(0, 2, -8), Vec3d.ZERO, Vec3d.ZERO, 40, Easing.easeInOutSine),
                new Keyframe(new Vec3d(0.5, 2.5, -4), Vec3d.ZERO, Vec3d.ZERO, 40, Easing.easeInOutSine),
                new Keyframe(new Vec3d(0, 2, 0), Vec3d.ZERO, Vec3d.ZERO, 0, Easing.easeInOutSine)
        );
        Path cameraPathPos = new Path((List<Frame>)(List<?>)posFrames, false, false);

        List<Keyframe> rotFrames = List.of(
                new Keyframe(Vec3d.ZERO, new Vec3d(-45, 30, 0), Vec3d.ZERO, 40, Easing.easeInOutSine),
                new Keyframe(Vec3d.ZERO, new Vec3d(-20, 15, 0), Vec3d.ZERO, 40, Easing.easeInOutSine),
                new Keyframe(Vec3d.ZERO, new Vec3d(0, 0, 0), Vec3d.ZERO, 0, Easing.easeInOutSine)
        );
        Path cameraPathRot = new Path((List<Frame>)(List<?>)rotFrames, false, false);

        return new WindowCutsceneManager(cameraPathPos, cameraPathRot, 4000); // 4 secondes
    }

    //keep this for future uses
    //WindowCutsceneManager cutscene = WindowCutsceneManager.createExample();
    //cutscene.start();
}