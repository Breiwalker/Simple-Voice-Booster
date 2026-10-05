package dev.breiwalker.simple_voice_booster;

import net.fabricmc.api.ModInitializer;

public class Simple_voice_booster implements ModInitializer {

    @Override
    public void onInitialize() {
        SimpleVoiceBoosterConfig.load();
    }
}
