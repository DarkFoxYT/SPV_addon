package net.dark.spv_addon.world.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.dark.spv_addon.util.ServerTickScheduler;

public final class LevelTeleportOnLectern {
    private static final Identifier SOURCE_DIM_ID = new Identifier("spv_addon", "level188");
    private static final Identifier TARGET_DIM_ID = new Identifier("spv_addon", "level105");
    private static final double TARGET_X = 8.0;
    private static final double TARGET_Y = 100.0;
    private static final double TARGET_Z = 8.0;
    private static final float TARGET_YAW = 0.0f;
    private static final float TARGET_PITCH = 0.0f;

    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!(player instanceof ServerPlayerEntity spe)) return ActionResult.PASS;
            if (world.isClient) return ActionResult.PASS;

            // only in source level
            if (!world.getRegistryKey().getValue().equals(SOURCE_DIM_ID)) return ActionResult.PASS;

            // only lectern
            if (!world.getBlockState(hitResult.getBlockPos()).isOf(Blocks.LECTERN)) return ActionResult.PASS;

            // teleport
            ServerWorld targetWorld = spe.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, TARGET_DIM_ID));
            if (targetWorld == null) return ActionResult.PASS;

            com.sp.SPBRevamped.sendBlackScreenPacket(spe, 24, true, true);
            ServerTickScheduler.schedule(20, () -> {
                if (!spe.isRemoved()) {
                    spe.teleport(targetWorld, TARGET_X, TARGET_Y, TARGET_Z, TARGET_YAW, TARGET_PITCH);
                    spe.sendMessage(Text.literal("Transitioned."), true);
                }
            });

            return ActionResult.SUCCESS;
        });
    }
}
