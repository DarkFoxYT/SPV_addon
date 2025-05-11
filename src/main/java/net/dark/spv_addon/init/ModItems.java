package net.dark.spv_addon.init;

import net.dark.spv_addon.items.custom.BatteryItem;
import net.dark.spv_addon.items.custom.BatteryItem2;
import net.dark.spv_addon.items.custom.DrinkableThirstItem;
import net.dark.spv_addon.items.custom.SanityRestoringItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item BATTERY_ITEM = new BatteryItem(new Item.Settings().maxCount(16));
    public static final Item BATTERY_ITEM2 = new BatteryItem2(new Item.Settings().maxCount(16));
    public static final Item HOTEL_WALL = register("hotel_wall", new BlockItem(ModBlocks.HOTEL_WALL, new FabricItemSettings()));
    public static final Item HOTEL_FLOOR = register("hotel_floor", new BlockItem(ModBlocks.HOTEL_FLOOR, new FabricItemSettings()));
    public static final Item TESTS = register("tests", new BlockItem(ModBlocks.TESTS, new FabricItemSettings()));
    public static final Item BED1 = register("bed1", new BlockItem(ModBlocks.BED1, new FabricItemSettings()));
    public static final Item BED2 = register("bed2", new BlockItem(ModBlocks.BED2, new FabricItemSettings()));
    public static final Item TABLE = register("table", new BlockItem(ModBlocks.TABLE, new FabricItemSettings()));

    public static final Item CANTEEN = new SanityRestoringItem(new Item.Settings().maxCount(1), 100);
    public static final Item ALMOND_BOTTLE = new SanityRestoringItem(new Item.Settings().maxCount(1), 25);


    public static void registerItems() {
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "battery_item"), BATTERY_ITEM);
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "battery_item2"), BATTERY_ITEM2);
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "canteen"), CANTEEN);
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "almond_water"), ALMOND_BOTTLE);
    }
    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("spv_addon", name), item);
    }
}
