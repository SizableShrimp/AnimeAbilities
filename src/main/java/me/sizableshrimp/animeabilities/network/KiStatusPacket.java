package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.capability.KiHolder;
import me.sizableshrimp.animeabilities.capability.KiHolderCapability;
import me.sizableshrimp.animeabilities.client.network.ClientPacketHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class KiStatusPacket extends CapabilityStatusPacket {
    private KiStatusPacket(int entityId, CompoundNBT tag) {
        super(entityId, tag);
    }

    public KiStatusPacket(int entityId, KiHolder capability) {
        super(entityId, capability);
    }

    public static void register(SimpleChannel channel, int id) {
        register(channel, id, KiStatusPacket.class, buf -> read(buf, KiStatusPacket::new));
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientPacketHandler.handleCapabilityStatus(this, KiHolderCapability::getKiHolderUnwrap));
        ctx.get().setPacketHandled(true);
    }
}
