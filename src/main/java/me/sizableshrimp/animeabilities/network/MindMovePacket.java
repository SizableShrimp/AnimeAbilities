package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.capability.KiHolderCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MindMovePacket implements IPacket {
    private final boolean start;

    public MindMovePacket(boolean start) {
        this.start = start;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        if (player == null)
            return;
        context.enqueueWork(() -> KiHolderCapability.getKiHolder(player).ifPresent(kiHolder -> Registration.MOB_PSYCHO.get().mindMove(player, start)));
    }

    public static boolean canMindMove(PlayerEntity player) {
        return player != null && Registration.MOB_PSYCHO.get().hasThisAbility(player);
    }

    @Override
    public void write(PacketBuffer packetBuf) {
        packetBuf.writeBoolean(start);
    }

    public static MindMovePacket read(PacketBuffer packetBuf) {
        return new MindMovePacket(packetBuf.readBoolean());
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, MindMovePacket.class, MindMovePacket::read);
    }
}
