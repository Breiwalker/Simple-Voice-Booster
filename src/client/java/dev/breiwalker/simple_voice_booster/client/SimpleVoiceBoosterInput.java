package dev.breiwalker.simple_voice_booster.client;

import de.maxhenkel.voicechat.gui.widgets.MicAmplificationSlider;
import net.minecraft.client.gui.components.EditBox;

/**
 * Shared state between the patched {@link MicAmplificationSlider} and the text
 * input box we add next to it, so the slider and the typed value stay in sync.
 */
public final class SimpleVoiceBoosterInput {

    /** The most recently created amplification slider (one per settings screen). */
    public static MicAmplificationSlider slider;

    /** The text box attached to {@link #slider}, if the settings screen is open. */
    public static EditBox box;

    /** Guards against slider &lt;-&gt; text box update loops. */
    public static boolean syncing;

    private SimpleVoiceBoosterInput() {
    }

}
