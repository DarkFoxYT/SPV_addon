package net.dark.spv_addon.particle;

import net.dark.spv_addon.init.ModParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class WindowDripSpawner {

    // Appelle ceci dans ta méthode d'init client
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) return;
            if (!client.world.isRaining()) return;

            BlockPos playerPos = client.player.getBlockPos();
            for (int dx = -6; dx <= 6; dx++) {
                for (int dz = -6; dz <= 6; dz++) {
                    BlockPos pos = playerPos.add(dx, 0, dz);
                    BlockPos top = client.world.getTopPosition(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, pos);
                    BlockPos glassPos = top.down(1);

                    if (client.world.getBlockState(glassPos).isOf(Blocks.GLASS)) {
                        // Vérifie les 4 côtés horizontaux
                        for (Direction dir : Direction.Type.HORIZONTAL) {
                            BlockPos sidePos = glassPos.offset(dir);
                            if (client.world.getBlockState(sidePos).isAir() &&
                                    client.world.isSkyVisible(glassPos.up())) {

                                // Spawn sur la face exposée, avec un offset pour coller à la vitre
                                double px = glassPos.getX() + 0.5 + dir.getOffsetX() * 0.501;
                                double py = glassPos.getY() + 0.95;
                                double pz = glassPos.getZ() + 0.5 + dir.getOffsetZ() * 0.501;

                                // Petit random latéral pour l'effet naturel
                                if (client.world.random.nextInt(7) == 0) {
                                    double rx = (dir.getAxis() == Direction.Axis.Z) ? (client.world.random.nextDouble() - 0.5) * 0.8 : 0.0;
                                    double rz = (dir.getAxis() == Direction.Axis.X) ? (client.world.random.nextDouble() - 0.5) * 0.8 : 0.0;
                                    client.world.addParticle(ModParticles.WINDOW_DRIP_TYPE, px + rx, py, pz + rz, 0, 0, 0);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

}
