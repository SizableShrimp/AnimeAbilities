package me.sizableshrimp.animeabilities.item;

import me.sizableshrimp.animeabilities.capability.AbilityHolderCapability;
import me.sizableshrimp.animeabilities.container.AbilityContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class AbilityItem extends Item {
    private static final INamedContainerProvider ABILITIES_CONTAINER_PROVIDER = new INamedContainerProvider() {
        @Override
        public ITextComponent getDisplayName() {
            return new StringTextComponent("Abilities");
        }

        @Override
        public Container createMenu(int containerId, PlayerInventory playerInv, PlayerEntity player) {
            return new AbilityContainer(containerId, playerInv);
        }
    };

    public AbilityItem(Properties properties) {
        super(properties);
    }

    public boolean hasThisAbility(PlayerEntity player) {
        return findAbilityItem(player, this).map(this::isEnabled).orElse(false);
    }

    public boolean isEnabled(ItemStack stack) {
        return !stack.hasTag() || !stack.getTag().getBoolean("Disabled");
    }

    public void setEnabled(ItemStack stack, boolean enabled) {
        if (enabled && stack.hasTag()) {
            stack.getTag().remove("Disabled");
        } else if (!enabled) {
            stack.getOrCreateTag().putBoolean("Disabled", true);
        }
    }

    public static Optional<ItemStack> findAbilityItem(PlayerEntity player, Item item) {
        ItemStack playerInvStack = findItem(player.inventory, item);
        return playerInvStack != null
                ? Optional.of(playerInvStack)
                : AbilityHolderCapability.getAbilityHolder(player).resolve().map(abilityHolder -> findItem(abilityHolder.getAbilities(), item));
    }

    public static ItemStack findItem(IInventory inv, Item item) {
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem().equals(item))
                return stack;
        }

        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltipComponents, ITooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        ITextComponent onOff = getEnabledComponent(stack, "ON", "OFF");
        tooltipComponents.add(new StringTextComponent("Ability: ").withStyle(s -> s.withColor(TextFormatting.GRAY)).append(onOff));
    }

    protected IFormattableTextComponent getEnabledComponent(ItemStack stack, String enabledText, String disabledText) {
        return isEnabled(stack)
                ? new StringTextComponent(enabledText).withStyle(s -> s.withColor(TextFormatting.GREEN))
                : new StringTextComponent(disabledText).withStyle(s -> s.withColor(TextFormatting.RED));
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            this.setEnabled(stack, !this.isEnabled(stack));
            ITextComponent enabledComponent = getEnabledComponent(stack, "Enabled", "Disabled");
            player.displayClientMessage(new StringTextComponent("Ability: ").withStyle(s -> s.withColor(TextFormatting.GRAY)).append(enabledComponent), true);
            // openAbilitiesContainer((ServerPlayerEntity) player);
        }

        return ActionResult.sidedSuccess(stack, level.isClientSide);
    }

    public static void openAbilitiesContainer(ServerPlayerEntity player) {
        NetworkHooks.openGui(player, ABILITIES_CONTAINER_PROVIDER);
    }
}
