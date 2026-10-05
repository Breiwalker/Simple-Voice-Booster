package dev.breiwalker.simple_voice_booster.mixin.client;

import dev.breiwalker.simple_voice_booster.MicBoost;
import de.maxhenkel.voicechat.config.ClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Extends the upper bound of the {@code microphone_gain} config entry to the
 * absolute ceiling {@link MicBoost#absoluteMaxGainDb()} (20000%). The user's own
 * maximum is enforced dynamically in the slider span, the typed box and the gain
 * path, so this bound never needs runtime mutation.
 * <p>
 * Simple Voice Chat's {@code VolumeManager.MAX_GAIN} is a compile-time constant, so
 * its value is inlined at every use site. We therefore patch the inlined constant in
 * the config entry definition instead of the field itself.
 */
@Mixin(ClientConfig.class)
public class ClientConfigMixin {

    @ModifyConstant(method = "<init>", constant = @Constant(doubleValue = 24.0D))
    private double simple_voice_booster$extendMicrophoneGainMax(double original) {
        return MicBoost.absoluteMaxGainDb();
    }

}
