package me.sizableshrimp.animeabilities.item;

import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.capability.KiHolderCapability;
import me.sizableshrimp.animeabilities.capability.SpiritBombHolder;
import me.sizableshrimp.animeabilities.capability.SpiritBombHolderCapability;
import me.sizableshrimp.animeabilities.entity.SpiritBombEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class DragonBallItem extends UpgradeableAbilityItem<DragonBallItem.UpgradeType> {
    // We need to make our own slow falling so forge doesn't remove it
    public static final AttributeModifier ANIME_SLOW_FALLING = new AttributeModifier(
            UUID.fromString("1fe1c0a0-a852-4b79-9d2d-c16ba044af1e"),
            "Slow falling acceleration reduction",
            -0.07, // Add -0.07 to 0.08 so we get the vanilla default of 0.01
            AttributeModifier.Operation.ADDITION);
    public static final int SPIRIT_BOMB_KI_COST = 200;
    public static final int SPIRIT_BOMB_ANIMATION_DURATION = 200;

    public enum UpgradeType implements IUpgradeType {
        KAIKOEN(Registration.KAIKOEN_UPGRADE), SUPER_SAIYEN(Registration.SUPER_SAIYEN_UPGRADE);

        private final Supplier<Item> upgradeItem;

        UpgradeType(Supplier<Item> upgradeItem) {
            this.upgradeItem = upgradeItem;
        }

        @Override
        public Supplier<Item> getUpgradeItemSupplier() {
            return upgradeItem;
        }
    }

    public DragonBallItem(Properties properties) {
        super(properties, UpgradeType.values());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            checkGlide(event.player);
        } else {
            tickSpiritBomb(event.player);
        }
    }

    private void checkGlide(PlayerEntity player) {
        boolean canGlide = !player.abilities.mayfly
                && Registration.DRAGON_BALL.get().hasThisAbility(player)
                && player.getY() > player.level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, player.blockPosition()).getY() + 3;
        ModifiableAttributeInstance gravity = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        if (canGlide) {
            if (!gravity.hasModifier(ANIME_SLOW_FALLING))
                gravity.addTransientModifier(ANIME_SLOW_FALLING);
            player.fallDistance = 0.0F;
        } else {
            gravity.removeModifier(ANIME_SLOW_FALLING);
        }
    }

    private void tickSpiritBomb(PlayerEntity player) {
        SpiritBombHolderCapability.getSpiritBombHolder(player).ifPresent(spiritBombHolder -> {
            if (!spiritBombHolder.isUsingSpiritBomb())
                return;
            int remaining = spiritBombHolder.getSpiritBombRemainingAnimation() - 1;
            spiritBombHolder.setSpiritBombRemainingAnimation(remaining, false);
            if (player.level.isClientSide) {
                if (remaining + 1 == SPIRIT_BOMB_ANIMATION_DURATION) {
                    player.level.playSound(player, player, Registration.SPIRIT_BOMB_CHARGE_SOUND.get(), SoundCategory.PLAYERS, 0.1F, 0.5F);
                }
                double scale = (SPIRIT_BOMB_ANIMATION_DURATION - remaining) / (double) SPIRIT_BOMB_ANIMATION_DURATION;
                if (player.getRandom().nextDouble() < scale) {
                    double y = player.getEyeY() + ((2.0D * player.getRandom().nextDouble() - 1.0D) * scale) + getSphereYOffset();
                    player.level.addParticle(Registration.DRAGONBALL_BOLT.get(), player.getRandomX(scale), y, player.getRandomZ(scale), 0, 0, 0);
                }
            } else {
                // Use a portion of the ki cost for every tick its active, and stop if they don't have enough ki
                boolean hasEnoughKi = useKi(player, SPIRIT_BOMB_KI_COST / SPIRIT_BOMB_ANIMATION_DURATION);
                if (!hasEnoughKi) {
                    spiritBombHolder.setSpiritBombRemainingAnimation(0, false);
                }
                if (!spiritBombHolder.isUsingSpiritBomb()) {
                    // Only sync once it reaches zero on the server to reduce packets sent
                    spiritBombHolder.updateTracking();
                    if (hasEnoughKi) {
                        player.level.addFreshEntity(new SpiritBombEntity(player));
                        player.level.playSound(null, player, SoundEvents.GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.1F, 0.5F);
                    }
                }
            }
        });
    }

    /**
     * true if the player had enough, false if they didn't
     */
    public static boolean useKi(PlayerEntity player, int cost) {
        return hasEnoughKi(player, cost, true);
    }

    /**
     * true if the player has enough, false if they don't
     */
    public static boolean hasEnoughKi(PlayerEntity player, int cost) {
        return hasEnoughKi(player, cost, false);
    }


    private static boolean hasEnoughKi(PlayerEntity player, int cost, boolean use) {
        return KiHolderCapability.getKiHolder(player).map(kiHolder -> {
            int currentKi = kiHolder.getKi();
            if (currentKi < cost)
                return false;

            if (use)
                kiHolder.setKi(currentKi - cost);
            return true;
        }).orElse(false);
    }

    public static boolean isUsingSpiritBomb(PlayerEntity player) {
        return SpiritBombHolderCapability.getSpiritBombHolder(player).map(SpiritBombHolder::isUsingSpiritBomb).orElse(false);
    }

    public boolean useSpiritBomb(PlayerEntity player) {
        if (isUsingSpiritBomb(player) || (!hasUpgrade(player, UpgradeType.SUPER_SAIYEN) && !hasUpgrade(player, UpgradeType.KAIKOEN)) || !hasEnoughKi(player, SPIRIT_BOMB_KI_COST))
            return false;

        SpiritBombHolder spiritBombHolder = SpiritBombHolderCapability.getSpiritBombHolderUnwrap(player);
        if (spiritBombHolder == null)
            return false;

        spiritBombHolder.setSpiritBombRemainingAnimation(SPIRIT_BOMB_ANIMATION_DURATION, true);
        return true;
    }

    public static int getSphereYOffset() {
        return 2;
    }
}
