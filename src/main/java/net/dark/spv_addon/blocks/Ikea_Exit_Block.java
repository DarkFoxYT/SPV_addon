package net.dark.spv_addon.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Ikea_Exit_Block extends Block {

    private final RegistryKey<World> requiredLevel;
    private final RegistryKey<World> destinationLevel;

    public Ikea_Exit_Block(Settings settings, RegistryKey<World> requiredLevel, RegistryKey<World> destinationLevel) {
        super(settings);
        this.requiredLevel = requiredLevel;
        this.destinationLevel = destinationLevel;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof ServerPlayerEntity player) {
            // Vérifie qu'on est dans le bon niveau
            if (world.getRegistryKey().equals(requiredLevel)) {
                ServerWorld destWorld = player.getServer().getWorld(destinationLevel);
                if (destWorld != null) {
                    BlockPos spawn = destWorld.getSpawnPos();
                    player.moveToWorld(destWorld);
                    player.teleport(destWorld, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());
                    player.sendMessage(net.minecraft.text.Text.literal("§eYou escaped IKEA… for now."), false);
                } else {
                    player.sendMessage(net.minecraft.text.Text.literal("§cDestination world not found!"), false);
                }
            } else {
                // Optionnel : message si le joueur essaie depuis le mauvais niveau
                player.sendMessage(net.minecraft.text.Text.literal("§cYou can't escape from here."), false);
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }
}
