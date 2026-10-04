package dev.breiwalker.simple_voice_booster.mixin.client;

import de.maxhenkel.voicechat.voice.client.VolumeManager;
import de.maxhenkel.voicechat.voice.common.AudioUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Replaces Simple Voice Chat's microphone volume adjustment.
 * <p>
 * The vanilla implementation keeps a rolling maximum and clamps the applied gain so
 * the audio never clips. That clamp means high slider values (e.g. 5000%) would not
 * actually be applied to loud input. Here we apply the requested gain unconditionally
 * and hard-clip the samples to the 16-bit range instead, which is what a boost this
 * aggressive is expected to sound like.
 */
@Mixin(VolumeManager.class)
public class VolumeManagerMixin {

    /**
     * @author Simple Voice Booster
     * @reason Allow the microphone gain to exceed the clipping guard so the extended
     * slider range is actually applied.
     */
    @Overwrite
    public void adjustVolume(short[] audio, double gainDb) {
        double multiplier;
        if (gainDb <= VolumeManager.MIN_GAIN) {
            multiplier = 0.0D;
        } else {
            multiplier = AudioUtils.dbToLinear(gainDb);
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
    }

}
