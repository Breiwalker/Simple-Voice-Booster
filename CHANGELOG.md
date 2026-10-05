# Changelog

All notable changes to this project are documented here.

## [1.2] - 5.10.2026

### Added

- A mod-owned config file (`config/simple_voice_booster.json`) with three options:
  `maxBoostPercent` (100–20000, default 5000), `forceManualGain` (default true),
  and `hardClip` (default true).
- A config screen reachable from Mod Menu's "Configure" button, with a slider for
  the maximum boost, toggles for the AGC/clipping overrides, and a shortcut into
  Simple Voice Chat's audio settings.
- The microphone amplification ceiling is now configurable up to 20000% (200x,
  ~46 dB).

### Changed

- The maximum boost is no longer hardcoded to 5000%; the slider span, typed value
  and gain path all respect the configured `maxBoostPercent`.
- `forceManualGain` now gates both the AGC option (hidden) and the forced manual
  gain path, instead of always disabling AGC.
- `hardClip` now gates the hard-clip volume override; when disabled, Simple Voice
  Chat's original anti-clip guard is used unchanged.

## [1.1] - 4.10.2026

### Added

- Mod Menu integration (the "Configure" button opens Simple Voice Chat's audio
  settings).
- Mod icon.

## [1.0] - 4.10.2026

### Added

- Raised the Simple Voice Chat microphone amplification limit to 5000%.
- A text input box next to the amplification slider for typing an exact boost.
- Swapped the clipping warning tooltip for a "Boosted with Simple Voice Booster"
  indicator past 200%.
