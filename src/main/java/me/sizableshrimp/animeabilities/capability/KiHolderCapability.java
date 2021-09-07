package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = AnimeAbilitiesMod.MODID)
public class KiHolderCapability extends CapabilityAttacher {
    @CapabilityInject(KiHolder.class)
    public static final Capability<KiHolder> KI_HOLDER_CAPABILITY = null;
    private static final ResourceLocation KI_HOLDER_RL = new ResourceLocation(AnimeAbilitiesMod.MODID, "ki_holder");

    @Nullable
    public static KiHolder getKiHolderUnwrap(PlayerEntity player) {
        return getKiHolder(player).orElse(null);
    }

    public static LazyOptional<KiHolder> getKiHolder(PlayerEntity player) {
        return player.getCapability(KI_HOLDER_CAPABILITY);
    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getObject();
            genericAttachCapability(event, new KiHolder(player), KI_HOLDER_CAPABILITY, KI_HOLDER_RL);
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            getKiHolder(serverPlayer).ifPresent(kiHolder -> kiHolder.sendPlayerUpdatePacket(serverPlayer));
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof PlayerEntity) {
            PlayerEntity playerToTrack = (PlayerEntity) event.getTarget();
            getKiHolder(playerToTrack).ifPresent(kiHolder -> kiHolder.sendPlayerUpdatePacket((ServerPlayerEntity) event.getPlayer()));
        }
    }

    public static void register() {
        CapabilityAttacher.registerCapability(KiHolder.class);
    }
}
