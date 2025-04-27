plugins {
    id("com.addhen.livefront.android.library")
    id("com.addhen.livefront.kotlin.android")
}

android.namespace = "com.addhen.livefront.testing"

dependencies {
    implementation(libs.test.coroutines.test)
    implementation(libs.test.android.junit5)
    implementation(libs.test.jupiter.api)
    implementation(libs.test.jupiter.engine)
    api(libs.test.turbine)
}