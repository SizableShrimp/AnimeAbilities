package me.sizableshrimp.animeabilities.item;

import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.capability.TitanHolder;
import me.sizableshrimp.animeabilities.capability.TitanHolderCapability;
import me.sizableshrimp.animeabilities.network.SwitchTitanPacket;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.function.Supplier;

public class AttackOnTitanItem extends UpgradeableAbilityItem<AttackOnTitanItem.UpgradeType> {
    public enum UpgradeType implements IUpgradeType {
        ARMORED(Registration.ARMORED_TITAN_UPGRADE), COLOSSAL(Registration.COLOSSAL_TITAN_UPGRADE);

        private final Supplier<Item> upgradeItem;

        UpgradeType(Supplier<Item> upgradeItem) {
            this.upgradeItem = upgradeItem;
        }

        @Override
        public Supplier<Item> getUpgradeItemSupplier() {
            return upgradeItem;
        }
    }

    public AttackOnTitanItem(Properties properties) {
        super(properties, UpgradeType.values());
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void switchTitans(PlayerEntity player) {
        if (!SwitchTitanPacket.canSwitchTitan(player))
            return;
        List<TitanHolder.Type> available = TitanHolder.Type.getAllAvailable(player);
        available.add(null); // Allow null (no titan)
        TitanHolderCapability.getTitanHolder(player).ifPresent(titanHolder -> {
            TitanHolder.Type currentType = titanHolder.getType();
            TitanHolder.Type newType = available.get((available.indexOf(currentType) + 1) % available.size());
            titanHolder.setType(newType, true);
            titanHolder.refreshPlayerTitan();
        });
    }

    @SubscribeEvent
    public void onEntitySize(EntityEvent.Size event) {
        if (!(event.getEntity() instanceof PlayerEntity))
            return;
        PlayerEntity player = (PlayerEntity) event.getEntity();

        TitanHolderCapability.getTitanHolder(player).ifPresent(titanHolder -> {
            boolean isCrouching = event.getPose() == Pose.CROUCHING;
            if (event.getPose() == Pose.STANDING || isCrouching) {
                TitanHolder.Type type = titanHolder.getType();
                if (type == null)
                    return;
                EntitySize size = type.getSize();
                event.setNewSize(size);
                event.setNewEyeHeight(size.height * 0.85F + (isCrouching ? -0.35F : 0F));
            }
        });
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        TitanHolderCapability.getTitanHolder(player).resolve()
                .map(TitanHolder::getType)
                .ifPresent(titanType -> player.setDeltaMovement(player.getDeltaMovement().multiply(1, titanType.getJumpScale() * 0.42, 1)));
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if (hasThisAbility(player) && TitanHolderCapability.getTitanHolder(player).resolve().map(TitanHolder::getType).isPresent()) {
                event.setCanceled(true);
            }
        }
    }
}
