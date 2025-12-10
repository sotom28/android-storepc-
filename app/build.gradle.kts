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
    // Firebase: usar BoM para manejar versiones y declarar librerías sin versión
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    // Añadir dentro de `dependencies` en `app/build.gradle.kts`
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")        // Compose test JUnit4
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")         // soporte debug para tests de UI

// Mockito Kotlin y Android para mockear NavController en androidTest
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    androidTestImplementation("org.mockito:mockito-android:4.11.0")

// JUnit (si lo necesitas en androidTest)
    androidTestImplementation("junit:junit:4.13.2")

// Dependencias para pruebas unitarias (src/test)
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    testImplementation("org.mockito:mockito-core:4.11.0")
    // Robolectric para ejecutar tests de Android/Compose en la JVM
    testImplementation("org.robolectric:robolectric:4.10.3")
    // Activity runtime needed by ActivityScenario under Robolectric
    testImplementation("androidx.activity:activity:1.8.2")
    testImplementation("androidx.activity:activity-ktx:1.8.2")
    // androidx.test core/runner needed for ActivityScenario and test rules
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("androidx.test:runner:1.5.2")
    testImplementation("androidx.test:rules:1.5.0")
    // For testing coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    // For testing LiveData and ViewModel
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // --- Redes: Retrofit, OkHttp y Coroutines (conversor Gson incluido más abajo)

    //Gson
    implementation("com.google.code.gson:gson:2.10.1")

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
    implementation("io.coil-kt:coil-compose:2.4.0")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.ui.test.junit4)

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

    // --- Redes: Retrofit, OkHttp y Coroutines ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}