# Clock Home 1.0

A simple offline Android digital clock for an old phone used as a home/table clock.

Features:
- Large 24-hour digital clock
- Seconds display
- Date display
- Fullscreen / immersive mode
- Keeps the screen awake while the app is running
- Landscape-first layout
- Custom clock launcher icon (adaptive icon)
- No internet permission and no ads

## Build on GitHub from a phone
1. Create a new GitHub repository.
2. Upload the project files/folders so the repository contains `settings.gradle`, `build.gradle`, `app/`, and `.github/workflows/build-apk.yml` at the root.
3. Open **Actions** > **Build Clock Home APK** > **Run workflow**.
4. After the workflow finishes, open the run and download the **Clock-Home-debug-apk** artifact.

GitHub Actions uses Gradle to build the APK; no Android Studio is required on the phone.
