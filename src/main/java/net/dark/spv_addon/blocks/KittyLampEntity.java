package net.dark.spv_addon.blocks;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.dark.spv_addon.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class KittyLampEntity extends BlockEntity {
    PointLight light;
    float brightness;
    float angle;
    int ticks;
    Random random = Random.create();
    int randomInt;
    boolean on;

    public KittyLampEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KITTY_LAMP, pos, state);
        this.randomInt = this.random.nextBetween(1, 4);
        this.on = true;
    }

    public void markRemoved() {
        if (this.light != null && this.world.isClient) {
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.light);
            this.light = null;
        }

        super.markRemoved();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) {
            if (this.light == null) {
                Vec3d position = pos.toCenterPos().add((double)0, 0, (double)0);
                this.brightness = 1F;
                this.light = new PointLight();
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(((PointLight)this.light.setBrightness(this.brightness).setColor(1,0,1).setRadius(13).setPosition(new Vector3d(position.x, position.y, position.z))));
            }
        }
    }
}
