package net.dark.spv_addon.blocks.entities;

import com.sp.block.custom.FluorescentLightBlock;
import com.sp.clientWrapper.ClientWrapper;
import com.sp.init.BackroomsLevels;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.dark.spv_addon.init.ModBlockEntities;
import net.dark.spv_addon.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class KittyLightBlockEntity extends BlockEntity {
    public BlockState currentState;
    public Random random = Random.create();
    public java.util.Random random1 = new java.util.Random();
    public boolean playingSound = false;
    public PointLight pointLight;
    public boolean prevOn;
    public final int randInt;
    public int ticks = 0;

    public KittyLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KITTY_LIGHT_BLOCK_ENTITY, pos, state);
        this.currentState = state;
        this.randInt = this.random.nextBetween(1, 5);
    }

    public void markRemoved() {
        super.markRemoved();
        if (this.world != null) {
            if (this.world.isClient) {
                this.setPlayingSound(false);
                if (this.pointLight != null) {
                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
                    this.pointLight = null;
                }
            }
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.getBlockState(pos).getBlock() == ModBlocks.KITTY_LIGHT) {
            ++this.ticks;
            this.currentState = state;
            if (!world.isClient) {
                if (world.getRegistryKey() == net.dark.spv_addon.init.BackroomsLevels.LEVEL_KITTY_WORLD_KEY && world.getBlockState(pos.down()) != Blocks.AIR.getDefaultState()) {
                    world.removeBlockEntity(pos);
                    world.getWorldChunk(pos);
                    world.setBlockState(pos, ModBlocks.KITTY_ROOF.getDefaultState());
                    return;
                }

                BlockState northState = world.getBlockState(pos.north());
                BlockState westState = world.getBlockState(pos.west());
                int northOWest = 0;
                if (northState.getBlock() == ModBlocks.KITTY_LIGHT) {
                    northOWest = 1;
                } else if (westState.getBlock() == ModBlocks.KITTY_LIGHT) {
                    northOWest = 2;
                }

                if (northOWest != 0) {
                    if (northOWest == 1) {
                        world.setBlockState(pos, (BlockState)northState.with(FluorescentLightBlock.COPY, true));
                    } else {
                        world.setBlockState(pos, (BlockState)westState.with(FluorescentLightBlock.COPY, true));
                    }
                } else {
                    if ((Boolean)state.get(FluorescentLightBlock.COPY)) {
                        world.setBlockState(pos, (BlockState)ModBlocks.KITTY_LIGHT.getDefaultState().with(FluorescentLightBlock.COPY, false));
                    }

                    BackroomsLevel var8 = BackroomsLevels.getLevel(this.getWorld());
                    if (!(var8 instanceof Level0BackroomsLevel)) {
                        return;
                    }

                    Level0BackroomsLevel level = (Level0BackroomsLevel)var8;
                    if (level.getLightState() == Level0BackroomsLevel.LightState.BLACKOUT) {
                        world.setBlockState(pos, (BlockState)world.getBlockState(pos).with(FluorescentLightBlock.BLACKOUT, true));
                    }

                    if (level.getLightState() != Level0BackroomsLevel.LightState.ON && (Boolean)state.get(FluorescentLightBlock.ON)) {
                        world.setBlockState(pos, (BlockState)world.getBlockState(pos).with(FluorescentLightBlock.ON, false));
                    }

                    if (level.getLightState() == Level0BackroomsLevel.LightState.FLICKER && !(Boolean)state.get(FluorescentLightBlock.BLACKOUT)) {
                        if (this.ticks % this.randInt == 0) {
                            boolean i = this.random.nextBoolean();
                            if (i) {
                                world.setBlockState(pos, (BlockState)world.getBlockState(pos).with(FluorescentLightBlock.ON, true));
                            } else {
                                world.setBlockState(pos, (BlockState)world.getBlockState(pos).with(FluorescentLightBlock.ON, false));
                            }
                        }
                    } else if (!(Boolean)state.get(FluorescentLightBlock.ON) && level.getLightState() == Level0BackroomsLevel.LightState.ON) {
                        world.setBlockState(pos, (BlockState)world.getBlockState(pos).with(FluorescentLightBlock.ON, true));
                    }
                }
            }

            if (this.ticks > 100) {
                this.ticks = 1;
            }

            this.prevOn = (Boolean)world.getBlockState(pos).get(FluorescentLightBlock.ON);
        }
    }

    public boolean isPlayingSound() {
        return this.playingSound;
    }

    public void setPlayingSound(boolean playingSound) {
        this.playingSound = playingSound;
    }

    public BlockState getCurrentState() {
        return this.currentState;
    }
}
