package net.dark.spv_addon.api;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class SanityHandler {
    public static void handleSanity(ServerPlayerEntity player, int sanity) {
        if (sanity == 20) {
            // Simuler le clignotement : retirer et redonner la lampe torche à intervalles réguliers
            if (player.age % 20 < 10) { // 0.5s on, 0.5s off
                // Éteindre la lampe torche (ex : retirer l'item de la main)
                if (player.getMainHandStack().getItem() == Items.TORCH) {
                    player.setStackInHand(player.getActiveHand(), ItemStack.EMPTY);
                }
            } else {
                // Rendre la lampe torche si elle n'est pas là
                if (player.getMainHandStack().isEmpty()) {
                    player.setStackInHand(player.getActiveHand(), new ItemStack(Items.TORCH));
                }
            }
        }
    }
}