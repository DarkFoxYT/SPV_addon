package net.dark.spv_addon.Additions.Sanity;

import foundry.veil.api.client.render.deferred.light.PointLight;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SanityLightStore {
    private static final List<PointLight> POINT_LIGHTS = new CopyOnWriteArrayList<>();

    public static void add(PointLight light) {
        POINT_LIGHTS.add(light);
    }

    public static void remove(PointLight light) {
        POINT_LIGHTS.remove(light);
    }

    public static List<PointLight> getActiveLights() {
        return Collections.unmodifiableList(POINT_LIGHTS);
    }
}
