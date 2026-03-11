package net.dark.spv_addon.items.custom;

import net.dark.spv_addon.Additions.battery.BatteryManager;
import net.dark.spv_addon.init.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BatteryItem extends Item {
    private static final int FLASHLIGHT_DISABLE_TICKS = 4 * 20;
    private static final String NBT_RECHARGE_TICKS = "RechargeTicks";
    private final int maxDurability;

    public BatteryItem(Settings settings, int maxDurability) {
        super(settings.maxDamage(maxDurability));
        this.maxDurability = maxDurability;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ItemStack stack = user.getStackInHand(hand);
            playBatterySound(world, user);

            // Start the battery changing process
            BatteryManager.startBatteryChanging(user);
            BatteryManager.setBattery(user, 0);
            user.sendMessage(Text.literal("Changing Battery"), true);

            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.putInt(NBT_RECHARGE_TICKS, FLASHLIGHT_DISABLE_TICKS);

            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            NbtCompound nbt = stack.getOrCreateNbt();
            if (nbt.contains(NBT_RECHARGE_TICKS)) {
                int ticks = nbt.getInt(NBT_RECHARGE_TICKS);
                if (ticks > 0) {
                    nbt.putInt(NBT_RECHARGE_TICKS, ticks - 1);
                } else {
                    int currentDurability = maxDurability - stack.getDamage();
                    int added = Math.min(currentDurability, 100);
                    int current = BatteryManager.getBattery(player);
                    BatteryManager.setBattery(player, Math.min(current + added, 100));
                    player.sendMessage(Text.literal("Battery Changed"), true);
                    nbt.remove(NBT_RECHARGE_TICKS);
                    stack.decrement(1);
                }
            }
        }
    }

    protected void playBatterySound(World world, PlayerEntity user) {
        user.playSound(ModSounds.ELEV, 1.0F, 1.0F);
    }
}
