package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.network.KiStatusPacket;
import me.sizableshrimp.animeabilities.network.NetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class KiHolder implements ISyncableCapability {
    private int ki = 500;
    private int maxKi = 500;
    private final PlayerEntity player;

    public KiHolder(PlayerEntity player) {
        this.player = player;
    }

    public int getKi() {
        return ki;
    }

    public void setKi(int ki) {
        this.ki = ki;
        this.updateTracking();
    }

    public int getMaxKi() {
        return maxKi;
    }

    public void setMaxKi(int maxKi) {
        this.maxKi = maxKi;
        this.updateTracking();
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
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("Ki", ki);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.ki = nbt.getInt("Ki");
    }
}
