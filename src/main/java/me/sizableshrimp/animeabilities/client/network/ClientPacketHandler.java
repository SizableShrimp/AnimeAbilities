package me.sizableshrimp.animeabilities.client.network;

import me.sizableshrimp.animeabilities.capability.ISyncableCapability;
import me.sizableshrimp.animeabilities.network.CapabilityStatusPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

import java.util.function.Function;

public class ClientPacketHandler {
    public static <T extends Entity> void handleCapabilityStatus(CapabilityStatusPacket packet, Function<T, ISyncableCapability> function) {
        ISyncableCapability capability = function.apply(getEntity(packet));
        if (capability != null)
            capability.deserializeNBT(packet.getTag());
    }

    private static <T extends Entity> T getEntity(CapabilityStatusPacket packet) {
        ClientWorld level = Minecraft.getInstance().level;
        if (level == null)
            return null;
        Entity entity = level.getEntity(packet.getEntityId());
        //noinspection unchecked
        return (T) entity;
    }
}
