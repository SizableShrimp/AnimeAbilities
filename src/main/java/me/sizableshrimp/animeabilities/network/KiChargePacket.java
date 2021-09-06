package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.capability.KiHolderCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class KiChargePacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        if (player == null)
            return;
        context.enqueueWork(() -> KiHolderCapability.getKiHolder(player).ifPresent(kiHolder -> {
            int ki = kiHolder.getKi();
            int maxKi = kiHolder.getMaxKi();
            if (ki == maxKi)
                return;
            kiHolder.setKi(Math.min(maxKi, ki + 3));
        }));
    }

    @Override
    public void write(PacketBuffer packetBuf) {}

    public static KiChargePacket read(PacketBuffer packetBuf) {
        return new KiChargePacket();
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, KiChargePacket.class, KiChargePacket::read);
    }
}
