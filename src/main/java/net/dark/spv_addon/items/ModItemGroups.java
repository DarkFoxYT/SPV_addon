package net.dark.spv_addon.items;

import com.sp.SPBRevamped;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.items.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup BACKROOMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Spv_addon.MOD_ID, "spv_addon"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.spv_addon"))
                    .icon(() -> new ItemStack(ModBlocks.HOTEL_FLOOR)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.HOTEL_FLOOR);
                        entries.add(ModBlocks.HOTEL_WALL);
                        entries.add(ModItems.BATTERY_ITEM);
                        entries.add(ModItems.BATTERY_ITEM2);
                        entries.add(ModItems.HOTEL_WALL);
                        entries.add(ModItems.HOTEL_FLOOR);





                    }).build());




    public static void registerItemGroups() {
        Spv_addon.LOGGER.info("Registering Item Groups");
    }
}
