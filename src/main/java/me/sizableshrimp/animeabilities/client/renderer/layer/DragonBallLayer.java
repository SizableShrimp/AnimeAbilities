package me.sizableshrimp.animeabilities.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.capability.KiHolder;
import me.sizableshrimp.animeabilities.capability.KiHolderCapability;
import me.sizableshrimp.animeabilities.capability.SpiritBombHolder;
import me.sizableshrimp.animeabilities.capability.SpiritBombHolderCapability;
import me.sizableshrimp.animeabilities.client.AnimeKeyBindings;
import me.sizableshrimp.animeabilities.client.event.ClientEventHandler;
import me.sizableshrimp.animeabilities.client.renderer.OBJRenderer;
import me.sizableshrimp.animeabilities.client.renderer.RenderTypeExtension;
import me.sizableshrimp.animeabilities.client.renderer.SpiritBombRenderer;
import me.sizableshrimp.animeabilities.client.util.ColorUtil;
import me.sizableshrimp.animeabilities.item.DragonBallItem;
import me.sizableshrimp.animeabilities.network.KiChargePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class DragonBallLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
    private static final ResourceLocation AURA_LOCATION = new ResourceLocation(AnimeAbilitiesMod.MODID, "textures/aura.png");
    private static boolean added;
    private final ModelRenderer aura = new ModelRenderer(this.getParentModel());

    public DragonBallLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> renderer) {
        super(renderer);
        if (!added) {
            ClientEventHandler.addAlwaysRenderLayer(this);
            added = true;
        }

        aura.setTexSize(64, 32).texOffs(14, 0).addBox(-12.0F, 0.0F, 18.0F, 20.0F, 0.0F, 28.0F, 0.0F);
        aura.setPos(0, 32, 0);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netYRot, float headPitch) {
        if (ClientEventHandler.isRenderingInventory())
            return;
        if (DragonBallItem.isUsingSpiritBomb(player)) {
            renderSpiritBomb(matrixStack, buffer, player, packedLight);
        }
        Minecraft mc = Minecraft.getInstance();
        if ((AnimeKeyBindings.KI_CHARGE.isDown() && KiChargePacket.canChargeKi(mc.player))
                || (Registration.DRAGON_BALL.get().isBoostFlying(player) && (mc.player != player || !mc.options.getCameraType().isFirstPerson()))) {
            renderAura(matrixStack, buffer, player, packedLight, 0xFFFFDD00);
        }

        boolean usingKamehameha = DragonBallItem.isUsingKamehameha(player);
        boolean activeKamehameha = !usingKamehameha && DragonBallItem.isKamehamehaActive(player);
        if (usingKamehameha || activeKamehameha) {
            renderKamehameha(player, matrixStack, buffer, partialTicks, packedLight, activeKamehameha);
        }
    }

    private void renderSpiritBomb(MatrixStack matrixStack, IRenderTypeBuffer buffer, AbstractClientPlayerEntity player, int packedLight) {
        SpiritBombHolder spiritBombHolder = SpiritBombHolderCapability.getSpiritBombHolderUnwrap(player);
        int usedDuration = DragonBallItem.SPIRIT_BOMB_ANIMATION_DURATION - spiritBombHolder.getSpiritBombRemainingAnimation();
        float scale = usedDuration / (float) DragonBallItem.SPIRIT_BOMB_ANIMATION_DURATION;
        matrixStack.pushPose();
        matrixStack.translate(0, -DragonBallItem.getSphereYOffset(), 0);
        matrixStack.scale(scale, scale, scale);
        SpiritBombRenderer.renderSpiritBomb(matrixStack, buffer, packedLight);
        matrixStack.popPose();
    }

    private void renderAura(MatrixStack matrixStack, IRenderTypeBuffer buffer, AbstractClientPlayerEntity player, int packedLight, int packedColor) {
        IVertexBuilder builder = buffer.getBuffer(RenderTypeExtension.aura(AURA_LOCATION));
        ColorUtil.ColorData color = ColorUtil.unpackColor(packedColor);

        for (int i = 0; i < 8; i++) {
            renderAuraInternal(matrixStack, builder, player, packedLight, color, player.tickCount + (i * 5));
        }
    }

    private void renderKamehameha(PlayerEntity player, MatrixStack matrixStack, IRenderTypeBuffer buffer, float partialTicks, int packedLight, boolean active) {
        KiHolder kiHolder = KiHolderCapability.getKiHolderUnwrap(player);
        if (kiHolder == null)
            return;
        // int i = 10;
        float lerpedYBodyRot = MathHelper.rotLerp(partialTicks, player.yBodyRotO, player.yBodyRot);
        // matrixStack.mulPose(Vector3f.YN.rotationDegrees(180.0F - lerpedYBodyRot));
        matrixStack.pushPose();
        Minecraft mc = Minecraft.getInstance();
        boolean isFirstPerson = mc.options.getCameraType().isFirstPerson() && mc.player == player;

        matrixStack.translate(0D, 0.25D, 0D);
        float lerpedYHeadRot = MathHelper.rotLerp(partialTicks, player.yHeadRotO, player.yHeadRot);
        float lerpedXRot = MathHelper.rotLerp(partialTicks, player.xRotO, player.xRot);
        float net = lerpedYHeadRot - lerpedYBodyRot;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(90 + net));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(lerpedXRot));

        matrixStack.pushPose();

        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));
        float usedPercentage;
        if (active) {
            usedPercentage = 1F;
        } else {
            int minAnimTime = kiHolder.getMinKamehamehaAnimation();
            int usedAnimTime = kiHolder.getUsedKamehamehaAnimation();
            usedPercentage = Math.min((float) usedAnimTime / minAnimTime, 1F);
        }
        float xzScale = usedPercentage * (isFirstPerson ? 0.2F : 0.4F);

        matrixStack.scale(xzScale, 5F, xzScale);

        if (isFirstPerson) {
            matrixStack.translate(0, -0.92, 0);
        } else {
            matrixStack.translate(0, -1.1, 0);
        }
        OBJRenderer.renderCylinder(matrixStack, buffer, packedLight, 0x5F0689FD);

        matrixStack.popPose();

        matrixStack.popPose();
    }

    private void renderAuraInternal(MatrixStack matrixStack, IVertexBuilder builder, AbstractClientPlayerEntity player, int packedLight, ColorUtil.ColorData color, int offset) {
        float auraX = offset % 60 * 6F;
        float auraY = offset % 33 * 0.85F;

        matrixStack.pushPose();
        matrixStack.translate(0, -auraY * 0.1125F, 0);
        float scale = Math.min(1F, (33 - offset % 33) / 33F);
        matrixStack.scale(scale, scale, scale);

        aura.yRot = 0;
        aura.xRot = 0;
        for (int i = 0; i < 16; ++i) {
            aura.yRot = (float) (Math.toRadians(auraX)/* - Math.toRadians(player.yBodyRot)*/ + Math.toRadians(i * 360D / 16));
            aura.xRot = (float) (Math.toRadians(25.0D + auraY * 0.15D));
            aura.render(matrixStack, builder, packedLight, OverlayTexture.NO_OVERLAY, color.r(), color.g(), color.b(), color.alpha());
        }

        matrixStack.popPose();
    }

    // private static void renderPart(MatrixStack pMatrixStack, IVertexBuilder pBuffer, float pRed, float pGreen, float pBlue, float pAlpha, int pYMin, int pYMax, float p_228840_8_, float p_228840_9_,
    //         float p_228840_10_, float p_228840_11_, float p_228840_12_, float p_228840_13_, float p_228840_14_, float p_228840_15_, float pU1, float pU2, float pV1, float pV2) {
    //     MatrixStack.Entry matrixstack$entry = pMatrixStack.last();
    //     Matrix4f matrix4f = matrixstack$entry.pose();
    //     Matrix3f matrix3f = matrixstack$entry.normal();
    //     renderQuad(matrix4f, matrix3f, pBuffer, pRed, pGreen, pBlue, pAlpha, pYMin, pYMax, p_228840_8_, p_228840_9_, p_228840_10_, p_228840_11_, pU1, pU2, pV1, pV2);
    //     renderQuad(matrix4f, matrix3f, pBuffer, pRed, pGreen, pBlue, pAlpha, pYMin, pYMax, p_228840_14_, p_228840_15_, p_228840_12_, p_228840_13_, pU1, pU2, pV1, pV2);
    //     renderQuad(matrix4f, matrix3f, pBuffer, pRed, pGreen, pBlue, pAlpha, pYMin, pYMax, p_228840_10_, p_228840_11_, p_228840_14_, p_228840_15_, pU1, pU2, pV1, pV2);
    //     renderQuad(matrix4f, matrix3f, pBuffer, pRed, pGreen, pBlue, pAlpha, pYMin, pYMax, p_228840_12_, p_228840_13_, p_228840_8_, p_228840_9_, pU1, pU2, pV1, pV2);
    // }
    //
    // private static void renderQuad(Matrix4f pMatrixPos, Matrix3f pMatrixNormal, IVertexBuilder pBuffer, float pRed, float pGreen, float pBlue, float pAlpha, int pYMin, int pYMax, float pX1,
    //         float pZ1, float pX2, float pZ2, float pU1, float pU2, float pV1, float pV2) {
    //     addVertex(pMatrixPos, pMatrixNormal, pBuffer, pRed, pGreen, pBlue, pAlpha, pYMax, pX1, pZ1, pU2, pV1);
    //     addVertex(pMatrixPos, pMatrixNormal, pBuffer, pRed, pGreen, pBlue, pAlpha, pYMin, pX1, pZ1, pU2, pV2);
    //     addVertex(pMatrixPos, pMatrixNormal, pBuffer, pRed, pGreen, pBlue, pAlpha, pYMin, pX2, pZ2, pU1, pV2);
    //     addVertex(pMatrixPos, pMatrixNormal, pBuffer, pRed, pGreen, pBlue, pAlpha, pYMax, pX2, pZ2, pU1, pV1);
    // }
    //
    // private static void addVertex(Matrix4f matrixPos, Matrix3f matrixNormal, IVertexBuilder pBuffer, float red, float green, float blue, float alpha, int y, float x, float z, float texU,
    //         float texV) {
    //     pBuffer.vertex(matrixPos, x, y, z)
    //             .color(red, green, blue, alpha)
    //             .uv(texU, texV)
    //             .overlayCoords(OverlayTexture.NO_OVERLAY)
    //             .uv2(15728880)
    //             .normal(matrixNormal, 0.0F, 1.0F, 0.0F)
    //             .endVertex();
    // }
}
