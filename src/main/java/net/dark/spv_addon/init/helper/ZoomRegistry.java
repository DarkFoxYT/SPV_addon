package net.dark.spv_addon.init.helper;

import net.minecraft.block.Block;

import java.util.HashMap;
import java.util.Map;

public class ZoomRegistry {

    private static final Map<Block, ZoomData> ZOOM_MAP = new HashMap<>();

    public static void registerZoom(Block block, ZoomData data) {
        ZOOM_MAP.put(block, data);
    }

    public static ZoomData getZoomData(Block block) {
        return ZOOM_MAP.getOrDefault(block, new ZoomData(1f));
    }

    public static record ZoomData(float zoom) {}
}
