package net.dark.spv_addon.entities.client.renderer;

import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.entities.client.model.BellWalkerModel;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;
import software.bernie.geckolib.model.GeoModel;
import com.sp.entity.ik.model.GeckoLib.GeoModelAccessor;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class BellWalkerRenderer extends DynamicGeoEntityRenderer<BellWalkerEntity> {

    public BellWalkerRenderer(EntityRendererFactory.Context context) {
        super(context, new BellWalkerModel());
    }

    @Override
    public void render(BellWalkerEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {

        GeoModel<BellWalkerEntity> modelProvider = this.getGeoModel();
        GeoModelAccessor accessor = new GeoModelAccessor(modelProvider);
        entity.getModelPositions(entity, accessor);
        InitializeComponents.BELL_WALKER.get(entity).getIKComponent().tickClient(entity, accessor);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
