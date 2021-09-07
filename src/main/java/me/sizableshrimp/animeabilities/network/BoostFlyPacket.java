package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.Registration;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class BoostFlyPacket implements IPacket {
    private final boolean start;

    public BoostFlyPacket(boolean start) {
        this.start = start;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        if (player == null)
            return;
        context.enqueueWork(() -> {
            if (this.start) {
                if (!player.abilities.mayfly && !player.abilities.flying && Registration.DRAGON_BALL.get().hasThisAbility(player)) {
                    // player.startFallFlying();
                }
            } else {
                player.stopFallFlying();
            }
        });
    }

    @Override
    public void write(PacketBuffer packetBuf) {
        packetBuf.writeBoolean(this.start);
    }

    public static BoostFlyPacket read(PacketBuffer packetBuf) {
        return new BoostFlyPacket(packetBuf.readBoolean());
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, BoostFlyPacket.class, BoostFlyPacket::read);
    }
}
