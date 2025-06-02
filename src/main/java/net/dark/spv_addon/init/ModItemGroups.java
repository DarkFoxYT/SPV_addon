package net.dark.spv_addon.init;

import net.dark.spv_addon.Spv_addon;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup SPV_ADDON = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(Spv_addon.MOD_ID, "spv_addon"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.spv_addon"))
                    .icon(() -> new ItemStack(ModBlocks.HOTEL_FLOOR))
                    .entries((ctx, entries) -> {
                        // Blocks
                        entries.add(ModBlocks.HOTEL_FLOOR);
                        entries.add(ModBlocks.HOTEL_WALL);
                        entries.add(ModBlocks.BED1);
                        entries.add(ModBlocks.BED2);
                        entries.add(ModBlocks.TABLE);
                        entries.add(ModBlocks.IKEA_SHELF);
                        entries.add(ModBlocks.IKEA_SHELF1);
                        entries.add(ModBlocks.IKEA_SHELF2);
                        entries.add(ModBlocks.KITTY_WALL);
                        entries.add(ModBlocks.KITTY_FLOOR);
                        entries.add(ModBlocks.KITTY_PLUSHIE);
                        entries.add(ModBlocks.EXIT_SIGN);
                        entries.add(ModBlocks.KITTY_PLUSHIE1);
                        entries.add(ModBlocks.GRASS1);
                        entries.add(ModBlocks.CROSS);
                        // Items
                        entries.add(ModItems.BATTERY_ITEM);
                        entries.add(ModItems.BATTERY_ITEM2);
                        entries.add(ModItems.ALMOND_BOTTLE);
                        entries.add(ModItems.ALMOND_DIRTY);
                        entries.add(ModItems.CANTEEN);
                    })
                    .build()
    );

    public static void registerItemGroups() {
        Spv_addon.LOGGER.info("Registered item group: {}", SPV_ADDON.getType());
    }
}
