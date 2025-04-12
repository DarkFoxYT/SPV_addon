package net.dark.spv_addon.items;

import net.dark.spv_addon.items.custom.BatteryItem;
import net.dark.spv_addon.items.custom.BatteryItem2;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item BATTERY_ITEM = new BatteryItem(new Item.Settings().maxCount(16));
    public static final Item BATTERY_ITEM2 = new BatteryItem2(new Item.Settings().maxCount(16));

    public static void registerItems() {
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "battery_item"), BATTERY_ITEM);
        Registry.register(Registries.ITEM, new Identifier("spv_addon", "battery_item2"), BATTERY_ITEM2);
    }
}