package net.dark.spv_addon.items;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.items.custom.BatteryItem;
import net.dark.spv_addon.items.custom.BatteryItem2;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item BATTERY_ITEM = new BatteryItem(new Item.Settings().maxCount(16));
    public static final Item BATTERY_ITEM2 = new BatteryItem2(new Item.Settings().maxCount(16));
    public static final Item HOTEL_WALL = register("hotel_wall", new BlockItem(ModBlocks.HOTEL_WALL, new FabricItemSettings()));
    public static final Item HOTEL_FLOOR = register("hotel_floor", new BlockItem(ModBlocks.HOTEL_FLOOR, new FabricItemSettings()));

    public static void registerItems() {
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "battery_item"), BATTERY_ITEM);
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "battery_item2"), BATTERY_ITEM2);
    }
    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("spv_addon", name), item);
    }
}