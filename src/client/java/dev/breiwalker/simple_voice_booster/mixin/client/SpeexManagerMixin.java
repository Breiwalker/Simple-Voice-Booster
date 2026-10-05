package dev.breiwalker.simple_voice_booster.mixin.client;

import de.maxhenkel.voicechat.natives.SpeexManager;
import dev.breiwalker.simple_voice_booster.SimpleVoiceBoosterConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hides the automatic gain control (AGC) option while {@code forceManualGain} is
 * enabled, because manual gain is forced in that mode. When the user disables the
 * override, the option is shown again and works normally.
 */
@Mixin(SpeexManager.class)
public class SpeexManagerMixin {

    @Inject(method = "canUseAgc", at = @At("HEAD"), cancellable = true)
    private static void simple_voice_booster$disableAgc(CallbackInfoReturnable<Boolean> cir) {
        if (SimpleVoiceBoosterConfig.get().isForceManualGain()) {
            cir.setReturnValue(false);
        }
    }

}
