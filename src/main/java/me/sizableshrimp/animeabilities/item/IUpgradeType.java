package me.sizableshrimp.animeabilities.item;

import net.minecraft.item.Item;

import java.util.function.Supplier;

public interface IUpgradeType {
    Supplier<Item> getUpgradeItemSupplier();
}
