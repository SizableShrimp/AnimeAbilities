package me.sizableshrimp.animeabilities.item;

import me.sizableshrimp.animeabilities.Registration;
import net.minecraft.item.Item;

import java.util.function.Supplier;

public class MobPsychoItem extends UpgradeableAbilityItem<MobPsychoItem.UpgradeType> {
    public enum UpgradeType implements IUpgradeType {
        ONE_THIRD(Registration.ONE_THIRD_UPGRADE), TWO_THIRD(Registration.TWO_THIRD_UPGRADE),
        NINETY_NINE(Registration.NINETY_NINE_UPGRADE), QUESTION_MARKS(Registration.QUESTION_MARKS_UPGRADE);

        private final Supplier<Item> upgradeItem;

        UpgradeType(Supplier<Item> upgradeItem) {
            this.upgradeItem = upgradeItem;
        }

        @Override
        public Supplier<Item> getUpgradeItemSupplier() {
            return upgradeItem;
        }
    }

    public MobPsychoItem(Properties properties) {
        super(properties, UpgradeType.values());
        // MinecraftForge.EVENT_BUS.register(this);
    }
}
