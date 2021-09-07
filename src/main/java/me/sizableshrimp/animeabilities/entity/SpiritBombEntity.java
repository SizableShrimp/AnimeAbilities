package me.sizableshrimp.animeabilities.entity;

import me.sizableshrimp.animeabilities.Registration;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class SpiritBombEntity extends AbstractKiEntity {
    public SpiritBombEntity(EntityType<? extends SpiritBombEntity> type, World world) {
        super(type, world);
    }

    public SpiritBombEntity(LivingEntity living) {
        super(Registration.SPIRIT_BOMB_ENTITY_TYPE.get(), living, 0.8F, 5F);
    }

    @Override
    protected void onCollide() {
        this.level.explode(this, this.getX(), this.getY(), this.getZ(), 7, true, Explosion.Mode.DESTROY);
    }
}
