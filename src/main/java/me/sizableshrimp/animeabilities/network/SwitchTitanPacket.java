package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.capability.TitanHolderCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SwitchTitanPacket implements IPacket {
    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        if (player == null)
            return;
        context.enqueueWork(() -> Registration.ATTACK_ON_TITAN.get().switchTitans(player));
    }

    public static boolean canSwitchTitan(PlayerEntity player) {
        return player != null && Registration.ATTACK_ON_TITAN.get().hasThisAbility(player);
    }

    @Override
    public void write(PacketBuffer packetBuf) {}

    public static SwitchTitanPacket read(PacketBuffer packetBuf) {
        return new SwitchTitanPacket();
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, SwitchTitanPacket.class, SwitchTitanPacket::read);
    }
}
