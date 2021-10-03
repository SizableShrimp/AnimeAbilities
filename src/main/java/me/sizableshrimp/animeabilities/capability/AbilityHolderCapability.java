package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = AnimeAbilitiesMod.MODID)
public class AbilityHolderCapability extends CapabilityAttacher {
    private static final Class<AbilityHolder> CAPABILITY_CLASS = AbilityHolder.class;
    @CapabilityInject(AbilityHolder.class) // HAS to be public!
    public static final Capability<AbilityHolder> ABILITY_HOLDER_CAPABILITY = null;
    private static final ResourceLocation ABILITY_HOLDER_RL = new ResourceLocation(AnimeAbilitiesMod.MODID, "ability_holder");

    @Nullable
    public static AbilityHolder getAbilityHolderUnwrap(PlayerEntity player) {
        return getAbilityHolder(player).orElse(null);
    }

    public static LazyOptional<AbilityHolder> getAbilityHolder(PlayerEntity player) {
        return player.getCapability(ABILITY_HOLDER_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, PlayerEntity player) {
        genericAttachCapability(event, new AbilityHolder(player), ABILITY_HOLDER_CAPABILITY, ABILITY_HOLDER_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerPlayerAttacher(AbilityHolderCapability::attach, AbilityHolderCapability::getAbilityHolder);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity oldPlayer = event.getOriginal();
        PlayerEntity newPlayer = event.getPlayer();

        // So we can copy capabilities
        oldPlayer.revive();

        getAbilityHolder(oldPlayer).ifPresent(oldAbilityHolder -> getAbilityHolder(newPlayer)
                .ifPresent(newAbilityHolder -> newAbilityHolder.deserializeNBT(oldAbilityHolder.serializeNBT(false), false)));
    }
}
