plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    alias(libs.plugins.hilt.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.finalproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.finalproject"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Sau khi xóa các dependency sai ở dưới, bạn có thể thử BỎ đoạn exclude này đi.
    // Nếu vẫn lỗi thì hãy giữ lại.
    configurations.all {
        exclude(group = "com.google.auto.value", module = "auto-value")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    // implementation(libs.androidx.compose.material3) // Nếu bạn dùng material3 ở dưới rồi thì bỏ dòng này đi cũng được

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.i18n)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // --- CÁC DÒNG GÂY LỖI ĐÃ BỊ XÓA ---
    // XÓA: implementation(libs.androidx.room.compiler.processing.testing)
    // XÓA: implementation(libs.firebase.appdistribution.gradle)

    // Retrofit & Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Coil
    implementation(libs.coil.compose)

    // Icons
    implementation(libs.androidx.material.icons.extended)

    implementation("androidx.compose.material3:material3:1.3.0")
//    implementation("com.github.barteksc:android-pdf-viewer:2.8.2")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")

    // Youtube
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    // Chart
    implementation("com.patrykandpatrick.vico:compose:1.13.0")
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.0")
    implementation("com.patrykandpatrick.vico:core:1.13.0")
}