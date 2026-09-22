# Contributing to NullVoid Launcher 🤝

Thank you for your interest in contributing to **NullVoid Launcher**! We welcome contributions of all kinds — bug fixes, new widgets, theme variants, documentation improvements, and feature ideas.

---

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Project Structure](#project-structure)
- [Coding Guidelines](#coding-guidelines)
- [Submitting a Pull Request](#submitting-a-pull-request)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Features](#suggesting-features)
- [License](#license)

---

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment. Be kind, constructive, and collaborative.

---

## How Can I Contribute?

### 🐛 Fix Bugs
Browse [open issues](https://github.com/codershubinc/Null-Void-Launcher/issues) labeled `bug` and pick one to work on.

### ✨ Add Features
Check issues labeled `enhancement` or `feature-request` for ideas. You can also propose your own!

### 🎨 Create New Widget Styles
NullVoid Launcher uses a style-variant architecture. You can add new visual variants for existing widgets (Clock, Power, Network, Storage, Bluetooth, Music, etc.) by creating a new Composable file inside the appropriate widget subdirectory.

### 📖 Improve Documentation
Help improve the README, code comments, or this contributing guide.

### 🧪 Test on Different Devices
Run the launcher on various Android devices and report any UI issues, crashes, or layout problems.

---

## Getting Started

1. **Fork** the repository on GitHub.
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/<your-username>/Null-Void-Launcher.git
   cd Null-Void-Launcher
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/codershubinc/Null-Void-Launcher.git
   ```
4. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

---

## Development Setup

### Prerequisites
- **Android Studio** (Ladybug or later recommended)
- **JDK 17** or higher
- **Android SDK** with API 37 (Target) and API 29 (Minimum)

### Build & Run
```bash
# Debug build
./gradlew assembleDebug

# Release build (requires keystore — see keystore.properties)
./gradlew assembleRelease

# Run on connected device
./gradlew installDebug
```

### Project Requirements
- **Minimum SDK**: 29 (Android 10)
- **Target SDK**: 37 (Android 15)
- **Language**: Kotlin (100%)
- **UI Framework**: Jetpack Compose with Material 3

---

## Project Structure

```text
app/src/main/java/com/codershubinc/nullvoidlauncher/
├── data/                  # Data models, UserManager, repositories
├── ui/
│   ├── about/             # About screen and in-app updater
│   ├── bluetooth/         # Bluetooth helpers and settings
│   ├── drawer/            # App drawer, search, and category tabs
│   ├── focus/             # Focus/Pomodoro mode
│   ├── github/            # GitHub profile integration
│   ├── homescreen/        # Home screen, themes, and widget layout
│   ├── music/             # Media session service
│   ├── network/           # Network helpers and data usage
│   ├── power/             # Battery/power helpers
│   ├── settings/          # Launcher settings and widget config
│   ├── theme/             # Compose theme definitions
│   └── widgets/           # All widget composables
│       ├── clock/         # Clock styles (Elegant, Minimal, Modern, Retro, Terminal)
│       ├── day/           # Day-of-week styles
│       ├── music/         # Music player styles
│       ├── network/       # Network telemetry styles
│       ├── power/         # Battery telemetry styles
│       ├── storage/       # Storage telemetry styles
│       └── bluetooth/     # Bluetooth telemetry styles
└── utils/                 # Constants, AppUpdater, helpers
```

---

## Coding Guidelines

### General
- Write **clean, readable Kotlin** code following the official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
- Use **Jetpack Compose** for all UI — no XML layouts.
- Keep Composable functions focused and modular.

### Naming Conventions
- **Files**: `PascalCase.kt` (e.g., `ElegantPowerWidget.kt`)
- **Composables**: `PascalCase` function names (e.g., `fun ElegantPowerWidget()`)
- **State variables**: `camelCase` (e.g., `isCharging`, `batteryLevel`)

### Widget Style Architecture
When adding a new variant for an existing widget:
1. Create a new file in the appropriate `widgets/<type>/` directory (e.g., `widgets/power/MyNewPowerWidget.kt`).
2. Follow the existing signature pattern (accept `powerInfo`, `modifier`, `font`, `onTap`, `onLongClick`, `onClick`).
3. Register the new style in the corresponding `Style` enum in `data/` and the routing `when` block in the parent widget (e.g., `PowerWidget.kt`).

### Commit Messages
Follow the [Conventional Commits](https://www.conventionalcommits.org/) format:
```
feat(widgets): add neon power widget style
fix(drawer): prevent crash on empty app list
docs: update CONTRIBUTING.md with widget guide
chore(release): bump version to v0.1.2
```

---

## Submitting a Pull Request

1. **Ensure your branch is up to date** with `main`:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```
2. **Test your changes**:
   - Run `./gradlew assembleDebug` — it must compile without errors.
   - Test on a physical device or emulator (API 29+).
3. **Push your branch**:
   ```bash
   git push origin feature/your-feature-name
   ```
4. **Open a Pull Request** on GitHub against the `main` branch.
5. **Describe your changes** clearly in the PR description:
   - What does it do?
   - Why is it needed?
   - Screenshots (if UI changes).
6. **Wait for review** — maintainers may request changes.

### PR Checklist
- [ ] Code compiles without errors (`./gradlew assembleDebug`)
- [ ] No unrelated changes included
- [ ] New files follow existing naming and architecture patterns
- [ ] Commit messages follow conventional commit format
- [ ] Screenshots attached for UI changes

---

## Reporting Bugs

Open a [new issue](https://github.com/codershubinc/Null-Void-Launcher/issues/new) with the following:

- **Title**: Brief description of the bug
- **Device**: Model, Android version, API level
- **Steps to Reproduce**: Numbered steps to trigger the bug
- **Expected Behavior**: What should happen
- **Actual Behavior**: What actually happens
- **Screenshots/Logs**: If applicable

Use the `bug` label when creating the issue.

---

## Suggesting Features

Open a [new issue](https://github.com/codershubinc/Null-Void-Launcher/issues/new) with:

- **Title**: Clear feature name
- **Description**: What the feature does and why it would be useful
- **Mockups**: Optional sketches or references

Use the `enhancement` label when creating the issue.

---

## License

By contributing to NullVoid Launcher, you agree that your contributions will be licensed under the [GNU General Public License v3.0](LICENSE).

---

Thank you for helping make NullVoid Launcher better! 🚀
