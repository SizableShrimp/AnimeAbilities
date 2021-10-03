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

    private static void attach(AttachCapabilitiesEvent<Entity> event, PlayerEntity player) {
        genericAttachCapability(event, new TitanHolder(player), TITAN_HOLDER_CAPABILITY, TITAN_HOLDER_RL, false);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerPlayerAttacher(TitanHolderCapability::attach, TitanHolderCapability::getTitanHolder);
    }
}
