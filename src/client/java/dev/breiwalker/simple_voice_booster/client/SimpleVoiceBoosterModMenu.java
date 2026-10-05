package dev.breiwalker.simple_voice_booster.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Mod Menu integration. The "Configure" button opens Simple Voice Booster's own
 * config screen, which links on to Simple Voice Chat's audio settings.
 */
public class SimpleVoiceBoosterModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new SimpleVoiceBoosterConfigScreen(parent);
    }

}
