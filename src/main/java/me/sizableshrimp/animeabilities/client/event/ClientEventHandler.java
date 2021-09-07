package me.sizableshrimp.animeabilities.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.capability.KiHolderCapability;
import me.sizableshrimp.animeabilities.capability.SpiritBombHolderCapability;
import me.sizableshrimp.animeabilities.client.AnimeKeyBindings;
import me.sizableshrimp.animeabilities.client.sound.KiChargeSound;
import me.sizableshrimp.animeabilities.item.DragonBallItem;
import me.sizableshrimp.animeabilities.network.BoostFlyPacket;
import me.sizableshrimp.animeabilities.network.KiChargePacket;
import me.sizableshrimp.animeabilities.network.NetworkHandler;
import me.sizableshrimp.animeabilities.network.UseKiBlastPacket;
import me.sizableshrimp.animeabilities.network.UseSpiritBombPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ElytraSound;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = AnimeAbilitiesMod.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    private static final List<LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>> alwaysRenderLayers = new ArrayList<>();

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == AnimeKeyBindings.KI_CHARGE.getKey().getValue()
                && AnimeKeyBindings.KI_CHARGE.isConflictContextAndModifierActive() && KiChargePacket.canChargeKi(mc.player)) {
            if (mc.player != null && mc.level != null) {
                mc.level.playSound(mc.player, mc.player, Registration.KI_START_CHARGE_SOUND.get(), SoundCategory.PLAYERS, 1F, 1F);
                mc.getSoundManager().playDelayed(new KiChargeSound(p -> AnimeKeyBindings.KI_CHARGE.isDown(), p -> 0.7F), 5);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.phase != TickEvent.Phase.END || mc.player == null)
            return;

        if (AnimeKeyBindings.KI_CHARGE.isDown() && KiChargePacket.canChargeKi(mc.player)) {
            KiHolderCapability.getKiHolder(mc.player).ifPresent(kiHolder -> {
                if (kiHolder.getKi() < kiHolder.getMaxKi()) {
                    NetworkHandler.INSTANCE.sendToServer(new KiChargePacket());
                }
            });
        }

        if (AnimeKeyBindings.KI_BLAST.isDown() && UseKiBlastPacket.canKiBlast(mc.player)) {
            NetworkHandler.INSTANCE.sendToServer(new UseKiBlastPacket());
        }

        if (AnimeKeyBindings.SPIRIT_BOMB.isDown()) {
            SpiritBombHolderCapability.getSpiritBombHolder(mc.player).ifPresent(spiritBombHolder -> {
                if (Registration.DRAGON_BALL.get().hasThisAbility(mc.player) && !spiritBombHolder.isUsingSpiritBomb()) {
                    NetworkHandler.INSTANCE.sendToServer(new UseSpiritBombPacket());
                }
            });
        }

        if (AnimeKeyBindings.BOOST.isDown() && !mc.player.abilities.flying
                && !mc.player.isPassenger() && !mc.player.onClimbable()
                && Registration.DRAGON_BALL.get().hasThisAbility(mc.player)) {
            boolean boostHeld = isBoostHeld(); //ClientSetupHandler.BOOST.isDown();
            if (!mc.player.isFallFlying()) {
                if (mc.player.isOnGround()) {
                    mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(0, 1, 0));
                    mc.player.level.playSound(mc.player, mc.player, Registration.BOOST_FLY_JUMP_SOUND.get(), SoundCategory.PLAYERS, 1F, 1F);
                }
                // NetworkHandler.INSTANCE.sendToServer(new BoostFlyPacket(true));
                mc.player.startFallFlying();
                mc.getSoundManager().play(new KiChargeSound(LivingEntity::isFallFlying, p -> isBoostHeld() ? 0.7F : 0.5F));
            }
            if (mc.player.isFallFlying() && mc.player.tickCount % 4 == 0)
                boost(mc.player, boostHeld);
        } else if (!AnimeKeyBindings.BOOST.isDown() && mc.player.isFallFlying() && !mc.player.getItemBySlot(EquipmentSlotType.CHEST).canElytraFly(mc.player)) {
            mc.player.stopFallFlying();
            NetworkHandler.INSTANCE.sendToServer(new BoostFlyPacket(false));
        }
    }

    private static boolean isBoostHeld() {
        return Minecraft.getInstance().options.keyJump.isDown();
    }

    private static void boost(PlayerEntity player, boolean boostHeld) {
        double minBoost = boostHeld ? 1.5 : 0.5;
        if (player.getDeltaMovement().length() < minBoost) {
            player.setDeltaMovement(player.getDeltaMovement().add(player.getLookAngle()));
        }
        // Vector3d velocity = player.getDeltaMovement();
        // Vector3d lookVec = player.getLookAngle();
        // double d1 = 0.1D;
        // double d0 = 1.5D;
        // double addedX = lookVec.x * d1 + (lookVec.x * d0 - velocity.x) * 0.5D;
        // double addedY = lookVec.y * d1 + (lookVec.y * d0 - velocity.y) * 0.5D;
        // double addedZ = lookVec.z * d1 + (lookVec.z * d0 - velocity.z) * 0.5D;
        // player.setDeltaMovement(velocity.add(addedX, addedY, addedZ));
    }

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        if (isDragonBallFlying() && event.getSound() instanceof ElytraSound) {
            event.setResultSound(null);
        }
    }

    public static boolean isDragonBallFlying() {
        Minecraft mc = Minecraft.getInstance();
        return AnimeKeyBindings.BOOST.isDown() && mc.player != null && Registration.DRAGON_BALL.get().hasThisAbility(mc.player)
                && mc.player.isFallFlying();
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
        float lerpedYBodyRot = MathHelper.rotLerp(partialTicks, player.yBodyRotO, player.yBodyRot);
        float lerpedYHeadRot = MathHelper.rotLerp(partialTicks, player.yHeadRotO, player.yHeadRot);
        float netYRot = lerpedYHeadRot - lerpedYBodyRot;
        float lerpedXRot = MathHelper.lerp(partialTicks, player.xRotO, player.xRot);

        Vector3d cameraVec = mc.gameRenderer.getMainCamera().getPosition();
        double lerpedX = MathHelper.lerp(partialTicks, player.xOld, player.getX());
        double lerpedY = MathHelper.lerp(partialTicks, player.yOld, player.getY());
        double lerpedZ = MathHelper.lerp(partialTicks, player.zOld, player.getZ());
        PlayerRenderer playerRenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
        Vector3d renderOffset = playerRenderer.getRenderOffset(player, partialTicks).subtract(cameraVec);

        float bob = playerRenderer.getBob(player, partialTicks);
        matrixStack.translate(lerpedX + renderOffset.x(), lerpedY + renderOffset.y(), lerpedZ + renderOffset.z());

        matrixStack.pushPose();

        playerRenderer.setupRotations(player, matrixStack, bob, lerpedYBodyRot, partialTicks);
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        playerRenderer.scale(player, matrixStack, partialTicks);
        matrixStack.translate(0.0D, -1.501F, 0.0D);

        alwaysRenderLayers.forEach(layerRenderer -> layerRenderer.render(matrixStack, buffer, packedLight, player, 0, 0, partialTicks, bob, netYRot, lerpedXRot));

        matrixStack.popPose();
    }

    @SubscribeEvent
    public static void onPlayerModelRenderPre(PlayerModelEvent.Render.Pre event) {
        PlayerModel<?> playerModel = event.getModelPlayer();
        PlayerEntity player = event.getPlayer();
        // if (isDragonBallFloating(player)) {
        //     double horizMovement = player.getDirection() == Direction.EAST || player.getDirection() == Direction.WEST
        //             ? player.getDeltaMovement().x()
        //             : player.getDeltaMovement().z();
        //     horizMovement *= player.getDirection() == Direction.WEST || player.getDirection() == Direction.NORTH ? -1 : 1;
        //     if (isMovingHorizontally(player) && player.zza >= 0) {
        //         playerModel.leftLeg.xRot = 0;
        //         playerModel.rightLeg.xRot = 0;
        //         int armRot = 60;
        //         playerModel.leftArm.xRot = armRot;
        //         playerModel.rightArm.xRot = armRot;
        //         event.getMatrixStack().mulPose(Vector3f.XP.rotationDegrees(90F));
        //         event.getMatrixStack().mulPose(Vector3f.YP.rotationDegrees((player.xxa * 30) + (-4 * (player.yHeadRot - player.yHeadRotO))));
        //     } else {
        //         playerModel.rightLeg.y -= 2;
        //         event.getMatrixStack().mulPose(Vector3f.XP.rotationDegrees((float) horizMovement * 50));
        //     }
        //     event.getMatrixStack().pushPose();
        // }
        SpiritBombHolderCapability.getSpiritBombHolder(player).ifPresent(spiritBombHolder -> {
            if (!spiritBombHolder.isUsingSpiritBomb())
                return;
            int usedDuration = DragonBallItem.SPIRIT_BOMB_ANIMATION_DURATION - spiritBombHolder.getSpiritBombRemainingAnimation();
            float percentage = usedDuration / (float) DragonBallItem.SPIRIT_BOMB_ANIMATION_DURATION;
            float rotated = Math.max(-3F, -percentage * 12F);
            playerModel.leftArm.xRot = rotated;
            playerModel.rightArm.xRot = rotated;
        });
    }

    @SubscribeEvent
    public static void onPlayerModelRenderPost(PlayerModelEvent.Render.Post event) {
        // if (isDragonBallFloating(event.getPlayer())) {
        //     event.getMatrixStack().popPose();
        // }
    }

    // private static boolean isMovingHorizontally(PlayerEntity player) {
    //     return Math.abs(player.getDeltaMovement().x()) > 0.2 || Math.abs(player.getDeltaMovement().z()) > 0.2;
    // }
    //
    // private static boolean isDragonBallFloating(PlayerEntity player) {
    //     ModifiableAttributeInstance gravityAttribute = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
    //     return gravityAttribute != null
    //             && player.abilities.flying
    //             /*&& gravityAttribute.hasModifier(DragonBallItem.ANIME_SLOW_FALLING)*/
    //             && Registration.DRAGON_BALL.get().hasThisAbility(player);
    // }

    public static void addAlwaysRenderLayer(LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> layerRenderer) {
        alwaysRenderLayers.add(layerRenderer);
    }
}
