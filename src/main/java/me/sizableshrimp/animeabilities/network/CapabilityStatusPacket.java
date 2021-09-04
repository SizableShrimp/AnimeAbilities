package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.capability.ISyncableCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CapabilityStatusPacket {
    private final int entityId;
    private final CompoundNBT tag;

    protected CapabilityStatusPacket(int entityId, CompoundNBT tag) {
        this.entityId = entityId;
        this.tag = tag;
    }

    protected CapabilityStatusPacket(int entityId, ISyncableCapability capability) {
        this(entityId, capability.serializeNBT());
    }

    protected static <T extends CapabilityStatusPacket> void register(SimpleChannel channel, int id, Class<T> packetClass, Function<PacketBuffer, T> readFunc) {
        channel.messageBuilder(packetClass, id, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CapabilityStatusPacket::write)
                .decoder(readFunc)
                .consumer(CapabilityStatusPacket::handle)
                .add();
    }

    public void write(PacketBuffer buf) {
        buf.writeInt(entityId);
        buf.writeNbt(tag);
    }

    protected static <T extends CapabilityStatusPacket> T read(PacketBuffer buf, BiFunction<Integer, CompoundNBT, T> function) {
        return function.apply(buf.readInt(), buf.readNbt());
    }

    public abstract void handle(Supplier<NetworkEvent.Context> ctx);

    public int getEntityId() {
        return entityId;
    }

    public CompoundNBT getTag() {
        return tag;
    }
}
