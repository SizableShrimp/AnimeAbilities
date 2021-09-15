package me.sizableshrimp.animeabilities.entity;

import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.network.SpiritBombExplodedPacket;
import me.sizableshrimp.animeabilities.network.NetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class SpiritBombEntity extends AbstractKiEntity {
    public static boolean cancelNextExplosionSound = false;

    public SpiritBombEntity(EntityType<? extends SpiritBombEntity> type, World world) {
        super(type, world);
    }

    public SpiritBombEntity(LivingEntity living) {
        super(Registration.SPIRIT_BOMB_ENTITY_TYPE.get(), living, 0.8F, 5F);
    }

    @Override
    protected void onCollide() {
        if (this.level.isClientSide)
            return;
        Entity owner = this.getOwner();
        if (owner instanceof ServerPlayerEntity) {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) owner), new SpiritBombExplodedPacket(this.position()));
        }
        this.level.explode(this, this.getX(), this.getY(), this.getZ(), 15, true, Explosion.Mode.DESTROY);
    }
}
