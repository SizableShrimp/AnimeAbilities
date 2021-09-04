package me.sizableshrimp.animeabilities.item;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Lazy;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpgradeableAbilityItem<U extends IUpgradeType> extends AbilityItem {
    private final Map<IUpgradeType, Supplier<ItemStack>> upgradeMap;

    public UpgradeableAbilityItem(Properties properties, U[] upgradeTypes) {
        super(properties);
        this.upgradeMap = Arrays.stream(upgradeTypes)
                .collect(ImmutableMap.toImmutableMap(Function.identity(), u -> Lazy.of(() -> new ItemStack(u.getUpgradeItemSupplier().get()))));
    }

    public boolean hasUpgrade(PlayerEntity player, U upgradeType) {
        return player.inventory.contains(upgradeMap.get(upgradeType).get());
    }
}
