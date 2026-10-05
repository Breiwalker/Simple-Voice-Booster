package dev.breiwalker.simple_voice_booster;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple Voice Booster's own configuration, stored as JSON in
 * {@code config/simple_voice_booster.json}.
 * <p>
 * The three options control how far the microphone boost may go and whether the
 * mod overrides Simple Voice Chat's automatic gain control and anti-clipping guard.
 * Loading never throws: a missing, unreadable or corrupt file falls back to the
 * defaults.
 */
public final class SimpleVoiceBoosterConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("simple_voice_booster");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "simple_voice_booster.json";

    /** Lowest boost the user may configure (100%). */
    public static final int MIN_BOOST_PERCENT = 100;
    /** Highest boost the user may configure, and the absolute SVC config ceiling (20000%). */
    public static final int MAX_BOOST_PERCENT = 20000;
    public static final int DEFAULT_BOOST_PERCENT = 5000;
    public static final boolean DEFAULT_FORCE_MANUAL_GAIN = true;
    public static final boolean DEFAULT_HARD_CLIP = true;

    private static SimpleVoiceBoosterConfig instance = new SimpleVoiceBoosterConfig();

    private int maxBoostPercent = DEFAULT_BOOST_PERCENT;
    private boolean forceManualGain = DEFAULT_FORCE_MANUAL_GAIN;
    private boolean hardClip = DEFAULT_HARD_CLIP;

    private SimpleVoiceBoosterConfig() {
    }

    /**
     * The active configuration. Never {@code null}; before {@link #load()} it holds defaults.
     */
    public static SimpleVoiceBoosterConfig get() {
        return instance;
    }

    /**
     * Reads the config from disk, replacing the current instance. Safe to call again to
     * re-read changes made while the game is running.
     */
    public static void load() {
        Path path = configPath();
        if (!Files.exists(path)) {
            instance = new SimpleVoiceBoosterConfig();
            return;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            SimpleVoiceBoosterConfig loaded = GSON.fromJson(reader, SimpleVoiceBoosterConfig.class);
            if (loaded == null) {
                loaded = new SimpleVoiceBoosterConfig();
            }
            loaded.maxBoostPercent = clampPercent(loaded.maxBoostPercent);
            instance = loaded;
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("Failed to load Simple Voice Booster config, using defaults", e);
            instance = new SimpleVoiceBoosterConfig();
        }
    }

    /**
     * Writes the current values to disk.
     */
    public void save() {
        this.maxBoostPercent = clampPercent(this.maxBoostPercent);
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to save Simple Voice Booster config", e);
        }
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public static int clampPercent(int percent) {
        return Math.max(MIN_BOOST_PERCENT, Math.min(MAX_BOOST_PERCENT, percent));
    }

    public int getMaxBoostPercent() {
        return maxBoostPercent;
    }

    public void setMaxBoostPercent(int maxBoostPercent) {
        this.maxBoostPercent = clampPercent(maxBoostPercent);
    }

    public boolean isForceManualGain() {
        return forceManualGain;
    }

    public void setForceManualGain(boolean forceManualGain) {
        this.forceManualGain = forceManualGain;
    }

    public boolean isHardClip() {
        return hardClip;
    }

    public void setHardClip(boolean hardClip) {
        this.hardClip = hardClip;
    }

}
