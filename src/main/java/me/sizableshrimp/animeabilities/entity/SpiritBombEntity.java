package me.sizableshrimp.animeabilities.entity;

import me.sizableshrimp.animeabilities.Registration;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

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
            cancelNextExplosionSound = true;
        this.level.explode(this, this.getX(), this.getY(), this.getZ(), 12, true, Explosion.Mode.DESTROY);
        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), Registration.SPIRIT_BOMB_EXPLODE_SOUND.get(), SoundCategory.PLAYERS, 1F, 1F, false);
    }
}
