package me.sizableshrimp.animeabilities.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.sizableshrimp.animeabilities.entity.SpiritBombEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SpiritBombRenderer extends EntityRenderer<SpiritBombEntity> {
    public SpiritBombRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(SpiritBombEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        renderSpiritBomb(matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SpiritBombEntity entity) {
        return null;
    }

    public static void renderSpiritBomb(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        matrixStack.pushPose();
        matrixStack.scale(0.4F, 0.4F, 0.4F);
        OBJRenderer.renderSphere(matrixStack, buffer, packedLight, 0xC8DEFBFF);
        matrixStack.popPose();

        OBJRenderer.renderSphere(matrixStack, buffer, packedLight, 0x7D88ECFF);

        matrixStack.pushPose();
        matrixStack.scale(1.1F, 1.1F, 1.1F);
        OBJRenderer.renderSphere(matrixStack, buffer, packedLight, 0x5FA0F3FF);
        matrixStack.popPose();
    }
}
