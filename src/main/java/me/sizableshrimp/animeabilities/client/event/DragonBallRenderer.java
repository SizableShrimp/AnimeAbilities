package me.sizableshrimp.animeabilities.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.capability.KiHolderCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = AnimeAbilitiesMod.MODID, value = Dist.CLIENT)
public class DragonBallRenderer {
    public static final ResourceLocation KI_BAR = new ResourceLocation(AnimeAbilitiesMod.MODID, "textures/ki_bar.png");

    @SubscribeEvent
    public static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
        boolean textRender = event.getType() == RenderGameOverlayEvent.ElementType.TEXT;
        boolean hotbarRender = event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR;
        if (!textRender && !hotbarRender)
            return;

        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (mc.screen != null || player == null || player.isSpectator() || !Registration.DRAGON_BALL.get().hasThisAbility(player))
            return;

        MatrixStack matrixStack = event.getMatrixStack();
        int scaledWidth = mc.getWindow().getGuiScaledWidth();
        int barWidth = 200;
        int barHeight = 11;
        int rightEdgeSpacer = 5;
        int topEdgeSpacer = 4;
        KiHolderCapability.getKiHolder(player).ifPresent(kiHolder -> {
            int ki = (int) kiHolder.getKi();
            int maxKi = (int) kiHolder.getMaxKi();
            if (textRender) {
                String kiMsg = ki + "/" + maxKi + " Ki";
                int x = scaledWidth - (barWidth / 2) - (Minecraft.getInstance().font.width(kiMsg) / 2) - rightEdgeSpacer;
                mc.font.draw(matrixStack, kiMsg, x, topEdgeSpacer + 2, 0xFFFFFF);
            } else if (hotbarRender) {
                mc.getTextureManager().bind(KI_BAR);
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                int x = scaledWidth - barWidth - rightEdgeSpacer;
                AbstractGui.blit(matrixStack, x, topEdgeSpacer, 0, 0, barWidth, barHeight, barWidth, barHeight);
                float percentage = (float) ki / maxKi;
                blitColor(matrixStack, x + 1, topEdgeSpacer + 1, (int) ((barWidth - 2) * percentage), barHeight - 2, 15, 255, 255, 190);
                RenderSystem.defaultBlendFunc();
            }
        });
    }

    private static void blitColor(MatrixStack matrixStack, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        innerBlitColor(matrixStack, x, x + width, y, y + height, 0, red, green, blue, alpha);
    }

    private static void innerBlitColor(MatrixStack matrixStack, int x1, int x2, int y1, int y2, int pBlitZOffset, int red, int green, int blue, int alpha) {
        innerBlitColor(matrixStack.last().pose(), x1, x2, y1, y2, pBlitZOffset, red, green, blue, alpha);
    }

    private static void innerBlitColor(Matrix4f pMatrix, int pX1, int pX2, int pY1, int pY2, int pBlitZOffset, int red, int green, int blue, int alpha) {
        RenderSystem.disableTexture();
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.vertex(pMatrix, pX1, pY2, pBlitZOffset).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(pMatrix, pX2, pY2, pBlitZOffset).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(pMatrix, pX2, pY1, pBlitZOffset).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(pMatrix, pX1, pY1, pBlitZOffset).color(red, green, blue, alpha).endVertex();
        bufferbuilder.end();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.end(bufferbuilder);
    }
}
