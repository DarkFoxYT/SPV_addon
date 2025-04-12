package net.dark.spv_addon.client;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.dark.spv_addon.battery.BatteryManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.util.*;

public class ClientFlashlightRendererAddon {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final Map<UUID, List<AreaLight>> lightMap = new HashMap<>();

    public void tick(float tickDelta) {
        if (client.world == null) return;

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if (player.isSpectator()) {
                removeLights(player);
                continue;
            }

            UUID uuid = player.getUuid();
            PlayerComponent comp = InitializeComponents.PLAYER.get(player);
            int battery = BatteryManager.getBattery(uuid);
            boolean lightOn = comp.isFlashLightOn();

            if (lightOn && battery <= 0) {
                comp.setFlashLightOn(false);
                removeLights(player);
                System.out.println("Battery 0%. Flashlight auto-disabled for " + player.getName().getString());
                continue;
            }

            if (lightOn && battery > 0) {
                updateLight(player, tickDelta);
                System.out.println("Battery " + battery + "% for " + player.getName().getString());
            } else {
                removeLights(player);
            }
        }
    }

    private void updateLight(AbstractClientPlayerEntity player, float tickDelta) {
        UUID uuid = player.getUuid();
        Vec3d pos = player.getCameraPosVec(tickDelta);
        Quaternionf rot = new Quaternionf().rotateXYZ(
                (float) -Math.toRadians(player.getPitch(tickDelta)),
                (float) Math.toRadians(player.getYaw(tickDelta)),
                0f
        );

        if (!hasLight(uuid)) {
            AreaLight l1 = new AreaLight()
                    .setBrightness(1f)
                    .setDistance(25f)
                    .setSize(0, 0)
                    .setPosition(pos.x, pos.y, pos.z)
                    .setOrientation(rot);

            AreaLight l2 = new AreaLight()
                    .setBrightness(1f)
                    .setDistance(25f)
                    .setAngle(0.25f)
                    .setSize(0, 0)
                    .setPosition(pos.x, pos.y, pos.z)
                    .setOrientation(rot);

            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(l1);
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(l2);

            lightMap.put(uuid, Arrays.asList(l1, l2));
        } else {
            for (AreaLight light : lightMap.get(uuid)) {
                light.setPosition(pos.x, pos.y, pos.z);
                light.getOrientation().slerp(rot, 0.7f * client.getLastFrameDuration());
            }
        }
    }

    private boolean hasLight(UUID uuid) {
        List<AreaLight> lights = lightMap.get(uuid);
        return lights != null && !lights.isEmpty();
    }

    private void removeLights(AbstractClientPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (hasLight(uuid)) {
            for (AreaLight light : lightMap.get(uuid)) {
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(light);
            }
            lightMap.remove(uuid);
        }
    }
}
