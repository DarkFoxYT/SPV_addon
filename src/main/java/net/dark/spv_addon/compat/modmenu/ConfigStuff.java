package net.dark.spv_addon.compat.modmenu;

import eu.midnightdust.lib.config.MidnightConfig;
import static net.dark.spv_addon.Spv_addon.MOD_ID;

public class ConfigStuff extends MidnightConfig {
    // categories
    private static final String HUD     = "hud";
    private static final String THIRST  = "thirst";
    private static final String SANITY  = "sanity";
    private static final String LEVEL   = "level";

    //
    // HUD CONFIGURATION
    //

    @Entry(category = HUD,
            name = "Enable Battery HUD")
    public static boolean batteryHudEnabled = true;
    @Entry(category = HUD,
            name = "Battery X Position", min = 0, max = 2000)
    public static int batteryHudPosX = 5;
    @Entry(category = HUD,
            name = "Battery Y Position", min = 0, max = 2000)
    public static int batteryHudPosY = 5;
    @Entry(category = HUD,
            name = "Battery Scale", isSlider = true, min = 0.1, max = 5, precision = 2)
    public static float batteryHudScale = 0.25f;

    @Comment(category = HUD)
    public static Comment spacer1;

    @Entry(category = HUD,
            name = "Enable Thirst HUD")
    public static boolean thirstHudEnabled = true;
    @Entry(category = HUD,
            name = "Thirst X Position", min = 0, max = 2000)
    public static int thirstHudPosX = 5;
    @Entry(category = HUD,
            name = "Thirst Y Position", min = 0, max = 2000)
    public static int thirstHudPosY = 35;
    @Entry(category = HUD,
            name = "Thirst Scale", isSlider = true, min = 0.1, max = 5, precision = 2)
    public static float thirstHudScale = 0.25f;

    @Comment(category = HUD)
    public static Comment spacer2;

    @Entry(category = HUD,
            name = "Enable Sanity HUD")
    public static boolean sanityHudEnabled = true;
    @Entry(category = HUD,
            name = "Sanity X Position", min = 0, max = 2000)
    public static int sanityHudPosX = 5;
    @Entry(category = HUD,
            name = "Sanity Y Position", min = 0, max = 2000)
    public static int sanityHudPosY = 65;
    @Entry(category = HUD,
            name = "Sanity Scale", isSlider = true, min = 0.1, max = 5, precision = 2)
    public static float sanityHudScale = 0.25f;

    //
    // THIRST SYSTEM CONFIGURATION
    //

    @Entry(category = THIRST,
            name = "Thirst System Enabled")
    public static boolean thirstEnabled = true;
    @Entry(category = THIRST,
            name = "Thirst Tick Interval (sec)", isSlider = true, min = 1, max = 60)
    public static int thirstTickInterval = 10;
    @Entry(category = THIRST,
            name = "Drain per Walk Tick", isSlider = true, min = 0, max = 10)
    public static int thirstDrainWalk = 1;
    @Entry(category = THIRST,
            name = "Drain per Sprint Tick", isSlider = true, min = 0, max = 10)
    public static int thirstDrainSprint = 2;
    @Entry(category = THIRST,
            name = "Low-Thirst Threshold (%)", isSlider = true, min = 0, max = 100)
    public static int thirstLowThreshold = 25;
    @Entry(category = THIRST,
            name = "Apply Effects at Low Thirst")
    public static boolean thirstEffectsEnabled = true;

    //
    // SANITY SYSTEM CONFIGURATION
    //

    @Entry(category = SANITY,
            name = "Sanity System Enabled")
    public static boolean sanityEnabled = true;
    @Entry(category = SANITY,
            name = "Darkness Drain Rate (%) per Tick", isSlider = true, min = 0, max = 100)
    public static int sanityDarknessDrain = 1;
    @Entry(category = SANITY,
            name = "Extra Drain when Thirst Low (%)", isSlider = true, min = 0, max = 100)
    public static int sanityThirstPenalty = 1;
    @Entry(category = SANITY,
            name = "Extra Drain when Entity Aggro (%)", isSlider = true, min = 0, max = 100)
    public static int sanityEntityAggroPenalty = 5;
    @Entry(category = SANITY,
            name = "Extra Drain when Battery < 10% (%)", isSlider = true, min = 0, max = 100)
    public static int sanityBatteryPenalty = 2;
    @Entry(category = SANITY,
            name = "Sanity Regen Enabled")
    public static boolean sanityRegenEnabled = true;
    @Entry(category = SANITY,
            name = "Sanity Regen Radius (blocks)", min = 0, max = 100)
    public static int sanityRegenRadius = 10;
    @Entry(category = SANITY,
            name = "Sanity Regen per Player (%)", isSlider = true, min = 0, max = 100)
    public static int sanityRegenPerPlayer = 5;

    //
    // LEVEL‐/WORLD SETTINGS
    //

    @Entry(category = LEVEL,
            name = "Use Skylight for Darkness Check")
    public static boolean useSkylightForDarkness = true;
    @Entry(category = LEVEL,
            name = "Darkness Threshold (0–15 light level)", min = 0, max = 15)
    public static int darknessThreshold = 7;
    @Entry(category = LEVEL,
            name = "Spawn Vision-Entity at Sanity ≤", isSlider = true, min = 0, max = 100)
    public static int visionSanityThreshold = 0;

}
