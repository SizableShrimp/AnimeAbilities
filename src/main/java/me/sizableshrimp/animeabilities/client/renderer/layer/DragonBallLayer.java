package me.sizableshrimp.animeabilities.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.sizableshrimp.animeabilities.capability.SpiritBombHolder;
import me.sizableshrimp.animeabilities.capability.SpiritBombHolderCapability;
import me.sizableshrimp.animeabilities.client.event.ClientEventHandler;
import me.sizableshrimp.animeabilities.client.renderer.SpiritBombRenderer;
import me.sizableshrimp.animeabilities.item.DragonBallItem;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;

public class DragonBallLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
    private static boolean added;

    public DragonBallLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> renderer) {
        super(renderer);
        if (!added) {
            ClientEventHandler.addAlwaysRenderLayer(this);
            added = true;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch) {
        if (DragonBallItem.isUsingSpiritBomb(player)) {
            renderSpiritBomb(matrixStack, buffer, player, packedLight);
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
}
