package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.capability.SpiritBombHolder;
import me.sizableshrimp.animeabilities.capability.SpiritBombHolderCapability;
import me.sizableshrimp.animeabilities.client.network.ClientPacketHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SpiritBombStatusPacket extends CapabilityStatusPacket {
    private SpiritBombStatusPacket(int entityId, CompoundNBT tag) {
        super(entityId, tag);
    }

    public SpiritBombStatusPacket(int entityId, SpiritBombHolder capability) {
        super(entityId, capability);
    }

    public static void register(SimpleChannel channel, int id) {
        register(channel, id, SpiritBombStatusPacket.class, buf -> read(buf, SpiritBombStatusPacket::new));
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientPacketHandler.handleCapabilityStatus(this, SpiritBombHolderCapability::getSpiritBombHolderUnwrap));
    }
}
