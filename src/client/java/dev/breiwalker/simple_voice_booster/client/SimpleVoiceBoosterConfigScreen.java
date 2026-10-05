package dev.breiwalker.simple_voice_booster.client;

import de.maxhenkel.voicechat.gui.VoiceChatSettingsScreen;
import dev.breiwalker.simple_voice_booster.SimpleVoiceBoosterConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Simple Voice Booster's own config screen, reached from Mod Menu's "Configure"
 * button. Exposes the three options stored in {@link SimpleVoiceBoosterConfig}
 * and a shortcut into Simple Voice Chat's audio settings.
 */
public class SimpleVoiceBoosterConfigScreen extends Screen {

    private static final int WIDGET_WIDTH = 200;
    private static final int WIDGET_HEIGHT = 20;
    private static final int WIDGET_SPACING = 24;

    private final Screen parent;

    public SimpleVoiceBoosterConfigScreen(Screen parent) {
        super(Component.translatable("screen.simple_voice_booster.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SimpleVoiceBoosterConfig config = SimpleVoiceBoosterConfig.get();

        int x = this.width / 2 - WIDGET_WIDTH / 2;
        int y = this.height / 4;

        this.addRenderableWidget(new MaxBoostSlider(x, y, WIDGET_WIDTH, WIDGET_HEIGHT, config.getMaxBoostPercent()));
        y += WIDGET_SPACING;

        this.addRenderableWidget(CycleButton.booleanBuilder(
                        Component.translatable("options.on"),
                        Component.translatable("options.off"),
                        config.isForceManualGain())
                .create(x, y, WIDGET_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("option.simple_voice_booster.force_manual_gain"),
                        (button, value) -> {
                            config.setForceManualGain(value);
                            config.save();
                        }));
        y += WIDGET_SPACING;

        this.addRenderableWidget(CycleButton.booleanBuilder(
                        Component.translatable("options.on"),
                        Component.translatable("options.off"),
                        config.isHardClip())
                .create(x, y, WIDGET_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("option.simple_voice_booster.hard_clip"),
                        (button, value) -> {
                            config.setHardClip(value);
                            config.save();
                        }));
        y += WIDGET_SPACING;

        this.addRenderableWidget(Button.builder(
                        Component.translatable("option.simple_voice_booster.open_voice_settings"),
                        button -> this.minecraft.setScreenAndShow(new VoiceChatSettingsScreen(this)))
                .bounds(x, y, WIDGET_WIDTH, WIDGET_HEIGHT)
                .build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.onClose())
                .bounds(x, this.height - 27, WIDGET_WIDTH, WIDGET_HEIGHT)
                .build());
    }

    @Override
    public void onClose() {
        SimpleVoiceBoosterConfig.get().save();
        this.minecraft.setScreenAndShow(this.parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(extractor, mouseX, mouseY, partialTick);
        extractor.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    /**
     * Maps the slider's 0..1 value onto the full allowed boost range
     * ({@link SimpleVoiceBoosterConfig#MIN_BOOST_PERCENT}..{@link SimpleVoiceBoosterConfig#MAX_BOOST_PERCENT}).
     */
    private static class MaxBoostSlider extends AbstractSliderButton {

        MaxBoostSlider(int x, int y, int width, int height, int initialPercent) {
            super(x, y, width, height, Component.empty(), percentToValue(initialPercent));
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable(
                    "option.simple_voice_booster.max_boost", valueToPercent(this.value) + "%"));
        }

        @Override
        protected void applyValue() {
            SimpleVoiceBoosterConfig config = SimpleVoiceBoosterConfig.get();
            config.setMaxBoostPercent(valueToPercent(this.value));
            config.save();
        }

        private static double percentToValue(int percent) {
            int clamped = SimpleVoiceBoosterConfig.clampPercent(percent);
            double span = SimpleVoiceBoosterConfig.MAX_BOOST_PERCENT - SimpleVoiceBoosterConfig.MIN_BOOST_PERCENT;
            return (clamped - SimpleVoiceBoosterConfig.MIN_BOOST_PERCENT) / span;
        }

        private static int valueToPercent(double value) {
            double span = SimpleVoiceBoosterConfig.MAX_BOOST_PERCENT - SimpleVoiceBoosterConfig.MIN_BOOST_PERCENT;
            int percent = (int) Math.round(SimpleVoiceBoosterConfig.MIN_BOOST_PERCENT + value * span);
            return SimpleVoiceBoosterConfig.clampPercent(percent);
        }
    }

}
