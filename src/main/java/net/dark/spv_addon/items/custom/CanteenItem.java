// File: net/dark/spv_addon/items/custom/CanteenItem.java

package net.dark.spv_addon.items.custom;

import net.dark.spv_addon.cca.InitializeComponents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.block.Blocks;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public class CanteenItem extends Item {

    public CanteenItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient) {
            NbtCompound nbt = stack.getOrCreateNbt();
            String state = nbt.getString("State");

            switch (state) {
                case "Contaminated":
                    player.damage(player.getDamageSources().magic(), 2.0F);
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 0));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0));
                    player.sendMessage(Text.literal("You drank contaminated water..."), true);
                    break;
                case "Purified":
                    InitializeComponents.THIRST.get(player).addThirst(50);
                    player.sendMessage(Text.literal("You drank purified water."), true);
                    break;
                case "SaltWater":
                    player.damage(player.getDamageSources().magic(), 1.0F);
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 300, 1));
                    player.sendMessage(Text.literal("You drank salty water..."), true);
                    break;
                case "AlmondWater":
                    InitializeComponents.THIRST.get(player).addThirst(75);
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1));
                    player.sendMessage(Text.literal("You drank almond water!"), true);
                    break;
                default:
                    player.sendMessage(Text.literal("The canteen is empty."), true);
                    break;
            }

            nbt.putString("State", "Empty");
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var world = context.getWorld();
        var pos = context.getBlockPos();
        var player = context.getPlayer();
        if (!world.isClient && player != null) {
            ItemStack stack = context.getStack();
            NbtCompound nbt = stack.getOrCreateNbt();
            var block = world.getBlockState(pos).getBlock();

            if (block == Blocks.WATER) {
                var biome = world.getBiome(pos).getKey().get().getValue().getPath();

                if (biome.contains("desert") || biome.contains("badlands")) {
                    nbt.putString("State", "SaltWater");
                } else if (biome.contains("swamp")) {
                    nbt.putString("State", "Contaminated");
                } else if (biome.contains("backrooms") || biome.contains("almond")) {
                    nbt.putString("State", "AlmondWater");
                } else {
                    nbt.putString("State", "Purified");
                }

                player.setStackInHand(context.getHand(), stack);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null) {
            String state = nbt.getString("State");
            if (!state.isEmpty()) {
                tooltip.add(Text.literal("Contents: " + state));
            } else {
                tooltip.add(Text.literal("Contents: Empty"));
            }
        }
    }
}
