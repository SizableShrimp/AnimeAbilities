package me.sizableshrimp.animeabilities.container;

import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.capability.AbilityHolderCapability;
import me.sizableshrimp.animeabilities.item.AbilityItem;
import me.sizableshrimp.animeabilities.item.UpgradeItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class AbilityContainer extends Container {
    private final IInventory abilities;

    public AbilityContainer(int containerId, PlayerInventory playerInv) {
        super(Registration.ABILITIES_CONTAINER.get(), containerId);
        this.abilities = AbilityHolderCapability.getAbilityHolderUnwrap(playerInv.player).getAbilities();

        checkContainerSize(this.abilities, 18);
        this.abilities.startOpen(playerInv.player);

        // Abilities
        for (int row = 0; row < 2; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new AbilitySlot(this.abilities, column + row * 9, column * 18 + 8, row * 18 + 20));
            }
        }
        // for (int column = 0; column < 9; ++column) {
        //     this.addSlot(new AbilitySlot(this.abilities, column, 8 + column * 18, 20));
        // }

        // Player Inv
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInv, column + row * 9 + 9, 8 + column * 18, row * 18 + 69));
            }
        }

        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInv, column, 8 + column * 18, 127));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.abilities.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.abilities.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.abilities.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.abilities.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        this.abilities.stopOpen(player);
    }

    static class AbilitySlot extends Slot {
        public AbilitySlot(IInventory pContainer, int pIndex, int pX, int pY) {
            super(pContainer, pIndex, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof AbilityItem || stack.getItem() instanceof UpgradeItem;
        }
    }
}
