package net.dark.spv_addon.entities.ik.util;

import java.util.logging.Logger;

public class PrAnCommonClass {
    public static Logger LOGGER = Logger.getLogger("PrAn");
    public static boolean isDev = true;
    public static boolean shouldRenderDebugLegs = false;

    public static void throwInDevOnly(RuntimeException exception) {
        if (isDev) {
            throw exception;
        }
    }

    public static void init() {
    }
}
