plugins {
    id("com.addhen.livefront.android.library")
    id("com.addhen.livefront.kotlin.android")
    id("com.addhen.livefront.junit5")
}

android.namespace = "com.addhen.livefront.testing"

dependencies {
    implementation(libs.test.jupiter.api)
    api(libs.test.coroutines.test)
    api(libs.test.turbine)
}