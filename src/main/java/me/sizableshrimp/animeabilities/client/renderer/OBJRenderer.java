package me.sizableshrimp.animeabilities.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;
import java.util.Random;

public class OBJRenderer {
    public static final ResourceLocation SPHERE_MODEL = new ResourceLocation(AnimeAbilitiesMod.MODID, "block/sphere");
    public static final ResourceLocation CYLINDER_MODEL = new ResourceLocation(AnimeAbilitiesMod.MODID, "block/cylinder");

    public static void renderSphere(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, int packedColor) {
        renderModel(matrixStack, buffer, SPHERE_MODEL, packedLight, packedColor);
    }

    public static void renderCylinder(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, int packedColor) {
        renderModel(matrixStack, buffer, CYLINDER_MODEL, packedLight, packedColor);
    }

    private static void renderModel(MatrixStack matrixStack, IRenderTypeBuffer buffer, ResourceLocation modelLocation, int packedLight, int packedColor) {
        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);
        Random random = new Random();
        random.setSeed(42L);
        List<BakedQuad> quads = model.getQuads(null, null, random, EmptyModelData.INSTANCE);
        IVertexBuilder vertexBuilder = ItemRenderer.getFoilBuffer(buffer, RenderTypeExtension.ENTITY_TRANSLUCENT_NO_DIFFUSE, false, false);

        MatrixStack.Entry matrixEntry = matrixStack.last();
        float alpha = (packedColor >> 24 & 255) / 255.0F;
        float r = (packedColor >> 16 & 255) / 255.0F;
        float g = (packedColor >> 8 & 255) / 255.0F;
        float b = (packedColor & 255) / 255.0F;

        for (BakedQuad quad : quads) {
            vertexBuilder.addVertexData(matrixEntry, quad, r, g, b, alpha, packedLight, OverlayTexture.NO_OVERLAY, true);
        }
    }

    public static class RenderTypeExtension extends RenderType {
        private RenderTypeExtension(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_,
                Runnable p_i225992_8_) {
            super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
        }

        public static final RenderType ENTITY_TRANSLUCENT_NO_DIFFUSE = create("animeabilities_entity_translucent_no_diffuse", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, RenderType.State.builder()
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
