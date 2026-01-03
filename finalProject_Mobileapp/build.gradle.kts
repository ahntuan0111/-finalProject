// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // Plugin này cần thiết cho Kotlin 2.0 + Compose
    alias(libs.plugins.kotlin.compose) apply false

    alias(libs.plugins.hilt.android) apply false
}