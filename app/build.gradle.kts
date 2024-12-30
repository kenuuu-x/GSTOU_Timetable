plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroid)
}

android {
    namespace = "com.nelsonxilv.gstoutimetable"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nelsonxilv.gstoutimetable"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "0.2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "GSTOU_Timetable-${variant.baseName}-${variant.versionName}.apk"
                output.outputFileName = outputFileName
            }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.bundles.androidx)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.retrofit)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.kotlin.serialization.json)
    implementation(libs.okhttp3)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.room)

    ksp(libs.hilt.compiler)
    ksp(libs.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.bundles.debug)
}