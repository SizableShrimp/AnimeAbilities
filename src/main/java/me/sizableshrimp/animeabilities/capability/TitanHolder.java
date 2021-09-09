package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.item.AttackOnTitanItem;
import me.sizableshrimp.animeabilities.network.TitanStatusPacket;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TitanHolder extends PlayerCapability {
    public enum Type {
        FIGHTING(8, p -> true),
        ARMORED(32, p -> Registration.ATTACK_ON_TITAN.get().hasUpgrade(p, AttackOnTitanItem.UpgradeType.ARMORED)),
        COLOSSAL(64, p -> Registration.ATTACK_ON_TITAN.get().hasUpgrade(p, AttackOnTitanItem.UpgradeType.COLOSSAL));

        private final float scale;
        private final EntitySize size;
        private final Predicate<PlayerEntity> canSwitch;

        Type(float scale, Predicate<PlayerEntity> canSwitch) {
            this.scale = scale;
            this.size = EntityType.PLAYER.getDimensions().scale(scale);
            this.canSwitch = canSwitch;
        }

        public float getScale() {
            return scale;
        }

        public EntitySize getSize() {
            return size;
        }

        public static List<Type> getAllAvailable(PlayerEntity player) {
            return Arrays.stream(values()).filter(t -> t.canSwitch.test(player)).collect(Collectors.toList());
        }
    }

    private Type type;

    public TitanHolder(PlayerEntity player) {
        super(player);
    }

    @Nullable
    public Type getType() {
        return type;
    }

    public void setType(@Nullable Type type, boolean sync) {
        this.type = type;
        if (sync)
            this.updateTracking();
    }

    @Override
    public TitanStatusPacket createUpdatePacket() {
        return new TitanStatusPacket(player.getId(), this);
    }

    @Override
    public CompoundNBT serializeNBT(boolean savingToDisk) {
        CompoundNBT tag = new CompoundNBT();
        if (type != null)
            tag.putString("Type", type.name());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt, boolean readingFromDisk) {
        if (nbt.contains("Type", Constants.NBT.TAG_STRING)) {
            this.type = Type.valueOf(nbt.getString("Type"));
        } else {
            this.type = null;
        }
        player.refreshDimensions();
    }
}
