# 📝 NullVoid Launcher Project TODOs

## 🚀 Performance & Optimization

### [ ] Optimize App Drawer Search
- **Problem**: Searching currently takes 1-3 seconds because `allApps` is fetched on every drawer open.
- **Solution**:
    - [ ] **Global Caching**: Move `allApps` state to `HomeScreen` and fetch once on app startup.
    - [ ] **Reactive Syncing**: Implement `LauncherApps.Callback` in `HomeScreen` using `DisposableEffect` to listen for:
        - `onPackageAdded`
        - `onPackageRemoved`
        - `onPackageChanged`
    - [ ] **Pre-computation**: Convert app labels to lowercase once during fetching to speed up `startsWith` filtering.
- **Goal**: Instant search results as the user types.

---

## 🖼️ Assets & Storage

### [ ] Move Wallpapers to GitHub Storage
- **Problem**: Bundled wallpapers increase APK size and cannot be updated dynamically.
- **Solution**:
    - [ ] **GitHub Repo Storage**: Upload high-res wallpapers to `/assets/wallpapers/` in the repo.
    - [ ] **JSON Database**: Create `wallpapers.json` containing `id`, `name`, and `url` (using `raw.githubusercontent.com` links).
    - [ ] **Dynamic Fetching**: Add `fetchWallpapers()` to `UserManager.kt` using `HttpURLConnection`.
    - [ ] **Hybrid Loading**:
        - Keep 3-5 "Core" wallpapers in `res/drawable` for offline/first-run use.
        - Fetch the rest from the JSON list.
    - [ ] **Coil Integration**: Use `NetworkImage.kt` to handle caching and loading of remote URLs.
- **Goal**: Infinite wallpaper collection with minimal APK size.

---

## 🛠️ Features & Enhancements

- [ ] **Flexible UserManager**: Update `saveWallpaperRes` and `getWallpaperRes` logic to support both local resource IDs and remote URL strings.
- [ ] **Wallpaper Preview**: Add a loading state in the Wallpaper selection row for remote thumbnails.
