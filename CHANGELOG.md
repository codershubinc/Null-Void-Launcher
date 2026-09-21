# Changelog

All notable changes to **NullVoid Launcher** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.1.0] - 2026-09-21 — First Stable Release

Welcome to the first official stable release of **NullVoid Launcher** (`v0.1.0`)! This milestone brings comprehensive hardware telemetry widgets, biometrics-preserving gestures, glassmorphism customization, and an ultra-refined minimalist Android desktop.

### Added
- **Storage Widget Telemetry Suite**:
  - Expanded storage telemetry with **6 distinct variants** mirroring the power widget design language:
    - `ELEGANT`: Frosted glassmorphism pill with storage usage percentage and available space.
    - `GAUGE_BAR`: Linear micro-gauge progress bar dynamically tinted based on disk consumption (Cyan $\to$ Amber $\to$ Red).
    - `RING`: Precision circular arc meter inspired by smart wearable telemetry.
    - `MINIMAL`: Ultra-compact horizontal pill displaying percentage and free/total storage.
    - `TERMINAL`: Hacker CLI aesthetic (`disk0: [42%] [74.2 GB FREE]`).
    - `RETRO`: Vintage amber LED block matrix display (`DISK [42%] [74.2 GB FREE]`).
  - Single-tap opens Android Storage Settings directly; long press opens launcher widget customization.
  - Integrated into all clock and homescreen configurations with real-time preview in Widget Settings.
- **Power & Battery Telemetry**:
  - 6 variants (`ELEGANT`, `MINIMAL`, `GAUGE_BAR`, `RING`, `TERMINAL`, `RETRO`).
  - Real-time battery percentage, charging state, battery saver status, and one-tap battery settings access.
- **Network & Wi-Fi Telemetry Revamp**:
  - Live upload & download speed tracking with jitter-free fixed-width container (`230dp`).
  - Refined `ELEGANT` network widget mirroring the CLI terminal prompt structure with specular glassmorphism gradients and signature indigo/blue accents (`net0: <ONLINE>`, live speed, and `inet: SSID • IP`).
  - Integrated data usage indicator and dedicated local Data Usage & Logs screen.
- **Bluetooth Telemetry & Device Management**:
  - Connected device monitoring with live battery percentage and contextual device type icons (Headphones, Watch, Audio, Generic).
  - "Show only if connected" toggle and dedicated long-press configuration screen for choosing preferred device telemetry.
- **Biometric-Preserving Screen Lock Gesture**:
  - Double-tap empty homescreen space to instantly sleep and lock the screen using the `NullVoidAccessibilityService` (`GLOBAL_ACTION_LOCK_SCREEN`).
  - **Preserves Biometrics**: Fingerprint and face unlock remain functional without forcing PIN entry.
  - Elegant setup prompt guiding users to enable the accessibility service with a 1-tap shortcut.
  - Configurable in Settings under the new **Gestures** section (Lock Screen, Cycle Wallpaper, or Disabled).
- **Comprehensive Widget Typography & Styling**:
  - Independent font customization per widget (`DEFAULT`, `SANS_SERIF`, `MONOSPACE`, `SERIF`, `CURSIVE`).
  - Dynamic glassmorphism settings (specular highlight, customizable tint color, alpha opacity, corner radius).

### Changed
- Promoted build versioning from `v0.0.2 beta` to `v0.1.0` (First Stable Release, `versionCode = 2`).
- Updated `Constants.kt` system build type to `First Stable Release`.
- Standardized gesture haptic feedback across all homescreen and widget interaction points.

---

## [0.0.2-beta] - 2026-09-15

### Added
- Integrated media session listener and `MediaService` for lock-screen/homescreen music controls.
- 5 unique music player styles: `ELEGANT`, `MINIMAL`, `VINYL`, `RETRO`, `NEON`.
- Pomodoro focus timer with distraction-free dashboard and interactive controls.
- GitHub profile screen and live developer telemetry feed.
- Auto-wallpaper engine supporting cloud wallpapers and device gallery images with real-time blur.

### Changed
- Restructured layout architecture into full Compose Modern engine.
- Replaced traditional app drawer with instant fuzzy-search drawer.

---

## [0.0.1-alpha] - 2026-09-01

### Added
- Initial proof-of-concept of NullVoid Launcher.
- Minimalist homescreen clock, day, and favorites widgets.
- Core settings storage and user preferences management via `UserManager`.
