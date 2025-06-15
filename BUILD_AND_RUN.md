# Build and Run Instructions

This document provides instructions on how to set up the development environment, build, and run the application.

## 1. Prerequisites

Ensure you have the following software installed and configured:

-   **Java Development Kit (JDK):**
    -   A version like JDK 8 (or Java 1.8.x) or higher is required. Android Studio's embedded JDK is often sufficient, but a standalone JDK might be needed for command-line builds.
-   **Android Studio:**
    -   It is highly recommended to install the latest stable version of Android Studio from [developer.android.com/studio](https://developer.android.com/studio). Android Studio provides an integrated development environment, including the Android SDK tools, emulator, and build system.
-   **Android SDK:**
    -   Android Studio typically manages SDK installations. However, ensure the following are available (usually installed via Android Studio's SDK Manager):
        -   `compileSdkVersion`: 28
        -   `minSdkVersion`: 19
        -   `targetSdkVersion`: 28
        -   Build Tools: A version compatible with Android Gradle Plugin (AGP) 3.4.1, such as version `28.0.3`. Android Studio will usually prompt if a specific version needs to be downloaded.
-   **Android Emulator or Physical Device:**
    -   An Android Emulator (configurable through Android Studio's AVD Manager) or a physical Android device (with USB debugging enabled) is required to run and test the application. The device/emulator should run Android API level 19 or higher.

## 2. Firebase Setup

This application uses Firebase for backend services. You need to set up a Firebase project:

1.  **Create a Firebase Project:**
    -   Go to the [Firebase console](https://console.firebase.google.com/).
    -   Click on "Add project" and follow the on-screen instructions to create a new project.

2.  **Add an Android App to Firebase:**
    -   Once your project is created, navigate to Project Overview.
    -   Click the Android icon (or "Add app" and then select Android) to register your Android application.
    -   **Package Name:** You must use `com.mansourappdevelopment.androidapp.kidsafe`. This is specified in the `Application/app/build.gradle` file (`applicationId`).
    -   You can optionally provide a nickname for the app and the SHA-1 debug signing certificate fingerprint at this stage (see step 5 for SHA-1).

3.  **Download `google-services.json`:**
    -   After registering the app, Firebase will provide a `google-services.json` file for download.
    -   Download this file.

4.  **Place `google-services.json`:**
    -   Copy the downloaded `google-services.json` file and place it into the `Application/app/` directory of this project. This file contains the necessary Firebase project configuration for your app.

5.  **Enable Firebase Services:**
    -   In the Firebase console, navigate to the services you need to enable for your project:
        -   **Authentication:**
            -   Go to "Authentication" -> "Sign-in method".
            -   Enable "Email/Password" provider.
            -   Enable "Google" provider. If you enable Google Sign-In, you will need to provide the SHA-1 fingerprint from your signing certificate (debug and release) in the Firebase project settings (Project Settings -> Your apps -> Android app -> SHA certificate fingerprints).
                -   To get your debug SHA-1 key, you can use the command (from the `Application` directory):
                    ```bash
                    ./gradlew signingReport
                    ```
                    Look for the "Variant: debug" and "Config: debug" SHA-1 fingerprint.
        -   **Realtime Database:**
            -   Go to "Realtime Database" -> "Create Database".
            -   Choose a location and set up security rules (e.g., start in test mode with read/write access, then refine for production).
        -   **Storage:**
            -   Go to "Storage" -> "Get started".
            -   Follow the prompts to set up Firebase Storage. Set up security rules as needed.

## 3. Building the Application

You can build the application using the Gradle wrapper (`gradlew`) provided in the project. Open a terminal or command prompt:

1.  **Navigate to Project Directory:**
    ```bash
    cd path/to/your/project/Application
    ```

2.  **Clean the Project (Optional):**
    -   This command removes any previous build artifacts.
    -   On macOS/Linux:
        ```bash
        ./gradlew clean
        ```
    -   On Windows:
        ```bash
        gradlew.bat clean
        ```

3.  **Build the Debug APK:**
    -   This command compiles the application and packages it into a debug APK.
    -   On macOS/Linux:
        ```bash
        ./gradlew assembleDebug
        ```
    -   On Windows:
        ```bash
        gradlew.bat assembleDebug
        ```
    -   The output APK file will be located in `Application/app/build/outputs/apk/debug/app-debug.apk`.

## 4. Running the Application

### Using Android Studio (Recommended)

1.  **Open Project:**
    -   Launch Android Studio.
    -   Select "Open an existing Android Studio project" (or "File" > "Open...").
    -   Navigate to and select the `Application` directory of this project.
2.  **Gradle Sync:**
    -   Android Studio will automatically attempt to sync the project with Gradle. Wait for this process to complete. It might download necessary dependencies or SDK components.
3.  **Select Run Configuration:**
    -   In the toolbar, you should see a run configuration, typically named `app`. Ensure this is selected.
4.  **Choose Device:**
    -   Select an available Android Emulator from the dropdown list or connect a physical Android device. Ensure the device is recognized by Android Studio.
5.  **Run:**
    -   Click the "Run" button (green play icon) in the toolbar, or select "Run" > "Run 'app'" from the menu.
    -   Android Studio will build and install the app on the selected emulator/device and then launch it.

### Using Gradle (Less Common for Direct Running)

While primarily used for building, Gradle can also install the app on a connected device or running emulator:

1.  **Install Debug APK:**
    -   Ensure you have a device connected or an emulator running.
    -   On macOS/Linux:
        ```bash
        ./gradlew installDebug
        ```
    -   On Windows:
        ```bash
        gradlew.bat installDebug
        ```
    -   This will build and install the debug version of the app. You will then need to manually launch it on the device/emulator.

## 5. Notes on Dependencies

-   The project declares its dependencies in the `Application/build.gradle` (project-level) and `Application/app/build.gradle` (app-level) files.
-   When you build the project or sync it in Android Studio, Gradle automatically downloads and manages these dependencies from configured repositories (like Google's Maven repository, JCenter, etc.). This includes libraries for Firebase, AndroidX support, and other third-party tools.
-   Ensure you have a stable internet connection when building the project for the first time, as Gradle will need to download these dependencies.
