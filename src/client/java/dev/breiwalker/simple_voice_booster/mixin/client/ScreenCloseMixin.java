package dev.breiwalker.simple_voice_booster.mixin.client;

import de.maxhenkel.voicechat.gui.VoiceChatSettingsScreen;
import dev.breiwalker.simple_voice_booster.client.SimpleVoiceBoosterInput;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Clears the shared slider/text-box references once Simple Voice Chat's audio
 * settings screen is closed, so the added input box does not outlive its screen.
 * <p>
 * The callback lives on {@link Screen} because {@code VoiceChatSettingsScreen} does
 * not override {@code removed()}.
 */
@Mixin(Screen.class)
public class ScreenCloseMixin {

    @Inject(method = "removed", at = @At("HEAD"))
    private void simple_voice_booster$clearInputOnRemoved(CallbackInfo ci) {
        if ((Object) this instanceof VoiceChatSettingsScreen) {
            SimpleVoiceBoosterInput.slider = null;
            SimpleVoiceBoosterInput.box = null;
        }
    }

}
