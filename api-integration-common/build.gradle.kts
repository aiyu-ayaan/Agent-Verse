/*
 *
 *  Copyright (c) 2026 Ayaan.
 *  Licensed under the MIT License.
 *
 *  Created: 2026
 *  Modified: 3/18/26, 9:46 PM
 *
 *  AgentVerse
 *  Integrates multiple AI models with a modular clean architecture.
 *
 *
 */

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.atech.api_integration_common"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.bundles.commonTest)
    androidTestImplementation(libs.bundles.commonTest)
}
