package net.dark.spv_addon;

import net.dark.spv_addon.items.ModItems;
import net.dark.spv_addon.util.FlashlightBatteryEvents;
import net.dark.spv_addon.commands.FlashlightBatteryCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Spv_addon implements ModInitializer {

    @Override
    public void onInitialize() {
        ModItems.registerItems();
        FlashlightBatteryEvents.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FlashlightBatteryCommand.register(dispatcher);
        });
    }
}
