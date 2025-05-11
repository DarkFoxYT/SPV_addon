package net.dark.spv_addon.client;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.dark.spv_addon.battery.BatteryManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
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
                forceOff(player);
                continue;
            }

            UUID uuid = player.getUuid();
            PlayerComponent comp = InitializeComponents.PLAYER.get(player);
            boolean isOn = comp.isFlashLightOn();
            int battery = BatteryManager.getBattery(uuid);
            if (battery <= 0) {
                if (isOn) comp.setFlashLightOn(false);
                forceOff(player);
                continue;
            }

            if (isOn) {
                updateLight(player, tickDelta);
            } else {
                removeLights(player);
            }


            int sanity = net.dark.spv_addon.cca.InitializeComponents.SANITY.get(player).getSanityLevel();
            if (battery <= 10 && sanity <= 50) {
                if (client.world.getTime() % 20 < 10) {
                    removeLights(player); // flicker
                    return;
                }
            }
        }
    }

    private void updateLight(AbstractClientPlayerEntity player, float tickDelta) {
        UUID uuid = player.getUuid();
        Vec3d pos = player.getCameraPosVec(tickDelta);
        Quaternionf rot = new Quaternionf()
                .rotateXYZ((float) -Math.toRadians(player.getPitch(tickDelta)),
                        (float)  Math.toRadians(player.getYaw(tickDelta)), 0f);

        List<AreaLight> lights = lightMap.computeIfAbsent(uuid, id -> {
            AreaLight l1 = new AreaLight().setBrightness(1f).setDistance(25f)
                    .setSize(0, 0).setPosition(pos.x, pos.y, pos.z).setOrientation(rot);
            AreaLight l2 = new AreaLight().setBrightness(1f).setDistance(25f)
                    .setAngle(0.25f).setSize(0, 0).setPosition(pos.x, pos.y, pos.z).setOrientation(rot);
            return Arrays.asList(l1, l2);
        });

        for (AreaLight light : lights) {
            light.setPosition(pos.x, pos.y, pos.z);
            light.getOrientation().slerp(rot, 0.7f * client.getLastFrameDuration());
        }
    }

    private void forceOff(AbstractClientPlayerEntity player) {
        PlayerComponent comp = InitializeComponents.PLAYER.get(player);
        if (comp.isFlashLightOn()) {
            comp.setFlashLightOn(false);
        }
        removeLights(player);
    }

    private void removeLights(AbstractClientPlayerEntity player) {
        UUID uuid = player.getUuid();
        List<AreaLight> lights = lightMap.remove(uuid);
        if (lights != null) {
            for (AreaLight light : lights) {
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(light);
            }
        }
    }

    public void updateBattery(PlayerEntity player, int newBatteryValue) {
        UUID uuid = player.getUuid();
        int battery = BatteryManager.getBattery(uuid);
        if (battery != newBatteryValue) {
            System.out.println("Battery at " + battery + "% for " + player.getName().getString());
            player.sendMessage(Text.literal("Battery at " + battery + "%"), true);
        }
    }
}
