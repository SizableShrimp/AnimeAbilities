package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.capability.AbilityHolder;
import me.sizableshrimp.animeabilities.capability.AbilityHolderCapability;
import me.sizableshrimp.animeabilities.capability.TitanHolder;
import me.sizableshrimp.animeabilities.client.network.ClientPacketHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class AbilityStatusPacket extends CapabilityStatusPacket {
    private AbilityStatusPacket(int entityId, CompoundNBT tag) {
        super(entityId, tag);
    }

    public AbilityStatusPacket(int entityId, AbilityHolder capability) {
        super(entityId, capability);
    }

    public static void register(SimpleChannel channel, int id) {
        register(channel, id, AbilityStatusPacket.class, buf -> read(buf, AbilityStatusPacket::new));
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientPacketHandler.handleCapabilityStatus(this, AbilityHolderCapability::getAbilityHolderUnwrap));
    }
}
