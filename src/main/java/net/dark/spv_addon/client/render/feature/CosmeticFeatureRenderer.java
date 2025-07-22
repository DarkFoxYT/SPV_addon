package net.dark.spv_addon.client.render.feature;

import net.dark.spv_addon.cosmetics.CosmeticType;
import net.dark.spv_addon.cosmetics.SpvCosmetics;
import net.dark.spv_addon.cosmetics.registry.RegisteredCosmetic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class CosmeticFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> 
        extends FeatureRenderer<T, M> {
    
    private final HeldItemRenderer heldItemRenderer;
    
    public CosmeticFeatureRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }
    
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, 
                      int light, T livingEntity, float limbAngle, float limbDistance, 
                      float tickDelta, float animationProgress, float headYaw, float headPitch) {
        
        if (!(livingEntity instanceof PlayerEntity player)) {
            return;
        }
        
        // Render head cosmetics
        renderHeadCosmetic(matrices, vertexConsumerProvider, light, player);
        
        // Render back cosmetics
        renderBackCosmetic(matrices, vertexConsumerProvider, light, player);
        
        // Render chest cosmetics
        renderChestCosmetic(matrices, vertexConsumerProvider, light, player);
        
        // Add more cosmetic types as needed
    }
    
    private void renderHeadCosmetic(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, 
                                   int light, PlayerEntity player) {
        RegisteredCosmetic headCosmetic = SpvCosmetics.getEquippedCosmetic(player, CosmeticType.HEAD);
        
        if (headCosmetic.isNone() || headCosmetic.getItem() == Items.AIR) {
            return;
        }
        
        matrices.push();
        
        // Position on head
        this.getContextModel().getHead().rotate(matrices);
        matrices.translate(0.0F, -0.25F, 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        
        // Scale the cosmetic
        float scale = 0.625F;
        matrices.scale(scale, -scale, -scale);
        
        // Render the cosmetic item
        this.heldItemRenderer.renderItem(
            player, 
            headCosmetic.getItemStack(), 
            ModelTransformationMode.HEAD, 
            false, 
            matrices, 
            vertexConsumerProvider, 
            light
        );
        
        matrices.pop();
    }
    
    private void renderBackCosmetic(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, 
                                   int light, PlayerEntity player) {
        RegisteredCosmetic backCosmetic = SpvCosmetics.getEquippedCosmetic(player, CosmeticType.BACK);
        
        if (backCosmetic.isNone() || backCosmetic.getItem() == Items.AIR) {
            return;
        }
        
        matrices.push();
        
        // Position on back
        matrices.translate(0.0F, 0.0F, 0.125F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        
        // Scale the cosmetic
        float scale = 0.5F;
        matrices.scale(scale, scale, scale);
        
        // Render the cosmetic item
        this.heldItemRenderer.renderItem(
            player, 
            backCosmetic.getItemStack(), 
            ModelTransformationMode.FIXED, 
            false, 
            matrices, 
            vertexConsumerProvider, 
            light
        );
        
        matrices.pop();
    }
    
    private void renderChestCosmetic(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, 
                                    int light, PlayerEntity player) {
        RegisteredCosmetic chestCosmetic = SpvCosmetics.getEquippedCosmetic(player, CosmeticType.CHEST);
        
        if (chestCosmetic.isNone() || chestCosmetic.getItem() == Items.AIR) {
            return;
        }
        
        matrices.push();
        
        // Position on chest
        matrices.translate(0.0F, 0.3F, -0.3F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        
        // Scale the cosmetic
        float scale = 0.4F;
        matrices.scale(scale, scale, scale);
        
        // Render the cosmetic item
        this.heldItemRenderer.renderItem(
            player, 
            chestCosmetic.getItemStack(), 
            ModelTransformationMode.FIXED, 
            false, 
            matrices, 
            vertexConsumerProvider, 
            light
        );
        
        matrices.pop();
    }
}
