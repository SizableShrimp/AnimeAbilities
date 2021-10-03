package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.item.AbilityItem;
import me.sizableshrimp.animeabilities.item.DragonBallItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class OpenAbilitiesPacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        if (player == null)
            return;
        context.enqueueWork(() -> AbilityItem.openAbilitiesContainer(player));
    }

    @Override
    public void write(PacketBuffer packetBuf) {}

    public static OpenAbilitiesPacket read(PacketBuffer packetBuf) {
        return new OpenAbilitiesPacket();
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, OpenAbilitiesPacket.class, OpenAbilitiesPacket::read);
    }
}
