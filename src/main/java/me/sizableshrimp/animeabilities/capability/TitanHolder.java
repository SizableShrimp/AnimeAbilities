package me.sizableshrimp.animeabilities.capability;

import me.sizableshrimp.animeabilities.network.TitanStatusPacket;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TitanHolder extends PlayerCapability {
    public enum Type {
        FIGHTING(8, 8, p -> true, new AttributeModifier(UUID.fromString("c75fb898-b10f-4258-bd2b-19b4928c24fb"), "Fighting titan speed boost", 0.3, AttributeModifier.Operation.MULTIPLY_TOTAL))/*,
        ARMORED(16, 8, p -> Registration.ATTACK_ON_TITAN.get().hasUpgrade(p, AttackOnTitanItem.UpgradeType.ARMORED)),
        COLOSSAL(32, 8, p -> Registration.ATTACK_ON_TITAN.get().hasUpgrade(p, AttackOnTitanItem.UpgradeType.COLOSSAL))*/;

        private final float scale;
        private final float jumpScale;
        private final EntitySize size;
        private final Predicate<PlayerEntity> canSwitch;
        private final AttributeModifier speedModifier;

        Type(float scale, float jumpScale, Predicate<PlayerEntity> canSwitch, @Nullable AttributeModifier speedModifier) {
            this.scale = scale;
            this.jumpScale = jumpScale;
            this.size = EntityType.PLAYER.getDimensions().scale(scale);
            this.canSwitch = canSwitch;
            this.speedModifier = speedModifier;
        }

        public float getScale() {
            return scale;
        }

        public float getJumpScale() {
            return jumpScale;
        }

        public EntitySize getSize() {
            return size;
        }

        @Nullable
        public AttributeModifier getSpeedModifier() {
            return speedModifier;
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
        ModifiableAttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && this.type != null && this.type.getSpeedModifier() != null) {
            UUID currentModifierId = this.type.getSpeedModifier().getId();
            if (speedAttribute.getModifier(currentModifierId) != null) {
                speedAttribute.removeModifier(currentModifierId);
            }
        }

        this.type = type;

        if (speedAttribute != null && this.type != null && this.type.getSpeedModifier() != null) {
            speedAttribute.addTransientModifier(this.type.getSpeedModifier());
        }

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
            this.setType(Type.valueOf(nbt.getString("Type")), false);
        } else {
            this.setType(null, false);
        }
        refreshPlayerTitan();
    }

    public void refreshPlayerTitan() {
        player.maxUpStep = this.type == null ? 0.6F : this.type.getJumpScale();
        player.refreshDimensions();
    }
}
