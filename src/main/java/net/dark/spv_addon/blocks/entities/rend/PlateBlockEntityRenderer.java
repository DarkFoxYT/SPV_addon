package net.dark.spv_addon.blocks.entities.rend;

import net.dark.spv_addon.blocks.entities.PlateBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class PlateBlockEntityRenderer implements BlockEntityRenderer<PlateBlockEntity> {

    private final ItemRenderer itemRenderer;

    public PlateBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(PlateBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) return;

        matrices.push();

        // Center of block: x=0.5, y=0.46875 (~7.5px up), z=0.5
        matrices.translate(0.5, 0.46875, 0.5);

        // Rotate item flat on the plate (90 degrees on X axis)
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));

        // Scale down to roughly item frame size
        matrices.scale(0.625f, 0.625f, 0.625f);

        // Render item in FIXED mode (like item frame)
        itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, overlay,
                matrices, vertexConsumers, entity.getWorld(), 0);

        matrices.pop();
    }
}
