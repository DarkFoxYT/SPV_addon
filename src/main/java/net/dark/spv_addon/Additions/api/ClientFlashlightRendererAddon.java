package net.dark.spv_addon.Additions.api;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.dark.spv_addon.Additions.battery.BatteryManager;
import net.dark.spv_addon.init.CustomDamageSources;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
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
            int sanity = net.dark.spv_addon.cca.InitializeComponents.SANITY.get(player).getSanityLevel();
            int battery = BatteryManager.getBattery(player);
            if (battery <= 0) {
                if (isOn) comp.setFlashLightOn(false);
                forceOff(player);
                continue;
            }

            if (sanity <= 20) {
                if (client.world.getRandom().nextFloat() > 0.135f) {
                    if (!comp.isFlashLightOn()) comp.setFlashLightOn(true);
                    updateLight(player, tickDelta);
                } else {
                    for (int i = 0; i < 5; i++) {
                        boolean shouldBeOn = client.world.getRandom().nextBoolean();

                        comp.setFlashLightOn(shouldBeOn);
                        if (shouldBeOn) {
                            updateLight(player, tickDelta);
                        } else {
                            removeLights(player);
                        }
                    }
                }
                continue;
            }

            if (sanity <= 0) {
                if (!comp.shouldGlitch()) {
                    comp.setShouldGlitch(true);
                    comp.justChanged();
                    if (!comp.shouldInflictGlitchDamage) {
                        RegistryEntry<DamageType> entry = player.getWorld()
                                .getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .entryOf(CustomDamageSources.DISTORTION_DAMAGE_ID);
                    }
                }
            } else {
                if (comp.shouldGlitch()) {
                    comp.setShouldGlitch(false);
                    comp.justChanged();
                    comp.shouldInflictGlitchDamage = false;
                }
            }

            if (isOn) {
                updateLight(player, tickDelta);
            } else {
                removeLights(player);
            }
        }
    }

    private void updateLight(AbstractClientPlayerEntity player, float tickDelta) {
        UUID uuid = player.getUuid();
        Vec3d pos = player.getCameraPosVec(tickDelta);
        Quaternionf rot = new Quaternionf()
                .rotateXYZ((float) -Math.toRadians(player.getPitch(tickDelta)),
                        (float) Math.toRadians(player.getYaw(tickDelta)), 0f);

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
}
