package dev.breiwalker.simple_voice_booster.mixin.client;

import dev.breiwalker.simple_voice_booster.MicBoost;
import dev.breiwalker.simple_voice_booster.client.SimpleVoiceBoosterInput;
import de.maxhenkel.voicechat.gui.widgets.MicAmplificationSlider;
import de.maxhenkel.voicechat.voice.client.VolumeManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes Simple Voice Chat's built-in microphone amplification slider span the
 * extended range (up to 5000%), shows the value as a percentage instead of dB,
 * and keeps the text input box in sync.
 */
@Mixin(MicAmplificationSlider.class)
public class MicAmplificationSliderMixin {

    // The slider maps its 0..1 value to a gain across the span
    // (MAX_GAIN - MIN_GAIN), which javac folds into the single constant 64.0
    // (24 - -40). We widen that folded span to the user's configured maximum.
    @ModifyConstant(method = "gainToValue", constant = @Constant(doubleValue = 64.0D))
    private static double simple_voice_booster$gainToValueSpan(double original) {
        return simple_voice_booster$span();
    }

    @ModifyConstant(method = "valueToGain", constant = @Constant(doubleValue = 64.0D))
    private static double simple_voice_booster$valueToGainSpan(double original) {
        return simple_voice_booster$span();
    }

    /**
     * The dB range the slider spans, derived from the user's configured maximum.
     * Guaranteed positive so the slider direction never inverts.
     */
    @Unique
    private static double simple_voice_booster$span() {
        double span = MicBoost.maxGainDb() - VolumeManager.MIN_GAIN;
        return span > 1.0E-6D ? span : 1.0E-6D;
    }

    @Redirect(
            method = "updateMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    private MutableComponent simple_voice_booster$percentageMessage(String key, Object[] args) {
        long percent = simple_voice_booster$percentForValue(
                ((AbstractSliderButtonAccessor) (Object) this).simpleVoiceBooster$getValue());
        return Component.translatable("message.simple_voice_booster.microphone_amplification", percent + "%");
    }

    /**
     * Replaces Simple Voice Chat's clipping warning tooltip with our own message
     * once the boost reaches 200%.
     */
    @Redirect(
            method = "updateMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lde/maxhenkel/voicechat/gui/widgets/MicAmplificationSlider;setTooltip(Lnet/minecraft/client/gui/components/Tooltip;)V"
            )
    )
    private void simple_voice_booster$tooltip(MicAmplificationSlider slider, Tooltip original) {
        long percent = simple_voice_booster$percentForValue(
                ((AbstractSliderButtonAccessor) (Object) slider).simpleVoiceBooster$getValue());
        AbstractWidget self = (AbstractWidget) (Object) slider;
        if (self.active && percent >= 200L) {
            self.setTooltip(Tooltip.create(
                    Component.translatable("message.simple_voice_booster.boosted").withStyle(ChatFormatting.RED)));
        } else {
            self.setTooltip(original);
        }
    }

    /**
     * Registers the newest slider so the text input box can drive it.
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void simple_voice_booster$registerSlider(CallbackInfo ci) {
        SimpleVoiceBoosterInput.slider = (MicAmplificationSlider) (Object) this;
    }

    /**
     * Pushes the slider value into the text box whenever the slider changes.
     */
    @Inject(method = "updateMessage", at = @At("TAIL"))
    private void simple_voice_booster$syncInputBox(CallbackInfo ci) {
        if (SimpleVoiceBoosterInput.syncing || SimpleVoiceBoosterInput.box == null || SimpleVoiceBoosterInput.box.isFocused()) {
            return;
        }
        long percent = simple_voice_booster$percentForValue(
                ((AbstractSliderButtonAccessor) (Object) this).simpleVoiceBooster$getValue());
        SimpleVoiceBoosterInput.syncing = true;
        try {
            SimpleVoiceBoosterInput.box.setValue(Long.toString(percent));
        } finally {
            SimpleVoiceBoosterInput.syncing = false;
        }
    }

    @Unique
    private static long simple_voice_booster$percentForValue(double value) {
        double span = simple_voice_booster$span();
        double gainDb = value * span + VolumeManager.MIN_GAIN;
        return MicBoost.gainDbToPercent(gainDb);
    }

}
