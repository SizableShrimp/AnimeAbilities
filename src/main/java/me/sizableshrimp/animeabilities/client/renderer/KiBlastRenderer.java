package me.sizableshrimp.animeabilities.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.sizableshrimp.animeabilities.entity.KiBlastEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class KiBlastRenderer extends EntityRenderer<KiBlastEntity> {
    public KiBlastRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(KiBlastEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        renderKiBlast(matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(KiBlastEntity entity) {
        return null;
    }

    public static void renderKiBlast(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        matrixStack.pushPose();
        matrixStack.scale(0.4F, 0.4F, 0.4F);
        OBJRenderer.renderSphere(matrixStack, buffer, packedLight, 0x5FFFDD00);
        matrixStack.popPose();
    }
}
