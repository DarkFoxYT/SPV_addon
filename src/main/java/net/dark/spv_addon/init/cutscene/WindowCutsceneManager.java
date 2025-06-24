package net.dark.spv_addon.init.cutscene;

import foundry.veil.api.client.anim.Keyframe;
import foundry.veil.api.client.anim.Path;
import foundry.veil.api.client.util.Easings.Easing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class WindowCutsceneManager {
    private long startTime;
    private final int duration = 2500;
    private Path cameraPathPos;
    private Path cameraPathRotX;
    private Path cameraPathRotY;
    private Path cameraPathRotZ;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private Entity camera = null;
    private Vec3d startPos;
    private Vec3d endPos;
    private Vec3d startRot;
    private Vec3d endRot;

    public void start() {
        if (client.player == null || client.world == null) return;
        this.startTime = System.currentTimeMillis();
        this.startPos = client.player.getPos();
        this.endPos = startPos.add(client.player.getRotationVec(1.0F).multiply(1)).add(0, -0.5, 0); // avance d'1 bloc devant et descend de 0.5 bloc
        this.startRot = new Vec3d(client.player.getPitch(), client.player.getYaw(), 0);
        this.endRot = new Vec3d(client.player.getPitch() - 30, client.player.getYaw(), 15); // regarde un peu plus vers le haut et roule de 15 degrés

        this.cameraPathPos = new Path(List.of(
                new Keyframe(startPos, Vec3d.ZERO, Vec3d.ZERO, 0, Easing.easeOutQuad),
                new Keyframe(endPos, Vec3d.ZERO, Vec3d.ZERO, duration, Easing.easeInQuad)
        ), false, false);

        this.cameraPathRotX = new Path(List.of(
                new Keyframe(Vec3d.ZERO, new Vec3d(startRot.x, 0, 0), Vec3d.ZERO, 0, Easing.easeOutQuad),
                new Keyframe(Vec3d.ZERO, new Vec3d(endRot.x, 0, 0), Vec3d.ZERO, duration, Easing.easeInQuad)
        ), false, false);

        this.cameraPathRotY = new Path(List.of(
                new Keyframe(Vec3d.ZERO, new Vec3d(0, startRot.y, 0), Vec3d.ZERO, 0, Easing.easeOutQuad),
                new Keyframe(Vec3d.ZERO, new Vec3d(0, endRot.y, 0), Vec3d.ZERO, duration, Easing.easeInQuad)
        ), false, false);

        this.cameraPathRotZ = new Path(List.of(
                new Keyframe(Vec3d.ZERO, new Vec3d(0, 0, 0), Vec3d.ZERO, 0, Easing.easeOutQuad),
                new Keyframe(Vec3d.ZERO, new Vec3d(0, 0, endRot.z), Vec3d.ZERO, duration, Easing.easeInQuad)
        ), false, false);

        if (this.camera != null) {
            this.camera.remove(RemovalReason.DISCARDED);
        }
        this.camera = new ItemEntity(client.world, startPos.x, startPos.y, startPos.z, ItemStack.EMPTY);
        this.camera.refreshPositionAndAngles(startPos.x, startPos.y, startPos.z, (float)startRot.y, (float)startRot.x);
    }

    public void tick() {
        if (client.player == null || client.world == null || this.camera == null) return;
        float timer = (float)(System.currentTimeMillis() - this.startTime) / (float)this.duration;
        timer = MathHelper.clamp(timer, 0.0F, 0.9999F);

        double interp = timer;
        Vec3d prevPos = cameraPathPos.frameAtProgress(0).getPosition();
        Vec3d nextPos = cameraPathPos.frameAtProgress(0.9999F).getPosition();
        Vec3d pos = new Vec3d(
                MathHelper.lerp(interp, prevPos.x, nextPos.x),
                MathHelper.lerp(interp, prevPos.y, nextPos.y),
                MathHelper.lerp(interp, prevPos.z, nextPos.z)
        );

        Vec3d prevRot = new Vec3d(
                cameraPathRotX.frameAtProgress(0).getRotation().x,
                cameraPathRotY.frameAtProgress(0).getRotation().y,
                cameraPathRotZ.frameAtProgress(0).getRotation().z
        );
        Vec3d nextRot = new Vec3d(
                cameraPathRotX.frameAtProgress(0.9999F).getRotation().x,
                cameraPathRotY.frameAtProgress(0.9999F).getRotation().y,
                cameraPathRotZ.frameAtProgress(0.9999F).getRotation().z
        );
        Vec3d rot = new Vec3d(
                MathHelper.lerp(interp, prevRot.x, nextRot.x),
                MathHelper.lerp(interp, prevRot.y, nextRot.y),
                MathHelper.lerp(interp, prevRot.z, nextRot.z)
        );

        this.camera.refreshPositionAndAngles(pos.x, pos.y, pos.z, (float)rot.y, (float)rot.x);
        this.client.cameraEntity = this.camera;
    }

    public void stop() {
        if (this.camera != null) {
            this.camera.remove(RemovalReason.DISCARDED);
            this.camera = null;
        }
        if (client.player != null) {
            client.cameraEntity = client.player;
        }
    }

    public boolean isFinished() {
        float timer = (float)(System.currentTimeMillis() - this.startTime) / (float)this.duration;
        return timer >= 1.0F;
    }
}