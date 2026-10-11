plugins {
    id("com.android.application")
}

android {
    namespace = "com.kana.study"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.kana.study"
        minSdk = 26
        targetSdk = 37
        versionCode = 3
        versionName = "1.2.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

}

dependencies {
    implementation("androidx.core:core:1.19.1")
    implementation("androidx.viewpager:viewpager:1.1.0")
}
