package dev.breiwalker.simple_voice_booster;

/**
 * Shared constants and conversions for the microphone boost.
 * <p>
 * Simple Voice Chat stores the microphone amplification as a gain in decibels.
 * The vanilla range is {@code -40 dB .. 24 dB}. This mod widens the upper bound
 * to an absolute ceiling of {@link #ABSOLUTE_MAX_PERCENT} (20000%). The boost the
 * user actually wants is stored in {@link SimpleVoiceBoosterConfig} and applied
 * dynamically to the slider span, the typed input box and the gain path.
 */
public final class MicBoost {

    /**
     * The absolute maximum the user may configure, and the value the Simple Voice
     * Chat config entry is widened to (20000%, a linear multiplier of 200).
     */
    public static final int ABSOLUTE_MAX_PERCENT = SimpleVoiceBoosterConfig.MAX_BOOST_PERCENT;

    /**
     * {@link #ABSOLUTE_MAX_PERCENT} as a linear multiplier (200.0).
     */
    public static final double ABSOLUTE_MAX_AMPLIFICATION = ABSOLUTE_MAX_PERCENT / 100.0D;

    /**
     * {@link #ABSOLUTE_MAX_AMPLIFICATION} expressed in decibels
     * (20 * log10(200) ~= 46.0206 dB).
     */
    public static final double ABSOLUTE_MAX_GAIN_DB = 20.0D * Math.log10(ABSOLUTE_MAX_AMPLIFICATION);

    private MicBoost() {
    }

    /**
     * The gain in decibels matching {@link #ABSOLUTE_MAX_PERCENT}. Used to widen the
     * inlined bound of Simple Voice Chat's {@code microphone_gain} config entry.
     */
    public static double absoluteMaxGainDb() {
        return ABSOLUTE_MAX_GAIN_DB;
    }

    /**
     * The boost percentage the user configured. Always within
     * {@link SimpleVoiceBoosterConfig#MIN_BOOST_PERCENT}..{@link SimpleVoiceBoosterConfig#MAX_BOOST_PERCENT}.
     */
    public static int maxBoostPercent() {
        return SimpleVoiceBoosterConfig.get().getMaxBoostPercent();
    }

    /**
     * {@link #maxBoostPercent()} as a linear multiplier.
     */
    public static double maxAmplification() {
        return maxBoostPercent() / 100.0D;
    }

    /**
     * {@link #maxBoostPercent()} expressed in decibels.
     */
    public static double maxGainDb() {
        return percentToGainDb(maxBoostPercent());
    }

    /**
     * Converts a gain in decibels to a display percentage where 0 dB is 100%.
     */
    public static long gainDbToPercent(double gainDb) {
        return Math.round(Math.pow(10.0D, gainDb / 20.0D) * 100.0D);
    }

    /**
     * Converts a display percentage (100% == 0 dB) back to a gain in decibels.
     */
    public static double percentToGainDb(double percent) {
        return 20.0D * Math.log10(percent / 100.0D);
    }

}
