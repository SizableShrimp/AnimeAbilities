package me.sizableshrimp.animeabilities.item;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Lazy;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpgradeableAbilityItem<U extends IUpgradeType> extends AbilityItem {
    private final Map<IUpgradeType, Supplier<Item>> upgradeMap;

    public UpgradeableAbilityItem(Properties properties, U[] upgradeTypes) {
        super(properties);
        this.upgradeMap = Arrays.stream(upgradeTypes)
                .collect(ImmutableMap.toImmutableMap(Function.identity(), u -> Lazy.of(() -> u.getUpgradeItemSupplier().get())));
    }

    public boolean hasUpgrade(PlayerEntity player, U upgradeType) {
        return AbilityItem.findAbilityItem(player, upgradeMap.get(upgradeType).get()).orElse(null) != null;
    }
}
