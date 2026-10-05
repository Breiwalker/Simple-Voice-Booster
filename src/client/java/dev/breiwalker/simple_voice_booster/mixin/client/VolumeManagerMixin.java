package dev.breiwalker.simple_voice_booster.mixin.client;

import de.maxhenkel.voicechat.voice.client.VolumeManager;
import de.maxhenkel.voicechat.voice.common.AudioUtils;
import dev.breiwalker.simple_voice_booster.MicBoost;
import dev.breiwalker.simple_voice_booster.SimpleVoiceBoosterConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces Simple Voice Chat's microphone volume adjustment when {@code hardClip} is
 * enabled.
 * <p>
 * The vanilla implementation keeps a rolling maximum and clamps the applied gain so
 * the audio never clips. That clamp means high slider values (e.g. 5000%) would not
 * actually be applied to loud input. With {@code hardClip} we apply the requested gain
 * (capped to the user's configured maximum) and hard-clip the samples to the 16-bit
 * range instead, which is what a boost this aggressive is expected to sound like.
 * <p>
 * When {@code hardClip} is disabled we return without cancelling so Simple Voice Chat
 * runs its original anti-clip guard untouched.
 */
@Mixin(VolumeManager.class)
public class VolumeManagerMixin {

    @Inject(method = "adjustVolume", at = @At("HEAD"), cancellable = true)
    private void simple_voice_booster$adjustVolume(short[] audio, double gainDb, CallbackInfo ci) {
        if (!SimpleVoiceBoosterConfig.get().isHardClip()) {
            return;
        }

        double appliedGain = Math.min(gainDb, MicBoost.maxGainDb());
        double multiplier;
        if (appliedGain <= VolumeManager.MIN_GAIN) {
            multiplier = 0.0D;
        } else {
            multiplier = AudioUtils.dbToLinear(appliedGain);
        }

        for (int i = 0; i < audio.length; i++) {
            double value = audio[i] * multiplier;
            if (value > Short.MAX_VALUE) {
                value = Short.MAX_VALUE;
            } else if (value < Short.MIN_VALUE) {
                value = Short.MIN_VALUE;
            }
            audio[i] = (short) value;
        }

        ci.cancel();
    }

}
