package me.sizableshrimp.animeabilities.capability;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import me.sizableshrimp.animeabilities.network.KiStatusPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.UUID;

public class KiHolder extends PlayerCapability {
    private float ki = 500;
    private float maxKi = 500;
    private int kamehamehaRemainingAnimation = 0;
    private int kamehamehaActiveDuration = 0;
    private float kamehamehaDamage = 0F;
    private int kiBlastLastUsedTime = 0;
    private final Reference2IntMap<UUID> kamehamehaHitCooldownMap;

    public KiHolder(PlayerEntity player) {
        super(player);
        kamehamehaHitCooldownMap = player.level.isClientSide ? null : new Reference2IntOpenHashMap<>();
    }

    public float getKi() {
        return ki;
    }

    public void setKi(float ki) {
        this.ki = ki;
        this.updateTracking();
    }

    public float getMaxKi() {
        return maxKi;
    }

    public void setMaxKi(float maxKi) {
        this.maxKi = maxKi;
        this.updateTracking();
    }

    public int getKamehamehaRemainingAnimation() {
        return kamehamehaRemainingAnimation;
    }

    public int getUsedKamehamehaAnimation() {
        return isUsingKamehameha() ? getMaxKamehamehaAnimation() - getKamehamehaRemainingAnimation() : -1;
    }

    public boolean isUsingKamehameha() {
        return kamehamehaRemainingAnimation > 0;
    }

    public void setKamehamehaRemainingAnimation(int kamehamehaRemainingAnimation, boolean sync) {
        this.kamehamehaRemainingAnimation = kamehamehaRemainingAnimation;
        if (sync)
            this.updateTracking();
    }

    public int getMinKamehamehaAnimation() {
        return 80;
    }

    public int getMaxKamehamehaAnimation() {
        return 480;
    }

    public int getKamehamehaActiveDuration() {
        return kamehamehaActiveDuration;
    }

    public boolean isKamehamehaActive() {
        return kamehamehaActiveDuration > 0;
    }

    public void setKamehamehaActiveDuration(int kamehamehaActiveDuration, boolean sync) {
        this.kamehamehaActiveDuration = kamehamehaActiveDuration;
        if (sync)
            this.updateTracking();
    }

    public int getMaxKamehamehaActiveDuration() {
        return 160;
    }

    public float getKamehamehaDamage() {
        return kamehamehaDamage;
    }

    public void setKamehamehaDamage(float kamehamehaDamage, boolean sync) {
        this.kamehamehaDamage = kamehamehaDamage;
        if (sync)
            this.updateTracking();
    }

    public int getKiBlastLastUsedTime() {
        return kiBlastLastUsedTime;
    }

    public void setKiBlastLastUsedTime(int kiBlastLastUsedTime, boolean sync) {
        this.kiBlastLastUsedTime = kiBlastLastUsedTime;
        if (sync)
            this.updateTracking();
    }

    public boolean isPlayerOnKiBlastCooldown() {
        return kiBlastLastUsedTime + getKiBlastCooldownDuration() > player.tickCount;
    }

    public int getKiBlastCooldownDuration() {
        return 20;
    }

    public Reference2IntMap<UUID> getKamehamehaHitCooldownMap() {
        return kamehamehaHitCooldownMap;
    }

    @Override
    public KiStatusPacket createUpdatePacket() {
        return new KiStatusPacket(player.getId(), this);
    }

    @Override
    public CompoundNBT serializeNBT(boolean savingToDisk) {
        CompoundNBT tag = new CompoundNBT();
        tag.putFloat("Ki", ki);
        tag.putFloat("MaxKi", maxKi);
        if (!savingToDisk) {
            tag.putInt("KamehamehaAnimation", kamehamehaRemainingAnimation);
            tag.putInt("KamehamehaActiveDuration", kamehamehaActiveDuration);
            tag.putFloat("KamehamehaDamage", kamehamehaDamage);
            tag.putInt("KiBlastLastUsedTime", kiBlastLastUsedTime);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt, boolean readingFromDisk) {
        this.ki = nbt.getFloat("Ki");
        this.maxKi = nbt.getFloat("MaxKi");
        if (!readingFromDisk) {
            this.kamehamehaRemainingAnimation = nbt.getInt("KamehamehaAnimation");
            this.kamehamehaActiveDuration = nbt.getInt("KamehamehaActiveDuration");
            this.kamehamehaDamage = nbt.getFloat("KamehamehaDamage");
            this.kiBlastLastUsedTime = nbt.getInt("KiBlastLastUsedTime");
        }
    }
}
