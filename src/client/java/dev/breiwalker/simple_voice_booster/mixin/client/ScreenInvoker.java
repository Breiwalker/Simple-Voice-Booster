package dev.breiwalker.simple_voice_booster.mixin.client;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Invokes {@link Screen#addRenderableWidget} from mixins that target screens
 * (an inherited method cannot be {@code @Shadow}-ed on the subclass).
 */
@Mixin(Screen.class)
public interface ScreenInvoker {

    @Invoker("addRenderableWidget")
    GuiEventListener simpleVoiceBooster$addRenderableWidget(GuiEventListener widget);

}
