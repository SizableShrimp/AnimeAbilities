package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.network.NetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class PlayerCapability implements ISyncableCapability {
    protected final PlayerEntity player;

    protected PlayerCapability(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void updateTracking() {
        if (player.level.isClientSide)
            return;
        NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), this.createUpdatePacket());
    }

    @Override
    public void sendPlayerUpdatePacket(ServerPlayerEntity serverPlayer) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), this.createUpdatePacket());
    }
}
