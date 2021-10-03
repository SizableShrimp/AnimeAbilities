package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.network.CapabilityStatusPacket;
import me.sizableshrimp.animeabilities.network.NetworkHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

public interface ISyncableCapability extends INBTSavable<CompoundNBT> {
    void updateTracking();

    CapabilityStatusPacket createUpdatePacket();

    default void sendUpdatePacketToPlayer(ServerPlayerEntity serverPlayer) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), this.createUpdatePacket());
    }
}
