package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.network.SpiritBombStatusPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class SpiritBombHolder extends PlayerCapability {
    private int spiritBombRemainingAnimation = 0;

    public SpiritBombHolder(PlayerEntity player) {
        super(player);
    }

    public boolean isUsingSpiritBomb() {
        return spiritBombRemainingAnimation > 0;
    }

    public int getSpiritBombRemainingAnimation() {
        return spiritBombRemainingAnimation;
    }

    public void setSpiritBombRemainingAnimation(int spiritBombRemainingAnimation, boolean sync) {
        this.spiritBombRemainingAnimation = spiritBombRemainingAnimation;
        if (sync)
            this.updateTracking();
    }

    @Override
    public SpiritBombStatusPacket createUpdatePacket() {
        return new SpiritBombStatusPacket(player.getId(), this);
    }

    @Override
    public CompoundNBT serializeNBT(boolean savingToDisk) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("RemainingAnimation", this.spiritBombRemainingAnimation);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt, boolean readingFromDisk) {
        this.spiritBombRemainingAnimation = nbt.getInt("RemainingAnimation");
    }
}
