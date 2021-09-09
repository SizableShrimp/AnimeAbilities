package me.sizableshrimp.animeabilities.client.sound;

import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import me.sizableshrimp.animeabilities.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;

import java.util.function.Predicate;

public class KiChargeSound extends TickableSound {
    private final Minecraft mc = Minecraft.getInstance();
    private final Predicate<PlayerEntity> continuePred;

    public KiChargeSound(Predicate<PlayerEntity> continuePred, float volume) {
        super(Registration.KI_CHARGE_SOUND.get(), SoundCategory.PLAYERS);
        this.delay = 0;
        this.looping = true;
        this.volume = volume;
        this.continuePred = continuePred;
    }

    @Override
    public void tick() {
        if (mc.player != null && mc.player.isAlive() && Registration.DRAGON_BALL.get().hasThisAbility(mc.player) && continuePred.test(mc.player)) {
            this.x = mc.player.getX();
            this.y = mc.player.getY();
            this.z = mc.player.getZ();
        } else {
            this.stop();
        }
    }
}
