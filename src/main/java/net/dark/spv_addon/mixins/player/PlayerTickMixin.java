package net.dark.spv_addon.mixins.player;

import com.sp.init.BackroomsLevels;
import net.dark.spv_addon.Additions.needs.SurvivalNeedsService;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.init.config.ServerConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerTickMixin {
    @Unique
    private static final TagKey<Block> SANITY_LIGHT_TAG =
            TagKey.of(Registries.BLOCK.getKey(), new Identifier("spv_addon", "sanity_lights"));
    @Unique
    private int tickCounter = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (SurvivalNeedsService.isCentralizedMode()) return;

        // Check if sanity system is enabled via server config
        if (!ServerConfig.isSanitySystemEnabled(player.getServer())) return;

        tickCounter++;
        if (tickCounter >= 140) { // 7 seconds at 20 TPS (140 ticks)
            tickCounter = 0;

            ServerWorld world = player.getServerWorld();
            BlockPos playerPos = player.getBlockPos();
            SanityComponent sanity = InitializeComponents.SANITY.get(player);


            boolean inPool = world.getRegistryKey().equals(BackroomsLevels.POOLROOMS_WORLD_KEY);
            boolean in207 = world.getRegistryKey().equals(net.dark.spv_addon.init.BackroomsLevels.LEVEL207_WORLD_KEY);
            boolean inField = world.getRegistryKey().equals(BackroomsLevels.INFINITE_FIELD_WORLD_KEY);
            boolean inKitty = world.getRegistryKey().equals(net.dark.spv_addon.init.BackroomsLevels.LEVEL_KITTY_WORLD_KEY);

            if (inPool || in207 || inField || inKitty) {
                return;
            }

            boolean nearSanityLight = false;
            int lightRange = 10; // Default sanity light range

            for (BlockPos pos : BlockPos.iterateOutwards(playerPos, lightRange, 5, lightRange)) {
                if (world.getBlockState(pos).isIn(SANITY_LIGHT_TAG)) {
                    if (pos.getSquaredDistance(playerPos) <= lightRange * lightRange) {
                        nearSanityLight = true;
                        break;
                    }
                }
            }


            for (BlockPos pos : BlockPos.iterateOutwards(playerPos, lightRange, lightRange, lightRange)) {
                BlockState state = world.getBlockState(pos);

                if (!state.isIn(SANITY_LIGHT_TAG)) continue;

                boolean active = true;

                if (state.getProperties().contains(BooleanProperty.of("red_light"))) {
                    active = state.get(BooleanProperty.of("red_light"));
                } else if (state.getProperties().contains(BooleanProperty.of("stopped"))) {
                    active = state.get(BooleanProperty.of("stopped"));
                }

                if (active && pos.getSquaredDistance(playerPos) <= lightRange * lightRange) {
                    nearSanityLight = true;
                    break;
                }
            }


            if (!nearSanityLight) {
                float drainRate = ServerConfig.getSanityDrainRate(player.getServer());
                sanity.decreaseSanity(Math.round(drainRate));
            }
        }
    }
}
