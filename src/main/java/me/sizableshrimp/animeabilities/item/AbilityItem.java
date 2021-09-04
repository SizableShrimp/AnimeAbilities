package me.sizableshrimp.animeabilities.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AbilityItem extends Item {
    private final ItemStack checkerStack = new ItemStack(this);

    public AbilityItem(Properties properties) {
        super(properties);
    }

    public boolean hasThisAbility(PlayerEntity player) {
        return player.inventory.contains(checkerStack);
    }
}
