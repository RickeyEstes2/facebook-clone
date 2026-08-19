// File: build.gradle.kts
import java.io.File

plugins {
    id("com.android.application") version "8.2.0" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register<DefaultTask>("setupProject") {
    doLast {
        // Create directory structure
        val base = File(projectDir, "app/src/main")
        base.resolve("AndroidManifest.xml").writeText(
            """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.facebookclone">
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="FB Clone"
        android:supportsRtl="true"
        android:theme="@style/Theme.AppCompat.Light.DarkActionBar">
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
        <activity android:name=".ShareActivity" android:exported="true"
            android:label="Share to FB Clone">
            <intent-filter>
                <action android:name="android.intent.action.SEND"/>
                <category android:name="android.intent.category.DEFAULT"/>
                <data android:mimeType="text/plain"/>
            </intent-filter>
        </activity>
    </application>
</manifest>"""
        )

        // MainActivity
        val activityPath = base.resolve("java/com/example/facebookclone")
        activityPath.mkdirs()
        activityPath.resolve("MainActivity.java").writeText(
            """package com.example.facebookclone;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var tv = new android.widget.TextView(this);
        tv.setText("Facebook Clone\\nShare URLs here");
        tv.setGravity(android.view.Gravity.CENTER);
        setContentView(tv);
    }
}"""
        )

        // ShareActivity
        activityPath.resolve("ShareActivity.java").writeText(
            """package com.example.facebookclone;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
public class ShareActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var tv = new android.widget.TextView(this);
        tv.setText(getIntent().getStringExtra("android.intent.extra.TEXT"));
        setContentView(tv);
    }
}"""
        )

        // Build config
        projectDir.resolve("app/build.gradle").writeText(
            """plugins { id 'com.android.application' }
android {
    namespace 'com.example.facebookclone'
    compileSdk 34
    defaultConfig {
        applicationId "com.example.facebookclone"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
    buildTypes { release { minifyEnabled false } }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
}"""
        )

        // Settings
        projectDir.resolve("settings.gradle").writeText(
            """pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement { repositories { google(); mavenCentral() } }
rootProject.name = 'FacebookClone'
include ':app'"""
        )

        // Properties
        projectDir.resolve("gradle.properties").writeText(
            "android.useAndroidX=true\norg.gradle.jvmargs=-Xmx2048m"
        )

        // Workflow
        File(projectDir, ".github/workflows").mkdirs()
        File(projectDir, ".github/workflows/build.yml").writeText(
            """name: Build APK
on: [push]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Setup Android SDK
      uses: android-actions/setup-android@v3
    - run: chmod +x ./gradlew
    - run: ./gradlew assembleDebug
    - uses: actions/upload-artifact@v4
      with:
        name: app-debug-apk
        path: app/build/outputs/apk/debug/app-debug.apk"""
        )

        println("\n✅ Project generated! Next steps:")
        println("1. Initialize git repo: git init && git add . && git commit -m 'init'")
        println("2. Push to GitHub")
        println("3. Go to Actions tab to get your APK")
    }
}
