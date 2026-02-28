# Consent Camera App (Kotlin + CameraX)

This is a minimal scaffold for a consent-based Android app that requests camera permission, shows a camera preview using CameraX, captures a photo, and uploads it to an example HTTPS endpoint.

Quick start

1. Open the folder `consent-camera-app` in Android Studio.
2. Update the upload endpoint in `MainActivity.kt` (variable `url`) to point to your server.
3. Sync Gradle and run on a device (minSdk 23+).

Notes
- The app explicitly requests camera permission and only captures/uploads when the user taps the button.
- After taking a picture a confirmation dialog appears displaying the image; the file is only uploaded if the user taps **Upload**.
- This scaffold is intentionally minimal — production apps should add error handling, TLS validation, authentication, and expanded consent UX before uploading.

Build on GitHub (optional)

If you don't want to build locally you can push the `consent-camera-app` repository to GitHub and GitHub Actions will build a debug APK for you using the included workflow. Steps:

1. Commit and push the project to a GitHub repo (branch `main` or `master`).
2. The workflow at `.github/workflows/android-build.yml` runs on push and will upload an artifact named `app-debug-apk` containing `app-debug.apk`.
3. In the repository Actions page, open the latest run and download the artifact.

Notes:
- The CI build still requires proper Android SDK setup in the workflow (provided). After pushing, allow a first run and download the artifact from the Actions UI.
- If you want a signed release APK, I can add signing instructions or a secure GitHub Actions secrets flow.
