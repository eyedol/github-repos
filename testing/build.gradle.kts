plugins {
    id("com.addhen.livefront.android.library")
    id("com.addhen.livefront.kotlin.android")
}

android.namespace = "com.addhen.livefront.testing"

dependencies {
    api(libs.test.coroutines.test)
    api(libs.test.android.junit5)
    api(libs.test.jupiter.api)
    api(libs.test.turbine)
}