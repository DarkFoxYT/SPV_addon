package net.dark.spv_addon.init;

import net.dark.spv_addon.items.custom.BatteryItem;
import net.dark.spv_addon.items.custom.BatteryItem2;
import net.dark.spv_addon.items.custom.SanityRestoringItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item HOTEL_WALL = register("hotel_wall", new BlockItem(ModBlocks.HOTEL_WALL, new FabricItemSettings()));
    public static final Item HOTEL_FLOOR = register("hotel_floor", new BlockItem(ModBlocks.HOTEL_FLOOR, new FabricItemSettings()));
    public static final Item BED1 = register("bed1", new BlockItem(ModBlocks.BED1, new FabricItemSettings()));
    public static final Item BED2 = register("bed2", new BlockItem(ModBlocks.BED2, new FabricItemSettings()));
    public static final Item TABLE = register("table", new BlockItem(ModBlocks.TABLE, new FabricItemSettings()));
    public static final Item IKEA_SHELF = register("ikea_shelf", new BlockItem(ModBlocks.IKEA_SHELF, new FabricItemSettings()));
    public static final Item IKEA_SHELF1 = register("ikea_shelf1", new BlockItem(ModBlocks.IKEA_SHELF1, new FabricItemSettings()));
    public static final Item IKEA_SHELF2 = register("ikea_shelf2", new BlockItem(ModBlocks.IKEA_SHELF2, new FabricItemSettings()));
    public static final Item KITTY_1 = register("kitty_floor", new BlockItem(ModBlocks.KITTY_FLOOR, new FabricItemSettings()));
    public static final Item KITTY_2 = register("kitty_wall", new BlockItem(ModBlocks.KITTY_WALL, new FabricItemSettings()));
    public static final Item KITTY_PLUSH = register("kitty_plushie", new BlockItem(ModBlocks.KITTY_PLUSHIE, new FabricItemSettings()));
    public static final Item KITTY_PLUSH1 = register("kitty_plushie1", new BlockItem(ModBlocks.KITTY_PLUSHIE1, new FabricItemSettings()));
    public static final Item EXIT_SIGN = register("exit_sign", new BlockItem(ModBlocks.EXIT_SIGN, new FabricItemSettings()));
    public static final Item CROSS = register("cross", new BlockItem(ModBlocks.CROSS, new FabricItemSettings()));
    public static final Item GRASS1 = register("grass1", new BlockItem(ModBlocks.GRASS1, new FabricItemSettings()));


    public static final Item BATTERY_ITEM = new BatteryItem(new Item.Settings().maxCount(2));
    public static final Item BATTERY_ITEM2 = new BatteryItem2(new Item.Settings().maxCount(2));
    public static final Item CANTEEN = new SanityRestoringItem(new Item.Settings().maxCount(1), 100, 50);
    public static final Item ALMOND_BOTTLE = new SanityRestoringItem(new Item.Settings().maxCount(1), 25, 10);
    public static final Item ALMOND_DIRTY = new SanityRestoringItem(new Item.Settings().maxCount(1), 5, 2);


    public static void registerItems() {
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "battery_item"), BATTERY_ITEM);
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "battery_item2"), BATTERY_ITEM2);
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "canteen"), CANTEEN);
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "almond_dirty"), ALMOND_DIRTY);
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "almond_water"), ALMOND_BOTTLE);
    }
    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("spv_addon", name), item);
    }
}
