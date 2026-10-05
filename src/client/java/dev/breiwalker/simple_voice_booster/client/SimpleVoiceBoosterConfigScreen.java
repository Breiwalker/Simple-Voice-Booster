package dev.breiwalker.simple_voice_booster.client;

import de.maxhenkel.voicechat.gui.VoiceChatSettingsScreen;
import dev.breiwalker.simple_voice_booster.SimpleVoiceBoosterConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Simple Voice Booster's own config screen, reached from Mod Menu's "Configure"
 * button. Exposes the three options stored in {@link SimpleVoiceBoosterConfig}
 * and a shortcut into Simple Voice Chat's audio settings.
 */
public class SimpleVoiceBoosterConfigScreen extends Screen {

    private static final int WIDGET_WIDTH = 200;
    private static final int WIDGET_HEIGHT = 20;
    private static final int WIDGET_SPACING = 24;
    private static final int BOX_WIDTH = 48;
    private static final int PERCENT_LABEL_WIDTH = 12;

    private final Screen parent;
    private MaxBoostSlider maxBoostSlider;
    private EditBox maxBoostBox;
    private boolean syncing;

    public SimpleVoiceBoosterConfigScreen(Screen parent) {
        super(Component.translatable("screen.simple_voice_booster.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SimpleVoiceBoosterConfig config = SimpleVoiceBoosterConfig.get();

        int x = this.width / 2 - WIDGET_WIDTH / 2;
        int y = this.height / 4;

        // Max boost slider + text box for typing an exact percentage.
        int sliderWidth = WIDGET_WIDTH - BOX_WIDTH - PERCENT_LABEL_WIDTH - 2;
        this.maxBoostSlider = this.addRenderableWidget(new MaxBoostSlider(
                x, y, sliderWidth, WIDGET_HEIGHT, config.getMaxBoostPercent(), this::onSliderChanged));
        this.maxBoostBox = new EditBox(
                this.font, x + sliderWidth + 2, y, BOX_WIDTH, WIDGET_HEIGHT, Component.literal("%"));
        this.maxBoostBox.setMaxLength(5);
        this.maxBoostBox.setValue(Integer.toString(config.getMaxBoostPercent()));
        this.maxBoostBox.setResponder(this::onBoxChanged);
        this.addRenderableWidget(this.maxBoostBox);
        y += WIDGET_SPACING;

        CycleButton<Boolean> manualGain = this.addRenderableWidget(CycleButton.booleanBuilder(
                        Component.translatable("options.on"),
                        Component.translatable("options.off"),
                        config.isForceManualGain())
                .create(x, y, WIDGET_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("option.simple_voice_booster.force_manual_gain"),
                        (button, value) -> {
                            config.setForceManualGain(value);
                            config.save();
                        }));
        manualGain.setTooltip(Tooltip.create(
                Component.translatable("tooltip.simple_voice_booster.force_manual_gain")));
        y += WIDGET_SPACING;

        CycleButton<Boolean> hardClip = this.addRenderableWidget(CycleButton.booleanBuilder(
                        Component.translatable("options.on"),
                        Component.translatable("options.off"),
                        config.isHardClip())
                .create(x, y, WIDGET_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("option.simple_voice_booster.hard_clip"),
                        (button, value) -> {
                            config.setHardClip(value);
                            config.save();
                        }));
        hardClip.setTooltip(Tooltip.create(
                Component.translatable("tooltip.simple_voice_booster.hard_clip")));
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
        if (this.maxBoostBox != null) {
            int labelX = this.maxBoostBox.getX() + this.maxBoostBox.getWidth() + 2;
            int labelY = this.maxBoostBox.getY() + (this.maxBoostBox.getHeight() - this.font.lineHeight) / 2;
            extractor.text(this.font, "%", labelX, labelY, 0xFFFFFF);
        }
    }

    private void onSliderChanged(int percent) {
        if (this.syncing || this.maxBoostBox == null || this.maxBoostBox.isFocused()) {
            return;
        }
        this.syncing = true;
        try {
            this.maxBoostBox.setValue(Integer.toString(percent));
        } finally {
            this.syncing = false;
        }
    }

    private void onBoxChanged(String text) {
        if (this.syncing) {
            return;
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        int percent;
        try {
            percent = Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            this.refreshBox();
            return;
        }
        percent = SimpleVoiceBoosterConfig.clampPercent(percent);

        SimpleVoiceBoosterConfig config = SimpleVoiceBoosterConfig.get();
        config.setMaxBoostPercent(percent);
        config.save();

        if (this.maxBoostSlider != null) {
            this.syncing = true;
            try {
                this.maxBoostSlider.setPercent(percent);
            } finally {
                this.syncing = false;
            }
        }
        if (!Integer.toString(percent).equals(text)) {
            this.refreshBox();
        }
    }

    private void refreshBox() {
        if (this.maxBoostBox == null) {
            return;
        }
        this.syncing = true;
        try {
            this.maxBoostBox.setValue(Integer.toString(SimpleVoiceBoosterConfig.get().getMaxBoostPercent()));
        } finally {
            this.syncing = false;
        }
    }

    /**
     * Maps the slider's 0..1 value onto the full allowed boost range
     * ({@link SimpleVoiceBoosterConfig#MIN_BOOST_PERCENT}..{@link SimpleVoiceBoosterConfig#MAX_BOOST_PERCENT}).
     */
    private static class MaxBoostSlider extends AbstractSliderButton {

        private final Consumer<Integer> onChange;

        MaxBoostSlider(int x, int y, int width, int height, int initialPercent, Consumer<Integer> onChange) {
            super(x, y, width, height, Component.empty(), percentToValue(initialPercent));
            this.onChange = onChange;
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
            int percent = valueToPercent(this.value);
            config.setMaxBoostPercent(percent);
            config.save();
            this.onChange.accept(percent);
        }

        void setPercent(int percent) {
            this.setValue(percentToValue(percent));
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
