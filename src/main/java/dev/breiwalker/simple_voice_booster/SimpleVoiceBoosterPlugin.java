package dev.breiwalker.simple_voice_booster;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;

/**
 * Simple Voice Chat plugin entrypoint, registered under the {@code "voicechat"}
 * entrypoint in {@code fabric.mod.json}. Registering the plugin tells Simple Voice
 * Chat this mod is aware of it and lets us hook into its API in the future.
 */
public class SimpleVoiceBoosterPlugin implements VoicechatPlugin {

    public static final String PLUGIN_ID = "simple_voice_booster";

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
    }

    @Override
    public void registerEvents(EventRegistration registration) {
    }

}
