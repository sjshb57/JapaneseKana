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
        versionCode = 2
        versionName = "1.1.0"
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

    androidResources {
        localeFilters += listOf("zh-rCN", "zh-rTW")
    }

}

dependencies {
    implementation("androidx.activity:activity:1.13.0")
    implementation("androidx.viewpager:viewpager:1.1.0")
}
