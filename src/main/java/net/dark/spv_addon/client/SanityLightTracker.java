// net.dark.spv_addon.client.SanityLightTracker.java

package net.dark.spv_addon.client;

import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3dc;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SanityLightTracker {
    private static final Set<PointLight> pointLights = ConcurrentHashMap.newKeySet();

    public static void registerPointLight(PointLight light) {
        pointLights.add(light);
    }

    public static void unregisterPointLight(PointLight light) {
        pointLights.remove(light);
    }

    public static boolean isNearAnyLight(Vec3d pos, double radius) {
        return pointLights.stream().anyMatch(light ->
                light.getPosition().distance((Vector3dc) pos) <= radius
        );
    }

    public static Set<PointLight> getLights() {
        return Collections.unmodifiableSet(pointLights);
    }
}
