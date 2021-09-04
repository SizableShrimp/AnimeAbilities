package me.sizableshrimp.animeabilities.item;

import com.google.common.collect.ImmutableMap;
import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.capability.KiHolderCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DragonBallItem extends UpgradeableAbilityItem<DragonBallItem.UpgradeType> {
    private final Map<UpgradeType, Supplier<ItemStack>> upgradeMap = Arrays.stream(UpgradeType.values())
            .collect(ImmutableMap.toImmutableMap(Function.identity(), u -> Lazy.of(() -> new ItemStack(u.getUpgradeItemSupplier().get()))));

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
        // MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * true if they had enough, false if they didn't
     */
    public boolean useKi(PlayerEntity player, int cost) {
        return KiHolderCapability.getKiHolder(player).map(kiHolder -> {
            int currentKi = kiHolder.getKi();
            if (currentKi < cost)
                return false;

            kiHolder.setKi(currentKi - cost);
            return true;
        }).orElse(false);
    }
}
