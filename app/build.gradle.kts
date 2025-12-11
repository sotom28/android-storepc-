plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    //
    buildFeatures {
        compose = true
    }
    // compilar opciones de compose
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }


    namespace = "com.example.storecomponents"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.storecomponents"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // CameraX core
    implementation("androidx.camera:camera-core:1.1.0")
    implementation("androidx.camera:camera-camera2:1.1.0")
    implementation("androidx.camera:camera-lifecycle:1.1.0")
    implementation("androidx.camera:camera-view:1.0.0-alpha32")
    implementation("androidx.camera:camera-extensions:1.0.0-alpha32")
    // biometrica
    implementation("androidx.biometric:biometric:1.1.0")

    // Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Coil for image loading in Compose
    implementation("io.coil-kt:coil-compose:2.5.0")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.camera.view)
    // Retrofit para llamadas HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp para logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // JUnit 4
    testImplementation("junit:junit:4.13.2")
    // Coroutines Testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    // Architecture Components Testing
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    // Truth
    testImplementation("com.google.truth:truth:1.1.5")
    // MockWebServer
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    // Kotlin Test
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")
    // Mockito
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    // Turbine para testear Flows
    testImplementation("app.cash.turbine:turbine:1.0.0")
    // Robolectric para tests con contexto Android
    testImplementation("org.robolectric:robolectric:4.11.1")

    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}