package dev.breiwalker.simple_voice_booster.mixin.client;

import dev.breiwalker.simple_voice_booster.MicBoost;
import de.maxhenkel.voicechat.config.ClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Extends the upper bound of the {@code microphone_gain} config entry so the
 * microphone amplification slider can be dragged up to {@link MicBoost#MAX_GAIN_DB}
 * (~5000%).
 * <p>
 * Simple Voice Chat's {@code VolumeManager.MAX_GAIN} is a compile-time constant, so
 * its value is inlined at every use site. We therefore patch the inlined constant in
 * the config entry definition instead of the field itself.
 */
@Mixin(ClientConfig.class)
public class ClientConfigMixin {

    @ModifyConstant(method = "<init>", constant = @Constant(doubleValue = 24.0D))
    private double simple_voice_booster$extendMicrophoneGainMax(double original) {
        return MicBoost.MAX_GAIN_DB;
    }

}
