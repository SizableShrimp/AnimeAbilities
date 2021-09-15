package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.item.DragonBallItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class UseKamehamehaPacket implements IPacket {
    private final boolean start;

    public UseKamehamehaPacket(boolean start) {
        this.start = start;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        if (player == null)
            return;
        context.enqueueWork(() -> Registration.DRAGON_BALL.get().useKamehameha(player, start));
    }

    public static boolean canKamehameha(PlayerEntity player) {
        DragonBallItem dragonBallItem = Registration.DRAGON_BALL.get();
        return player != null && !DragonBallItem.isUsingSpiritBomb(player) && !DragonBallItem.isUsingKamehameha(player)
                && dragonBallItem.hasThisAbility(player) && dragonBallItem.hasUpgrade(player, DragonBallItem.UpgradeType.SUPER_SAIYAN);
    }

    @Override
    public void write(PacketBuffer packetBuf) {
        packetBuf.writeBoolean(this.start);
    }

    public static UseKamehamehaPacket read(PacketBuffer packetBuf) {
        return new UseKamehamehaPacket(packetBuf.readBoolean());
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, UseKamehamehaPacket.class, UseKamehamehaPacket::read);
    }
}
