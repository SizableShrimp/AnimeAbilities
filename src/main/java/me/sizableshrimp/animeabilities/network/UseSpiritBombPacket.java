package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.capability.KiHolderCapability;
import me.sizableshrimp.animeabilities.item.DragonBallItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class UseSpiritBombPacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        if (player == null)
            return;
        context.enqueueWork(() -> Registration.DRAGON_BALL.get().useSpiritBomb(player));
    }

    @Override
    public void write(PacketBuffer packetBuf) {}

    public static UseSpiritBombPacket read(PacketBuffer packetBuf) {
        return new UseSpiritBombPacket();
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, UseSpiritBombPacket.class, UseSpiritBombPacket::read);
    }
}
