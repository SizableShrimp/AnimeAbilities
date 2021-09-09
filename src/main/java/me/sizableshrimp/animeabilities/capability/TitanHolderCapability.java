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
public class TitanHolderCapability extends CapabilityAttacher {
    private static final Class<TitanHolder> CAPABILITY_CLASS = TitanHolder.class;
    @CapabilityInject(TitanHolder.class) // HAS to be public!
    public static final Capability<TitanHolder> TITAN_HOLDER_CAPABILITY = null;
    private static final ResourceLocation TITAN_HOLDER_RL = new ResourceLocation(AnimeAbilitiesMod.MODID, "titan_holder");

    @Nullable
    public static TitanHolder getTitanHolderUnwrap(PlayerEntity player) {
        return getTitanHolder(player).orElse(null);
    }

    public static LazyOptional<TitanHolder> getTitanHolder(PlayerEntity player) {
        return player.getCapability(TITAN_HOLDER_CAPABILITY);
    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getObject();
            genericAttachCapability(event, new TitanHolder(player), TITAN_HOLDER_CAPABILITY, TITAN_HOLDER_RL, false);
            // Manually refresh dimensions again since setting up the dimensions for the first time happens before cap gathering
            player.refreshDimensions();
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            getTitanHolder(serverPlayer).ifPresent(titanHolder -> titanHolder.sendPlayerUpdatePacket(serverPlayer));
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof PlayerEntity) {
            PlayerEntity playerToTrack = (PlayerEntity) event.getTarget();
            getTitanHolder(playerToTrack).ifPresent(titanHolder -> titanHolder.sendPlayerUpdatePacket((ServerPlayerEntity) event.getPlayer()));
        }
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
    }
}
