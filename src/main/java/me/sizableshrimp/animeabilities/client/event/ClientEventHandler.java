package me.sizableshrimp.animeabilities.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.capability.KiHolderCapability;
import me.sizableshrimp.animeabilities.capability.SpiritBombHolderCapability;
import me.sizableshrimp.animeabilities.item.DragonBallItem;
import me.sizableshrimp.animeabilities.network.KiChargePacket;
import me.sizableshrimp.animeabilities.network.NetworkHandler;
import me.sizableshrimp.animeabilities.network.UseSpiritBombPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = AnimeAbilitiesMod.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    private static final List<LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>> alwaysRenderLayers = new ArrayList<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.phase != TickEvent.Phase.END || mc.player == null)
            return;

        if (ClientSetupHandler.KI_CHARGE_KEY.isDown()) {
            KiHolderCapability.getKiHolder(mc.player).ifPresent(kiHolder -> {
                if (kiHolder.getKi() < kiHolder.getMaxKi()) {
                    NetworkHandler.INSTANCE.sendToServer(new KiChargePacket());
                }
            });
        }

        if (ClientSetupHandler.SPIRIT_BOMB_KEY.isDown()) {
            SpiritBombHolderCapability.getSpiritBombHolder(mc.player).ifPresent(spiritBombHolder -> {
                if (!spiritBombHolder.isUsingSpiritBomb()) {
                    NetworkHandler.INSTANCE.sendToServer(new UseSpiritBombPacket());
                }
            });
        }
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || !mc.options.getCameraType().isFirstPerson() || player.isSpectator())
            return;

        MatrixStack matrixStack = event.getMatrixStack();
        IRenderTypeBuffer buffer = mc.renderBuffers().bufferSource();
        float partialTicks = event.getPartialTicks();
        int packedLight = mc.getEntityRenderDispatcher().getPackedLightCoords(player, partialTicks);

        Vector3d cameraVec = mc.gameRenderer.getMainCamera().getPosition();
        double lerpedX = MathHelper.lerp(partialTicks, player.xOld, player.getX());
        double lerpedY = MathHelper.lerp(partialTicks, player.yOld, player.getY());
        double lerpedZ = MathHelper.lerp(partialTicks, player.zOld, player.getZ());
        PlayerRenderer playerRenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
        Vector3d renderOffset = playerRenderer.getRenderOffset(player, partialTicks).subtract(cameraVec);

        matrixStack.pushPose();

        float bob = playerRenderer.getBob(player, partialTicks);
        float lerpedBodyRot = MathHelper.rotLerp(partialTicks, player.yBodyRotO, player.yBodyRot);
        playerRenderer.setupRotations(player, matrixStack, bob, lerpedBodyRot, partialTicks);
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        playerRenderer.scale(player, matrixStack, partialTicks);
        matrixStack.translate(0.0D, -1.501F, 0.0D);
        matrixStack.translate(lerpedX + renderOffset.x(), lerpedY + renderOffset.y(), lerpedZ + renderOffset.z());

        alwaysRenderLayers.forEach(layerRenderer -> layerRenderer.render(matrixStack, buffer, packedLight, player, 0, 0, partialTicks, 0, 0, 0));

        matrixStack.popPose();
    }

    @SubscribeEvent
    public static void onPlayerModelRenderPre(PlayerModelEvent.Render.Pre event) {
        if (isDragonBallFloating(event.getPlayer())) {
            PlayerModel<?> playerModel = event.getModelPlayer();
            playerModel.leftLeg.xRot = 0;
            playerModel.rightLeg.xRot = 0;
            playerModel.leftArm.xRot = 0;
            playerModel.rightArm.xRot = 0;
            event.getMatrixStack().mulPose(Vector3f.XP.rotationDegrees(90F));
            event.getMatrixStack().pushPose();
        }
    }

    @SubscribeEvent
    public static void onPlayerModelRenderPost(PlayerModelEvent.Render.Post event) {
        if (isDragonBallFloating(event.getPlayer())) {
            event.getMatrixStack().popPose();
        }
    }

    private static boolean isDragonBallFloating(PlayerEntity player) {
        ModifiableAttributeInstance gravityAttribute = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        return gravityAttribute != null
                && player.getDeltaMovement().y <= DragonBallItem.ANIME_SLOW_FALLING.getAmount()
                && gravityAttribute.hasModifier(DragonBallItem.ANIME_SLOW_FALLING)
                && Registration.DRAGON_BALL.get().hasThisAbility(player);
    }

    public static void addAlwaysRenderLayer(LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> layerRenderer) {
        alwaysRenderLayers.add(layerRenderer);
    }
}
