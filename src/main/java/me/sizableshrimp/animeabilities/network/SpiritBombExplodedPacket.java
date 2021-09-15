package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.entity.SpiritBombEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SpiritBombExplodedPacket implements IPacket {
    private final double x;
    private final double y;
    private final double z;

    public SpiritBombExplodedPacket(Vector3d vec) {
        this(vec.x(), vec.y(), vec.z());
    }

    public SpiritBombExplodedPacket(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        SpiritBombEntity.cancelNextExplosionSound = true;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
            return;
        mc.level.playLocalSound(x, y, z, Registration.SPIRIT_BOMB_EXPLODE_SOUND.get(), SoundCategory.PLAYERS, 4F, 1F, false);
    }

    @Override
    public void write(PacketBuffer packetBuf) {
        packetBuf.writeDouble(this.x);
        packetBuf.writeDouble(this.y);
        packetBuf.writeDouble(this.z);
    }

    public static SpiritBombExplodedPacket read(PacketBuffer packetBuf) {
        return new SpiritBombExplodedPacket(packetBuf.readDouble(), packetBuf.readDouble(), packetBuf.readDouble());
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, SpiritBombExplodedPacket.class, SpiritBombExplodedPacket::read);
    }
}
