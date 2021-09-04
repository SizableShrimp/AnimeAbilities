package me.sizableshrimp.animeabilities.client.renderlayer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.sizableshrimp.animeabilities.client.event.DragonBallRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;

public class DragonBallLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
    public DragonBallLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStack.pushPose();
        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(DragonBallRenderer.SPHERE_MODEL);
        Random random = new Random();
        random.setSeed(42L);
        List<BakedQuad> quads = model.getQuads(null, null, random, EmptyModelData.INSTANCE);
        IVertexBuilder vertexBuilder = ItemRenderer.getFoilBuffer(buffer, RenderTypeExtension.SPHERE, false, false);

        MatrixStack.Entry matrixEntry = matrixStack.last();

        for (BakedQuad quad : quads) {
            int packedColor = 0x0FFFFF;

            float r = (packedColor >> 16 & 255) / 255.0F;
            float g = (packedColor >> 8 & 255) / 255.0F;
            float b = (packedColor & 255) / 255.0F;
            vertexBuilder.addVertexData(matrixEntry, quad, r, g, b, packedLight, OverlayTexture.NO_OVERLAY, true);
        }
        matrixStack.popPose();
    }

    private static class RenderTypeExtension extends RenderType {
        private RenderTypeExtension(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_,
                Runnable p_i225992_8_) {
            super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
        }

        static final RenderType SPHERE = create("animeabilities_sphere", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, RenderType.State.builder()
                    .setTextureState(new RenderState.TextureState(PlayerContainer.BLOCK_ATLAS, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    //.setDiffuseLightingState(DIFFUSE_LIGHTING) - need to disable this
                    .setAlphaState(DEFAULT_ALPHA)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false));
    }
}
