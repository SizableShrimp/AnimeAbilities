package me.sizableshrimp.animeabilities.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CapabilityAttacher {
    @SuppressWarnings("rawtypes")
    private static final Capability.IStorage EMPTY_STORAGE = new Capability.IStorage() {
        @Nullable
        @Override
        public INBT writeNBT(Capability capability, Object instance, Direction side) { return null; }

        @Override
        public void readNBT(Capability capability, Object instance, Direction side, INBT nbt) {}
    };

    @SuppressWarnings("unchecked")
    protected static <T> void registerCapability(Class<T> capClass) {
        CapabilityManager.INSTANCE.register(capClass, (Capability.IStorage<T>) EMPTY_STORAGE, () -> null);
    }

    protected static <I extends INBTSerializable<T>, T extends INBT> void genericAttachCapability(AttachCapabilitiesEvent<?> event, I impl, Capability<I> capability, ResourceLocation location) {
        genericAttachCapability(event, impl, capability, location, true);
    }

    protected static <I extends INBTSerializable<T>, T extends INBT> void genericAttachCapability(AttachCapabilitiesEvent<?> event, I impl, Capability<I> capability, ResourceLocation location, boolean save) {
        LazyOptional<I> storage = LazyOptional.of(() -> impl);
        ICapabilityProvider provider = getProvider(impl, storage, capability, save);
        event.addCapability(location, provider);
        event.addListener(storage::invalidate);
    }

    protected static <I extends INBTSerializable<T>, T extends INBT> ICapabilityProvider getProvider(I impl, LazyOptional<I> storage, Capability<I> capability, boolean save) {
        return save ? new ICapabilitySerializable<T>() {
            @Nonnull
            @Override
            public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> cap, @Nullable Direction side) {
                return cap == capability ? storage.cast() : LazyOptional.empty();
            }

            @Override
            public T serializeNBT() {
                return impl.serializeNBT();
            }

            @Override
            public void deserializeNBT(T nbt) {
                impl.deserializeNBT(nbt);
            }
        } : new ICapabilityProvider() {
            @Nonnull
            @Override
            public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> cap, @Nullable Direction side) {
                return cap == capability ? storage.cast() : LazyOptional.empty();
            }
        };
    }
}
