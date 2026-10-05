package dev.breiwalker.simple_voice_booster.mixin.client;

import de.maxhenkel.voicechat.voice.client.MicrophoneProcessor;
import dev.breiwalker.simple_voice_booster.SimpleVoiceBoosterConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces the manual gain path so the Boost slider always applies when the
 * {@code forceManualGain} option is enabled. When the user turns that option off,
 * Simple Voice Chat's own automatic gain control behavior is left untouched.
 */
@Mixin(MicrophoneProcessor.class)
public class MicrophoneProcessorMixin {

    @Inject(method = "useAgc", at = @At("HEAD"), cancellable = true)
    private void simple_voice_booster$forceManualGain(CallbackInfoReturnable<Boolean> cir) {
        if (SimpleVoiceBoosterConfig.get().isForceManualGain()) {
            cir.setReturnValue(false);
        }
    }

}
