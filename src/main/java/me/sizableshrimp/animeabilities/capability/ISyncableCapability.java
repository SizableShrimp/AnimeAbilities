package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.network.CapabilityStatusPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISyncableCapability extends INBTSerializable<CompoundNBT> {
    void updateTracking();

    CapabilityStatusPacket createUpdatePacket();

    void sendPlayerUpdatePacket(ServerPlayerEntity serverPlayer);
}
