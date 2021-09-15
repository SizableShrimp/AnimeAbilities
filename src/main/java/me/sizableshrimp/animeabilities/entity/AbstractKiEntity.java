package me.sizableshrimp.animeabilities.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class AbstractKiEntity extends ProjectileEntity implements IEntityAdditionalSpawnData {
    protected AbstractKiEntity(EntityType<? extends AbstractKiEntity> type, World world) {
        super(type, world);
    }

    protected AbstractKiEntity(EntityType<? extends AbstractKiEntity> type, LivingEntity living, float velocity, float inaccuracy) {
        this(type, living.level);
        Vector3d vec = getRotatedShootVec(living);
        this.setPos(vec.x, vec.y, vec.z);
        this.setOwner(living);
        this.shootFromRotation(living, living.xRot, living.yRot, 0.0F, velocity, inaccuracy);
    }

    @Override
    public void baseTick() {
        if (!this.isAlive() || tickDespawn())
            return;
        if (removeWhen()) {
            this.remove();
            return;
        }

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

    protected boolean removeWhen() {
        return removeWhenInBlock();
    }

    protected boolean removeWhenInBlock() {
        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockpos);
        //noinspection deprecation
        if (!blockstate.isAir(this.level, blockpos)) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
            if (!voxelshape.isEmpty()) {
                Vector3d vector3d1 = this.position();

                for (AxisAlignedBB axisalignedbb : voxelshape.toAabbs()) {
                    if (axisalignedbb.move(blockpos).contains(vector3d1)) {
                        this.onCollide();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected boolean tickDespawn() {
        ++this.tickCount;
        if (tickCount >= getTickDespawnCount()) {
            this.remove();
            return true;
        }
        return false;
    }

    protected int getTickDespawnCount() {
        return 1200;
    }

    protected void updateMotion(Vector3d motion, Vector3d future) {
        // this.setDeltaMovement(motion.multiply(0.9F, 0.9F, 0.9F).subtract(0, 0.02D, 0)); // Slow down gradually
        this.setPos(future.x, future.y, future.z);
    }

    // Copied from AbstractArrowEntity
    protected void calcHit(Vector3d pos, Vector3d future) {
        EntityRayTraceResult entityTrace = this.findHitEntity(pos, future);

        if (entityTrace != null) {
            Entity target = entityTrace.getEntity();
            if (target instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) target.getEntity();
                Entity owner = this.getOwner();
                if (player.abilities.invulnerable || owner instanceof PlayerEntity && !((PlayerEntity) owner).canHarmPlayer(player)) {
                    return;
                }
            }

            this.onHitEntity(entityTrace);
        }
    }

    @Nullable
    protected EntityRayTraceResult findHitEntity(Vector3d startVec, Vector3d endVec) {
        return ProjectileHelper.getEntityHitResult(this.level, this, startVec, endVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        this.onCollide();
        this.remove();
    }

    protected void onCollide() {}

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
    public void writeSpawnData(PacketBuffer buf) {
        Entity owner = this.getOwner();
        buf.writeBoolean(owner != null);
        if (owner != null) {
            buf.writeInt(owner.getId());
        }
    }

    @Override
    public void readSpawnData(PacketBuffer buf) {
        if (buf.readBoolean()) {
            int ownerId = buf.readInt();
            Entity owner = this.level.getEntity(ownerId);
            if (owner != null)
                this.setOwner(owner);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected static Vector3d getRotatedShootVec(LivingEntity living) {
        Vector3d playerVec = new Vector3d(living.getX(), living.getEyeY() - 0.1D, living.getZ());
        Vector3d shootVec = new Vector3d(1, 1, 1).multiply(living.getLookAngle());

        double yawRadians = Math.toRadians(6);
        double xPos = shootVec.x * Math.cos(yawRadians) - shootVec.z * Math.sin(yawRadians);
        double zPos = shootVec.z * Math.cos(yawRadians) + shootVec.x * Math.sin(yawRadians);

        return playerVec.add(xPos, shootVec.y, zPos);
    }
}
