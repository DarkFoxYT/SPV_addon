package net.dark.spv_addon.blocks;

import com.sp.init.BackroomsLevels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Ikea_Exit_Block extends Block {

    public Ikea_Exit_Block(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof ServerPlayerEntity player) {
            // TP le joueur vers un autre level (par ex. Level 0)
            ServerWorld overworld = player.getServer().getWorld(com.sp.init.BackroomsLevels.LEVEL0_WORLD_KEY);
            if (overworld != null) {
                BlockPos spawn = overworld.getSpawnPos();
                player.moveToWorld(overworld);
                player.teleport(overworld, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());
                player.sendMessage(net.minecraft.text.Text.literal("§eYou escaped IKEA… for now."), false);
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }
}
