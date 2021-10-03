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

public class KiHolderCapability extends CapabilityAttacher {
    private static final Class<KiHolder> CAPABILITY_CLASS = KiHolder.class;
    @CapabilityInject(KiHolder.class) // HAS to be public!
    public static final Capability<KiHolder> KI_HOLDER_CAPABILITY = null;
    private static final ResourceLocation KI_HOLDER_RL = new ResourceLocation(AnimeAbilitiesMod.MODID, "ki_holder");

    @Nullable
    public static KiHolder getKiHolderUnwrap(PlayerEntity player) {
        return getKiHolder(player).orElse(null);
    }

    public static LazyOptional<KiHolder> getKiHolder(PlayerEntity player) {
        return player.getCapability(KI_HOLDER_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, PlayerEntity player) {
        genericAttachCapability(event, new KiHolder(player), KI_HOLDER_CAPABILITY, KI_HOLDER_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerPlayerAttacher(KiHolderCapability::attach, KiHolderCapability::getKiHolder);
    }
}
