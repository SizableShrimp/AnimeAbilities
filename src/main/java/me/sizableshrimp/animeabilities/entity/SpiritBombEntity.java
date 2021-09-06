package me.sizableshrimp.animeabilities.entity;

import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.item.DragonBallItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class SpiritBombEntity extends ProjectileEntity {
    public SpiritBombEntity(EntityType<SpiritBombEntity> type, World world) {
        super(type, world);
    }

    public SpiritBombEntity(LivingEntity living) {
        this(Registration.SPIRIT_BOMB_ENTITY_TYPE.get(), living.level);
        Vector3d vec = getRotatedShootVec(living);
        this.setPos(vec.x, vec.y + DragonBallItem.getSphereYOffset(), vec.z);
        this.setOwner(living);
        this.shootFromRotation(living, living.xRot, living.yRot, 0.0F, 0.8F, 5);
    }

    @Override
    public void baseTick() {
        if (!this.isAlive() || tickDespawn() || removeWhenInBlock())
            return;

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
        Vector3d pos = this.position();
        Vector3d motion = this.getDeltaMovement();
        Vector3d future = pos.add(motion);
        calcHit(pos, future);
        updateMotion(motion, future);
    }

    private boolean removeWhenInBlock() {
        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockpos);
        //noinspection deprecation
        if (!blockstate.isAir(this.level, blockpos)) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
            if (!voxelshape.isEmpty()) {
                Vector3d vector3d1 = this.position();

                for (AxisAlignedBB axisalignedbb : voxelshape.toAabbs()) {
                    if (axisalignedbb.move(blockpos).contains(vector3d1)) {
                        this.explodeOnRemove();
                        this.remove();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean tickDespawn() {
        ++this.tickCount;
        if (tickCount >= 1200) {
            this.remove();
            return true;
        }
        return false;
    }

    private void updateMotion(Vector3d motion, Vector3d future) {
        // this.setDeltaMovement(motion.multiply(0.9F, 0.9F, 0.9F).subtract(0, 0.02D, 0)); // Slow down gradually
        this.setPos(future.x, future.y, future.z);
    }

    // Copied from AbstractArrowEntity
    private void calcHit(Vector3d pos, Vector3d future) {
        EntityRayTraceResult entityTrace = this.findHitEntity(pos, future);

        if (entityTrace != null) {
            this.onHitEntity(entityTrace);
        }
    }

    @Nullable
    private EntityRayTraceResult findHitEntity(Vector3d startVec, Vector3d endVec) {
        return ProjectileHelper.getEntityHitResult(this.level, this, startVec, endVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        Entity target = result.getEntity();
        if (target instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) target.getEntity();
            Entity owner = this.getOwner();
            if (player.abilities.invulnerable || owner instanceof PlayerEntity && !((PlayerEntity) owner).canHarmPlayer(player)) {
                return;
            }
        }

        target.hurt(DamageSource.thrown(this, this.getOwner()), 14.0F);
        explodeOnRemove();

        this.remove();
    }

    private void explodeOnRemove() {
        this.level.explode(this, this.getX(), this.getY(), this.getZ(), 10, true, Explosion.Mode.DESTROY);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Age", this.tickCount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.tickCount = compound.getInt("Age");
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private static Vector3d getRotatedShootVec(LivingEntity living) {
        Vector3d playerVec = new Vector3d(living.getX(), living.getEyeY() - 0.1D, living.getZ());
        Vector3d shootVec = new Vector3d(1, 1, 1).multiply(living.getLookAngle());

        double yawRadians = Math.toRadians(6);
        double xPos = shootVec.x * Math.cos(yawRadians) - shootVec.z * Math.sin(yawRadians);
        double zPos = shootVec.z * Math.cos(yawRadians) + shootVec.x * Math.sin(yawRadians);

        return playerVec.add(xPos, shootVec.y, zPos);
    }
}
