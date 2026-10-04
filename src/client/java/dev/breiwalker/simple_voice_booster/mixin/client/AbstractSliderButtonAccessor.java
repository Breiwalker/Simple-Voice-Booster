package dev.breiwalker.simple_voice_booster.mixin.client;

import net.minecraft.client.gui.components.AbstractSliderButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Exposes {@link AbstractSliderButton}'s protected {@code value} field and
 * {@code setValue(double)} method to the text input integration.
 */
@Mixin(AbstractSliderButton.class)
public interface AbstractSliderButtonAccessor {

    @Accessor("value")
    double simpleVoiceBooster$getValue();

    @Invoker("setValue")
    void simpleVoiceBooster$setValue(double value);

}
