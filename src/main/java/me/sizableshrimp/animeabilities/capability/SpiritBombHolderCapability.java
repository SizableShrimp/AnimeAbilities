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
public class SpiritBombHolderCapability extends CapabilityAttacher {
    private static final Class<SpiritBombHolder> CAPABILITY_CLASS = SpiritBombHolder.class;
    @CapabilityInject(SpiritBombHolder.class) // HAS to be public!
    public static final Capability<SpiritBombHolder> SPIRIT_BOMB_HOLDER_CAPABILITY = null;
    private static final ResourceLocation SPIRIT_BOMB_HOLDER_RL = new ResourceLocation(AnimeAbilitiesMod.MODID, "spirit_bomb_holder");

    @Nullable
    public static SpiritBombHolder getSpiritBombHolderUnwrap(PlayerEntity player) {
        return getSpiritBombHolder(player).orElse(null);
    }

    public static LazyOptional<SpiritBombHolder> getSpiritBombHolder(PlayerEntity player) {
        return player.getCapability(SPIRIT_BOMB_HOLDER_CAPABILITY);
    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getObject();
            genericAttachCapability(event, new SpiritBombHolder(player), SPIRIT_BOMB_HOLDER_CAPABILITY, SPIRIT_BOMB_HOLDER_RL, false);
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            getSpiritBombHolder(serverPlayer).ifPresent(spiritBombHolder -> spiritBombHolder.sendPlayerUpdatePacket(serverPlayer));
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof PlayerEntity) {
            PlayerEntity playerToTrack = (PlayerEntity) event.getTarget();
            getSpiritBombHolder(playerToTrack).ifPresent(spiritBombHolder -> spiritBombHolder.sendPlayerUpdatePacket((ServerPlayerEntity) event.getPlayer()));
        }
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
    }
}
