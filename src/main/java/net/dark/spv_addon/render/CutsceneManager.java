package net.dark.spv_addon.render;

import com.sp.init.ModEntities;
import net.dark.spv_addon.entities.custom.DeathCamEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import foundry.veil.api.client.anim.Keyframe;
import foundry.veil.api.client.anim.Path;
import foundry.veil.api.client.util.Easings;
import com.sp.util.MathStuff;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class CutsceneManager {
    private final MinecraftClient client = MinecraftClient.getInstance();

    private boolean isDeathPlaying = false;
    private long deathStartTime;
    private static final int DEATH_DURATION = 2000;
    private Path deathCameraPath;
    private ItemEntity camera;
    private DeathCamEntity deathCamEntity;

    public CutsceneManager() {
        deathCameraPath = new Path(List.of(
                new Keyframe(Vec3d.ZERO, Vec3d.ZERO, Vec3d.ZERO, 0, Easings.Easing.linear),
                new Keyframe(new Vec3d(0, -2, 0), Vec3d.ZERO, Vec3d.ZERO,
                        MathStuff.millisecToTick(DEATH_DURATION), Easings.Easing.easeInSine)
        ), false, false);
    }

    public void startDeathCutscene() {
        PlayerEntity player = client.player;
        if (player == null) return;

        isDeathPlaying = true;
        deathStartTime = System.currentTimeMillis();

        client.options.hudHidden = true;
        client.getSoundManager().pauseAll();

        Vec3d eye = player.getCameraPosVec(1.0f);
        // spawn our GeoEntity at eye position so its bone animations run
        deathCamEntity = new DeathCamEntity(ModEntities.DEATH_CAM_ENTITY_TYPE, client.world);
        deathCamEntity.refreshPositionAndAngles(eye.x, eye.y, eye.z, player.getYaw(), player.getPitch());
        client.world.spawnEntity(deathCamEntity);

        // Use an ItemEntity as a dummy camera holder
        camera = new ItemEntity(client.world, eye.x, eye.y, eye.z, ItemStack.EMPTY);
        client.cameraEntity = camera;
    }

    public void tick() {
        if (!isDeathPlaying) return;

        float t = (System.currentTimeMillis() - deathStartTime) / (float)DEATH_DURATION;
        if (t < 1.0f) {
            // camera bone falls along our Path
            Vec3d offset = deathCameraPath.frameAtProgress(t).getPosition();
            camera.refreshPositionAndAngles(
                    deathCamEntity.getX(), deathCamEntity.getY() + offset.y, deathCamEntity.getZ(),
                    deathCamEntity.getYaw(), deathCamEntity.getPitch()
            );

        } else {
            // once fall finishes, we let the GeoEntity play its “eat” animation
            // (registered in DeathCamEntity.registerControllers)
            client.getSoundManager().resumeAll();
            client.options.hudHidden = false;
            client.setScreen(new DeathScreen(
                    Text.translatable("death.attack.generic", client.player.getName()),
                    false
            ));
            isDeathPlaying = false;
            deathCamEntity.remove(Entity.RemovalReason.DISCARDED);
            camera.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
