// package me.sizableshrimp.animeabilities.mixin;
//
// import me.sizableshrimp.animeabilities.Registration;
// import net.minecraft.entity.Entity;
// import net.minecraft.entity.EntityType;
// import net.minecraft.entity.LivingEntity;
// import net.minecraft.entity.player.PlayerEntity;
// import net.minecraft.world.World;
// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.ModifyVariable;
//
// @Mixin(LivingEntity.class)
// public abstract class LivingEntityMixin extends Entity {
//     private LivingEntityMixin(EntityType<?> p_i48580_1_, World p_i48580_2_) {
//         super(p_i48580_1_, p_i48580_2_);
//     }
//
//     @ModifyVariable(method = "updateFallFlying", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClientSide:Z"))
//     private boolean animeabilities$injectUpdateFallFlying(boolean flag) {
//         return flag || (((Object) this instanceof PlayerEntity) && Registration.DRAGON_BALL.get().hasThisAbility((PlayerEntity) (Object) this) && ((PlayerEntity) (Object) this).isFallFlying());
//     }
//
//     // @Redirect(method = "travel",
//     //         at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasEffect(Lnet/minecraft/potion/Effect;)Z"),
//     //         slice = @Slice(to = @At(value = "INVOKE",
//     //                 target = "Lnet/minecraft/entity/ai/attributes/ModifiableAttributeInstance;hasModifier(Lnet/minecraft/entity/ai/attributes/AttributeModifier;)Z")))
//     // private boolean animeabilities$injectTravel(LivingEntity self, Effect effect) {
//     //     return self.hasEffect(effect) || (effect == Effects.SLOW_FALLING
//     //             && self instanceof PlayerEntity
//     //             && !((PlayerEntity) self).abilities.mayfly
//     //             && Registration.DRAGON_BALL.get().hasThisAbility((PlayerEntity) self)
//     //             && this.getY() > this.level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, this.blockPosition()).getY() + 3);
//     // }
// }
