package me.sizableshrimp.animeabilities.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import me.sizableshrimp.animeabilities.client.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
        ColorUtil.ColorData color = ColorUtil.unpackColor(packedColor);

        RenderSystem.enableBlend();
        for (BakedQuad quad : quads) {
            vertexBuilder.addVertexData(matrixEntry, quad, color.r(), color.g(), color.b(), color.alpha(), packedLight, OverlayTexture.NO_OVERLAY, true);
        }
    }
}
