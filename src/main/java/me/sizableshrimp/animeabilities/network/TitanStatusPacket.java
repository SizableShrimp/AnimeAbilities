package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.capability.TitanHolder;
import me.sizableshrimp.animeabilities.capability.TitanHolderCapability;
import me.sizableshrimp.animeabilities.client.network.ClientPacketHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class TitanStatusPacket extends CapabilityStatusPacket {
    private TitanStatusPacket(int entityId, CompoundNBT tag) {
        super(entityId, tag);
    }

    public TitanStatusPacket(int entityId, TitanHolder capability) {
        super(entityId, capability);
    }

    public static void register(SimpleChannel channel, int id) {
        register(channel, id, TitanStatusPacket.class, buf -> read(buf, TitanStatusPacket::new));
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientPacketHandler.handleCapabilityStatus(this, TitanHolderCapability::getTitanHolderUnwrap));
    }
}
