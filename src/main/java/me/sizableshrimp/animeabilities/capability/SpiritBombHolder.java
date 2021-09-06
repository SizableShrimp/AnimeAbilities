package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.network.KiStatusPacket;
import me.sizableshrimp.animeabilities.network.NetworkHandler;
import me.sizableshrimp.animeabilities.network.SpiritBombStatusPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class SpiritBombHolder implements ISyncableCapability {
    private int spiritBombRemainingAnimation = 0;
    private final PlayerEntity player;

    public SpiritBombHolder(PlayerEntity player) {
        this.player = player;
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
    public void updateTracking() {
        if (player.level.isClientSide)
            return;
        NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), this.createUpdatePacket());
    }

    @Override
    public SpiritBombStatusPacket createUpdatePacket() {
        return new SpiritBombStatusPacket(player.getId(), this);
    }

    @Override
    public void sendPlayerUpdatePacket(ServerPlayerEntity serverPlayer) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), this.createUpdatePacket());
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("RemainingAnimation", this.spiritBombRemainingAnimation);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.spiritBombRemainingAnimation = nbt.getInt("RemainingAnimation");
    }
}
