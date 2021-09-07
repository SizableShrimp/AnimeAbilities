package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.network.KiStatusPacket;
import me.sizableshrimp.animeabilities.network.NetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class KiHolder implements ISyncableCapability {
    private float ki = 500;
    private float maxKi = 500;
    private int kamehamehaRemainingAnimation = 0;
    private int kiBlastLastUsedTime = 0;
    private final PlayerEntity player;

    public KiHolder(PlayerEntity player) {
        this.player = player;
    }

    public float getKi() {
        return ki;
    }

    public void setKi(float ki) {
        this.ki = ki;
        this.updateTracking();
    }

    public float getMaxKi() {
        return maxKi;
    }

    public void setMaxKi(float maxKi) {
        this.maxKi = maxKi;
        this.updateTracking();
    }

    public int getKamehamehaRemainingAnimation() {
        return kamehamehaRemainingAnimation;
    }

    public boolean isUsingKamehameha() {
        return kamehamehaRemainingAnimation > 0;
    }

    public void setKamehamehaRemainingAnimation(int kamehamehaRemainingAnimation, boolean sync) {
        this.kamehamehaRemainingAnimation = kamehamehaRemainingAnimation;
        if (sync)
            this.updateTracking();
    }

    public int getMaxKiBlastAnimation() {
        return 80;
    }

    public int getKiBlastLastUsedTime() {
        return kiBlastLastUsedTime;
    }

    public void setKiBlastLastUsedTime(int kiBlastLastUsedTime, boolean sync) {
        this.kiBlastLastUsedTime = kiBlastLastUsedTime;
        if (sync)
            this.updateTracking();
    }

    public boolean isPlayerOnCooldown() {
        return kiBlastLastUsedTime + getKiBlastCooldownDuration() > player.tickCount;
    }

    public int getKiBlastCooldownDuration() {
        return 20;
    }

    @Override
    public void updateTracking() {
        if (player.level.isClientSide)
            return;
        NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), this.createUpdatePacket());
    }

    @Override
    public KiStatusPacket createUpdatePacket() {
        return new KiStatusPacket(player.getId(), this);
    }

    @Override
    public void sendPlayerUpdatePacket(ServerPlayerEntity serverPlayer) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), this.createUpdatePacket());
    }

    @Override
    public CompoundNBT serializeNBT(boolean savingToDisk) {
        CompoundNBT tag = new CompoundNBT();
        tag.putFloat("Ki", ki);
        tag.putFloat("MaxKi", maxKi);
        tag.putInt("KamehamehaAnimation", kamehamehaRemainingAnimation);
        if (!savingToDisk)
            tag.putInt("KiBlastLastUsedTime", kiBlastLastUsedTime);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt, boolean readingFromDisk) {
        this.ki = nbt.getFloat("Ki");
        this.maxKi = nbt.getFloat("MaxKi");
        this.kamehamehaRemainingAnimation = nbt.getInt("KiBlastAnimation");
        if (!readingFromDisk)
            this.kiBlastLastUsedTime = nbt.getInt("KiBlastLastUsedTime");
    }
}
