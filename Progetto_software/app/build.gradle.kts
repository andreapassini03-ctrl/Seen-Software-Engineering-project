plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("kotlin-kapt") // Aggiungi questo plugin per Room
}

android {
    namespace = "com.example.progetto_software"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.progetto_software"
        minSdk = 25
        targetSdk = 35
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
        compose = true // Abilita le funzionalità Compose
    }
    composeOptions {
        // Puoi aggiungere qui le opzioni del compilatore Compose se necessario
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // *** UTILIZZA IL BOM PER GESTIRE LE VERSIONI DI COMPOSE ***
    implementation(platform(libs.androidx.compose.bom)) // Assicurati che il BOM sia qui e che sia una versione recente

    // Dipendenze di Compose gestite dal BOM
    implementation(libs.androidx.activity.compose) // L'activity per Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // Questa è la libreria Material 3!

    // Dipendenze non Compose o non gestite dal BOM
    //implementation(libs.androidx.lifecycle.viewmodel.compose) // Se vuoi lifecycle-viewmodel-compose

    implementation("androidx.navigation:navigation-compose:2.8.0") // Verifica la versione più recente e stabile
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.constraintlayout) // Per icone come ArrowBack, ecc.

    // --- Dipendenze di Room ---
    val room_version = "2.6.1" // Definisci la versione di Room qui

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version") // Necessario per l'annotazione processing di Room
    implementation("androidx.room:room-ktx:$room_version") // Per le estensioni Kotlin (coroutine)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Questa è per le icone "Filled" e altre
    implementation("androidx.compose.material:material-icons-extended")
}