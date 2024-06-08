plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // needed for @Parcelize in BarcodeField.kt
    id("org.jetbrains.kotlin.plugin.parcelize")
    alias(libs.plugins.google.gms.google.services)
    // needed for sonarqube analysis
    id("org.sonarqube") version "5.0.0.4638"
}

android {
    namespace = "com.iu.storageroom"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.iu.storageroom"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

sonar {
    properties {
        property("sonar.projectKey", "98jan_storageroom_cab43341-633c-4fab-a4d5-e59b14772719")
        property("sonar.host.url", System.getenv("SONAR_HOST_URL"))
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.play.services.base)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // barcode
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    api("com.google.guava:guava:32.0.1-jre")
    implementation("androidx.preference:preference-ktx:1.2.1")
    // Barcode model
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    // Object feature and model
    implementation("com.google.mlkit:object-detection:17.0.1")
    // json processor
    implementation(libs.jackson.databind)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    //navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
}