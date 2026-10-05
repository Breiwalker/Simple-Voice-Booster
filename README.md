# Simple Voice Booster

A client-side Fabric mod that raises the microphone amplification limit of
[Simple Voice Chat](https://modrinth.com/mod/simple-voice-chat) so you can boost
your mic far beyond the vanilla 100% — with a configurable maximum and optional
control over AGC and clipping.

## Features

- **Configurable boost** — raise the Simple Voice Chat microphone gain to up to
  **20000%** (200x), instead of the vanilla 100% (24 dB) cap.
- **Own config screen** — set the maximum boost, and toggle the AGC/clipping
  overrides, from Mod Menu's "Configure" button (no editing JSON by hand required).
- **Typed boost value** — Simple Voice Chat's audio settings gain a text box next
  to the amplification slider so you can type an exact percentage.
- **Optional aggressive gain** — `forceManualGain` disables Simple Voice Chat's
  automatic gain control; `hardClip` applies the gain unconditionally and hard-clips
  loud input instead of using SVC's anti-clip guard.

## Requirements

- Minecraft **26.1+** (built and tested against **26.3**)
- [Fabric Loader](https://fabricmc.net/) `0.19.5+`
- [Fabric API](https://modrinth.com/mod/fabric-api) `0.161.0+26.3`
- [Simple Voice Chat](https://modrinth.com/mod/simple-voice-chat) `2.6.24+`
- [Mod Menu](https://modrinth.com/mod/modmenu) (optional, for the config screen)

Simple Voice Chat and Mod Menu are **not bundled** — install them alongside this mod.

## Installation

1. Install Fabric Loader for Minecraft 26.x.
2. Put the Fabric API and Simple Voice Chat jars in your `mods` folder.
3. Put `simple-voice-booster-<version>.jar` in your `mods` folder.
4. (Optional) Install Mod Menu to reach the config screen.

## Configuration

The mod stores its config in `config/simple_voice_booster.json`:

| Key               | Type    | Default | Range      | Description                                                       |
| ----------------- | ------- | ------- | ---------- | ----------------------------------------------------------------- |
| `maxBoostPercent` | int     | `5000`  | `100`–`20000` | Maximum microphone boost as a percentage.                      |
| `forceManualGain` | boolean | `true`  | —          | Disable Simple Voice Chat's AGC and force the manual gain path.   |
| `hardClip`        | boolean | `true`  | —          | Apply the gain unconditionally and hard-clip; `false` restores SVC's anti-clip guard. |

A missing or corrupt config file falls back to the defaults and never crashes the
game. Values are clamped to the valid range on load and save.

## Usage

- Open Mod Menu → select **Simple Voice Booster** → **Configure**.
- Drag the **Max boost** slider or edit the JSON to change the ceiling.
- **Manual gain (disable AGC)** and **Hard-clip loud input** toggle the overrides.
- **Open Voice Chat audio settings** jumps to Simple Voice Chat's own settings,
  where the boosted slider (and the typed percentage box) live.

## Building

```
./gradlew build
```

The output jar is written to `build/libs/`.
