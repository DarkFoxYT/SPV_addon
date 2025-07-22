package net.dark.spv_addon.items;

import net.dark.spv_addon.client.gui.cosmetics.CosmeticsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CosmeticMirrorItem extends Item {
    
    public CosmeticMirrorItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        
        if (world.isClient) {
            // Open cosmetics screen on client side
            MinecraftClient.getInstance().setScreen(new CosmeticsScreen());
            user.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.5f, 1.0f);
        }
        
        return TypedActionResult.success(itemStack, world.isClient);
    }
    
    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable("item.spv_addon.cosmetic_mirror");
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // Give it a magical glint
    }
}
