package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.network.AbilityStatusPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

public class AbilityHolder extends PlayerCapability {
    private final Inventory abilities = new Inventory(18);

    public AbilityHolder(PlayerEntity player) {
        super(player);
    }

    public IInventory getAbilities() {
        return abilities;
    }

    @Override
    public AbilityStatusPacket createUpdatePacket() {
        return new AbilityStatusPacket(player.getId(), this);
    }

    @Override
    public CompoundNBT serializeNBT(boolean savingToDisk) {
        CompoundNBT tag = new CompoundNBT();
        tag.put("Abilities", abilities.createTag());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt, boolean readingFromDisk) {
        if (!readingFromDisk)
            this.abilities.clearContent();
        this.abilities.fromTag(nbt.getList("Abilities", Constants.NBT.TAG_COMPOUND));
    }
}
