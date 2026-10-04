package dev.breiwalker.simple_voice_booster.mixin.client;

import dev.breiwalker.simple_voice_booster.MicBoost;
import dev.breiwalker.simple_voice_booster.client.SimpleVoiceBoosterInput;
import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.gui.VoiceChatSettingsScreen;
import de.maxhenkel.voicechat.gui.widgets.MicAmplificationSlider;
import de.maxhenkel.voicechat.voice.client.VolumeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds a text input box next to Simple Voice Chat's microphone amplification
 * slider so the boost can be typed in exactly (e.g. 5000).
 */
@Mixin(VoiceChatSettingsScreen.class)
public class VoiceChatSettingsScreenMixin {

    @Inject(method = "init", at = @At("HEAD"))
    private void simple_voice_booster$resetInput(CallbackInfo ci) {
        SimpleVoiceBoosterInput.box = null;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void simple_voice_booster$addInput(CallbackInfo ci) {
        MicAmplificationSlider slider = SimpleVoiceBoosterInput.slider;
        if (slider == null) {
            return;
        }

        int boxWidth = 56;
        int width = slider.getWidth();
        if (width > boxWidth + 20) {
            slider.setWidth(width - boxWidth - 2);
        }

        int x = slider.getX() + slider.getWidth() + 2;
        int y = slider.getY();

        EditBox box = new EditBox(Minecraft.getInstance().font, x, y, boxWidth, slider.getHeight(), Component.literal("%"));
        box.setMaxLength(5);
        box.setValue(Long.toString(MicBoost.gainDbToPercent(VoicechatClient.CLIENT_CONFIG.microphoneGain.get())));
        box.setResponder(this::simple_voice_booster$onInputChanged);

        SimpleVoiceBoosterInput.box = box;
        ((ScreenInvoker) (Object) this).simpleVoiceBooster$addRenderableWidget(box);
    }

    @Unique
    private void simple_voice_booster$revertInput() {
        if (SimpleVoiceBoosterInput.box == null) {
            return;
        }
        long percent = MicBoost.gainDbToPercent(VoicechatClient.CLIENT_CONFIG.microphoneGain.get());
        SimpleVoiceBoosterInput.syncing = true;
        try {
            SimpleVoiceBoosterInput.box.setValue(Long.toString(percent));
        } finally {
            SimpleVoiceBoosterInput.syncing = false;
        }
    }

    @Unique
    private void simple_voice_booster$onInputChanged(String text) {
        if (SimpleVoiceBoosterInput.syncing) {
            return;
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        long percent;
        try {
            percent = Long.parseLong(trimmed);
        } catch (NumberFormatException e) {
            simple_voice_booster$revertInput();
            return;
        }
        percent = Math.max(1L, Math.min((long) MicBoost.MAX_AMPLIFICATION * 100L, percent));

        double gainDb = MicBoost.percentToGainDb(percent);
        VoicechatClient.CLIENT_CONFIG.microphoneGain.set(gainDb).save();

        MicAmplificationSlider slider = SimpleVoiceBoosterInput.slider;
        if (slider == null) {
            return;
        }

        double span = MicBoost.MAX_GAIN_DB - VolumeManager.MIN_GAIN;
        double value = (gainDb - VolumeManager.MIN_GAIN) / span;

        SimpleVoiceBoosterInput.syncing = true;
        try {
            ((AbstractSliderButtonAccessor) slider).simpleVoiceBooster$setValue(value);
            if (!Long.toString(percent).equals(text) && SimpleVoiceBoosterInput.box != null) {
                SimpleVoiceBoosterInput.box.setValue(Long.toString(percent));
            }
        } finally {
            SimpleVoiceBoosterInput.syncing = false;
        }
    }

}
