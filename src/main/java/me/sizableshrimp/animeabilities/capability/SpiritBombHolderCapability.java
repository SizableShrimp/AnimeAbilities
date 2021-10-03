package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

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

    private static void attach(AttachCapabilitiesEvent<Entity> event, PlayerEntity player) {
        genericAttachCapability(event, new SpiritBombHolder(player), SPIRIT_BOMB_HOLDER_CAPABILITY, SPIRIT_BOMB_HOLDER_RL, false);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerPlayerAttacher(SpiritBombHolderCapability::attach, SpiritBombHolderCapability::getSpiritBombHolder);
    }
}
