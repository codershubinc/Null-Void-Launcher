# NullVoid Launcher 🚀

NullVoid Launcher is a highly customizable, modern Android launcher built entirely with **Jetpack Compose**. It focuses on providing a clean, aesthetic experience with a wide variety of themes and widgets to suit your style.

## ✨ Features

- **🎨 Dynamic Themes**: Choose from multiple pre-built themes:
  - **Pixel Theme**: For the clean Google experience.
  - **Elegant Theme**: Minimalist and sophisticated.
  - **Modern Theme**: Sleek and current.
  - **Vertical Theme**: A unique take on layout.
  - **Standard Theme**: Familiar and reliable.
- **🕰️ Diverse Clock Widgets**: 
  - Minimal, Terminal, Bold, Vertical, Void, Modern, Pixel, and Elegant Clock styles.
- **🎵 Music Integration**:
  - Multiple music widget styles (Fused, Standard, Modern, Elegant).
- **📱 Smart App Drawer**: Fast and organized access to all your apps.
- **⭐ Favorites Widget**: Quick access to your most-used applications.
- **🛠️ Customization**: Fine-tune your home screen with various bottom bar styles and widget layouts.
- **📦 Github Integration**: View your Github profile directly from the launcher.

## 🛠️ Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Design System**: [Material 3](https://m3.material.io/)
- **Programming Language**: [Kotlin](https://kotlinlang.org/)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Minimum SDK**: 29 (Android 10)
- **Target SDK**: 37 (Android 15)

## 📁 Project Structure

```text
app/src/main/java/com/codershubinc/nullvoidlauncher/
├── data/              # Data management and repositories
├── ui/                
│   ├── drawer/        # App drawer implementation
│   ├── github/        # Github profile integration
│   ├── homescreen/    # Home screen layouts and themes
│   ├── music/         # Media service logic
│   ├── settings/      # Launcher settings
│   ├── theme/         # Compose theme definitions (Colors, Type, etc.)
│   └── widgets/       # Custom widgets (Clock, Music, Favorites, BottomBar)
└── utils/             # Helper classes and utilities
```

## 🚀 Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/codershubinc/NullVoidLauncher.git
   ```
2. **Open in Android Studio**:
   - Ensure you have the latest version of Android Studio (Ladybug or later recommended).
3. **Build and Run**:
   - Select the `app` module and click **Run**.

## 🤝 Contributing

Contributions are welcome! If you have ideas for new widgets, themes, or improvements:
1. Fork the project.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---
Built with ❤️ by [CodersHubInc](https://github.com/codershubinc)
