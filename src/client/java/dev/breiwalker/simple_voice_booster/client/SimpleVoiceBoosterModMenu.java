package dev.breiwalker.simple_voice_booster.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.maxhenkel.voicechat.gui.VoiceChatSettingsScreen;

/**
 * Mod Menu integration. The "Configure" button opens Simple Voice Chat's audio
 * settings, which is where the Boost slider and the typed input box live.
 */
public class SimpleVoiceBoosterModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new VoiceChatSettingsScreen(parent);
    }

}
