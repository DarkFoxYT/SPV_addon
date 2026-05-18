package net.dark.spv_addon.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.world.levels.BackroomsLevel;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.DirectionalLight;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.level207.Level207ChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.dark.spv_addon.world.transitions.SpbTransitionDirector;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;

public class Level207BackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();
    DirectionalLight light;
    float brightness;
    private boolean registered = false;


    public Level207BackroomsLevel() {
        super("level207", Level207ChunkGenerator.CODEC, new Vec3d(7, 22, 7), BackroomsLevels.LEVEL207_WORLD_KEY, "spv_addon");
    }

    private BackroomsLevel.LevelTransition getPoolRoomsTransition(PlayerComponent playerComponent) {
        return SpbTransitionDirector.createTransition(
                playerComponent,
                com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL.getSpawnPos(),
                this,
                com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL,
                SpbTransitionDirector.TransitionProfile.unstableGlitch()
        );
    }


    @Override
    public void register() {
        if (registered) {
            return;
        }
        registered = true;

        this.registerEvent("bellwalker_spawn", net.dark.spv_addon.world.events.level207.Level207BellWalkerEvent::new);
        this.registerEvent("empty", HaHvavCustomEvent::new);


    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) {
            if (this.light == null) {
                this.brightness = 1F;
                this.light = new DirectionalLight();
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.light.setBrightness(this.brightness).setColor(0.28F, 0.28F, 0.28F));
            }
        }
    }


    @Override
    public int nextEventDelay() {
        return this.random.nextBetween(300, 1200);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
    }


    @Override
    public void readFromNbt(NbtCompound nbt) {
    }


    public void transitionOut(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {
    }

    @Override
    public void transitionIn(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {
    }

}
