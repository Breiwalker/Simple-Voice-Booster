package dev.breiwalker.simple_voice_booster;

/**
 * Shared constants for the microphone boost.
 * <p>
 * Simple Voice Chat stores the microphone amplification as a gain in decibels.
 * The vanilla range is {@code -40 dB .. 24 dB}. This mod extends the upper
 * bound so the slider can reach 5000% amplification (a linear multiplier of 50).
 */
public final class MicBoost {

    /**
     * The maximum linear amplification we allow (5000%).
     */
    public static final double MAX_AMPLIFICATION = 50.0D;

    /**
     * {@link #MAX_AMPLIFICATION} expressed in decibels (20 * log10(50) ~= 33.979 dB).
     */
    public static final double MAX_GAIN_DB = 20.0D * Math.log10(MAX_AMPLIFICATION);

    private MicBoost() {
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
