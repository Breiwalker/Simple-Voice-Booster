package dev.breiwalker.simple_voice_booster.mixin.client;

import de.maxhenkel.voicechat.voice.client.MicrophoneProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces the manual gain path so the Boost slider always applies, even if the
 * automatic gain control config value is still enabled.
 */
@Mixin(MicrophoneProcessor.class)
public class MicrophoneProcessorMixin {

    @Inject(method = "useAgc", at = @At("HEAD"), cancellable = true)
    private void simple_voice_booster$forceManualGain(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

}
