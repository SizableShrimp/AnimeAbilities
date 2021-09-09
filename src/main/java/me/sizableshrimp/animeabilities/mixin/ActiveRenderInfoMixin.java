package me.sizableshrimp.animeabilities.mixin;

import me.sizableshrimp.animeabilities.capability.TitanHolder;
import me.sizableshrimp.animeabilities.capability.TitanHolderCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ActiveRenderInfo.class)
public class ActiveRenderInfoMixin {
    @ModifyConstant(method = "setup", constant = @Constant(doubleValue = 4.0D))
    private double animeabilities$modifyConstantSetup(double startingDistance, IBlockReader level, Entity renderViewEntity, boolean thirdPerson, boolean thirdPersonReverse, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (renderViewEntity != mc.player || mc.player == null)
            return startingDistance;

        return TitanHolderCapability.getTitanHolder(mc.player).resolve().map(TitanHolder::getType).map(titanType -> titanType.getScale() * startingDistance).orElse(startingDistance);
    }
}
