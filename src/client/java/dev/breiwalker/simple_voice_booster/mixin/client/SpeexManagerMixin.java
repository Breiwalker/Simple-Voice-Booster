package dev.breiwalker.simple_voice_booster.mixin.client;

import de.maxhenkel.voicechat.natives.SpeexManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Simple Voice Booster always uses manual gain, so the automatic gain control
 * (AGC) option is hidden from Simple Voice Chat's settings screens.
 */
@Mixin(SpeexManager.class)
public class SpeexManagerMixin {

    @Inject(method = "canUseAgc", at = @At("HEAD"), cancellable = true)
    private static void simple_voice_booster$disableAgc(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

}
