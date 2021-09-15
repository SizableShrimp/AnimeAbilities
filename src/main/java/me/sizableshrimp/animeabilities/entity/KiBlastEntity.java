package me.sizableshrimp.animeabilities.entity;

import me.sizableshrimp.animeabilities.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

public class KiBlastEntity extends AbstractKiEntity {
    public KiBlastEntity(EntityType<? extends KiBlastEntity> type, World world) {
        super(type, world);
    }

    public KiBlastEntity(LivingEntity living) {
        super(Registration.KI_BLAST_ENTITY_TYPE.get(), living, 1.3F, 2F);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        Entity target = result.getEntity();
        if (!this.level.isClientSide)
            target.hurt(DamageSource.thrown(this, this.getOwner()), 5F);

        super.onHitEntity(result);
    }

    @Override
    protected boolean removeWhen() {
        return false;
    }

    @Override
    protected int getTickDespawnCount() {
        return 100;
    }
}
