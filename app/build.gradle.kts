plugins {
    id("com.android.application")
}

android {
    namespace = "com.kana.study"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.kana.study"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables.useSupportLibrary = false
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    androidResources {
        localeFilters += listOf("zh", "zh-rCN", "zh-rTW")
    }

}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1") {
        exclude(group = "org.jetbrains.kotlin" , module = "kotlin-stdlib-jdk8")
    }
}
